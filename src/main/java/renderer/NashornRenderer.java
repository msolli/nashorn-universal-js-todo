package renderer;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import javax.script.ScriptException;

import jdk.nashorn.api.scripting.NashornScriptEngine;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import jdk.nashorn.api.scripting.URLReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.ThreadLocal.withInitial;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

public class NashornRenderer<T, S> implements JsRenderer<T, S> {

    private static final Logger LOG = LoggerFactory.getLogger(NashornRenderer.class);
    private static final String[] NASHORN_OPTS = {"--persistent-code-cache", "--optimistic-types"};

    private final ExecutorService pool;
    private final AtomicLong lastModified = new AtomicLong();
    private final Class<T> clazz;
    private final List<File> jsFiles;
    private final S defaultReturnValue;
    private final boolean isReloadingEnabled;
    private final long timeout;
    private final TimeUnit timeoutUnit;
    private final Optional<String> jsNamespace;
    private final AtomicReference<T> proxyObject;

    public static class Builder<T, S> {
        // Required parameters
        private final Class<T> clazz;
        private final List<File> jsFiles;
        private final S defaultReturnValue;

        // Optional parameters
        private Optional<String> jsNamespace = Optional.ofNullable(null);
        private boolean isReloadingEnabled = false;
        private long timeout = 0;
        private TimeUnit timeoutUnit = TimeUnit.MILLISECONDS;
        private int poolSize = Runtime.getRuntime().availableProcessors() + 1;

        // TODO More? logger/debug, stats, nashorn opts, thread pool size...

        public Builder(Class<T> clazz, List<File> jsFiles, S defaultReturnValue) {
            this.clazz = clazz;
            this.jsFiles = jsFiles;
            this.defaultReturnValue = defaultReturnValue;
        }


        public Builder enableReloading() {
            this.isReloadingEnabled = true;
            return this;
        }

        public Builder timeout(long timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder timeout(long timeout, TimeUnit timeoutUnit) {
            this.timeout = timeout;
            this.timeoutUnit = timeoutUnit;
            return this;
        }

        public Builder jsNamespace(String ns) {
            this.jsNamespace = Optional.ofNullable(ns);
            return this;
        }

        public Builder poolSize(int poolSize) {
            this.poolSize = poolSize;
            return this;
        }

        public T build() {
            return new NashornRenderer<>(this).proxyObject.get();
        }
    }

    private NashornRenderer(Builder<T, S> builder) {
        clazz = builder.clazz;
        jsFiles = builder.jsFiles;
        defaultReturnValue = builder.defaultReturnValue;
        isReloadingEnabled = builder.isReloadingEnabled;
        timeout = builder.timeout;
        timeoutUnit = builder.timeoutUnit;
        jsNamespace = builder.jsNamespace;
        pool = Executors.newFixedThreadPool(builder.poolSize);
        proxyObject = new AtomicReference<>(noopProxy());
        loadJs();
    }

    private T noopProxy() {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new InvocationHandler() {
            @Override
            public Supplier<S> invoke(Object proxy, Method method, Object[] args) throws Throwable {
                LOG.info("{}: Nashorn not ready yet", method.getName());
                return () -> defaultReturnValue;
            }
        });
    }

    private void loadJs() {
        try {
            do {
                try {
                    final ThreadLocal<T> jsInterface = withInitial(this::getInterface);
                    final InvocationHandler handler = new JsInvocationHandler(jsInterface);
                    proxyObject.set((T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, handler));
                } catch (RuntimeException e) {
                    LOG.error("Could not load JS", e);
                }
                Thread.sleep(200);
            } while (isReloadingEnabled && filesChanged());
        } catch (InterruptedException e) {
            LOG.info("Nashorn interrupted, will exit.");
        } catch (RuntimeException e) {
            LOG.error("Could not init Nashorn", e);
        }
    }

    private T getInterface() {
        final NashornScriptEngine engine = initEngine();
        return jsNamespace
                .map(ns -> engine.getInterface(engine.get(ns), clazz))
                .orElseGet(() -> engine.getInterface(clazz));
    }

    private NashornScriptEngine initEngine() {
        final long startTime = System.nanoTime();
        LOG.info("Initializing Nashorn");
        final NashornScriptEngine engine = getEngine();
        jsFiles.forEach(f -> load(engine, f));
        logTiming("Nashorn initialized", System.nanoTime() - startTime);
        return engine;
    }

    private static NashornScriptEngine getEngine() {
        final NashornScriptEngineFactory engineFactory = new NashornScriptEngineFactory();

        try {
            return (NashornScriptEngine) engineFactory.getScriptEngine(NASHORN_OPTS);
        } catch (IllegalArgumentException e) {
            return (NashornScriptEngine) engineFactory.getScriptEngine();
        }
    }

    private boolean filesChanged() {
        final Long newLastModified = jsFiles.stream()
                .filter((f) -> f.length() > 0)
                .mapToLong(File::lastModified)
                .max()
                .orElse(0);
        return (newLastModified > lastModified.getAndSet(newLastModified));
    }

    private static void load(NashornScriptEngine engine, File f) {
        try {
            LOG.debug("Loading JS file {}", f.getName());
            engine.eval(new URLReader(f.toURI().toURL()));
        } catch (IOException | ScriptException e) {
            throw new RuntimeException("Error loading JS file " + f.getName(), e);
        }
    }

//    @PreDestroy
//    public void shutdownExecutor() {
//        pool.shutdownNow();
//    }

    private class JsInvocationHandler implements InvocationHandler {
        private final ThreadLocal<T> jsInterface;

        public JsInvocationHandler(ThreadLocal<T> jsInterface) {
            this.jsInterface = jsInterface;
        }

        @Override
        public Supplier<S> invoke(Object proxy, Method method, Object[] args) throws Throwable {
            final String methodName = method.getName();
            LOG.info("Submitting {} for rendering", methodName);

            final Future<S> future;
            try {
                future = pool.submit(() -> {
                    final long startTime = System.nanoTime();
                    try {
                        S result = (S) method.invoke(jsInterface.get(), args);
                        logTiming(methodName + " finished", System.nanoTime() - startTime);
                        return result;
                    } catch (NullPointerException e) {
                        LOG.error("Could not match interface with JS functions");
                    } catch (Exception e) {
                        LOG.error("Could not render JS - TODO catch only relevant exceptions here", e.getMessage());
                        throw e;
                    }
                    logTiming(methodName + " failed", System.nanoTime() - startTime);
                    return defaultReturnValue;
                });
            } catch (RejectedExecutionException e) {
                LOG.warn("Could not submit JS for rendering: {}", e.getMessage());
                return () -> defaultReturnValue;
            }

            try {
                if (timeout > 0) {
                    final S result = future.get(timeout, timeoutUnit);
                    return () -> result;
                } else {
                    final S result = future.get();
                    LOG.debug("YOYO: {}", result);
                    return () -> result;
                }
            } catch (InterruptedException e) {
                LOG.error("Rendering interrupted", e);
                Thread.currentThread().interrupt();
            } catch (ExecutionException e) {
                LOG.error("Could not render JS", e);
            } catch (TimeoutException e) {
                LOG.warn("{} timed out", method);
            }
            return () -> defaultReturnValue;
        }
    }

    private static void logTiming(String msg, long elapsedTime) {
        LOG.info("{}: {} ms", msg, NANOSECONDS.toMillis(elapsedTime));
    }
}
