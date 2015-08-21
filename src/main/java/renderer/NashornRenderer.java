package renderer;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import javax.script.ScriptException;

import jdk.nashorn.api.scripting.NashornScriptEngine;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import jdk.nashorn.api.scripting.URLReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Optional.ofNullable;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

public class NashornRenderer<T> implements Supplier<T> {

    private static final Logger LOG = LoggerFactory.getLogger(NashornRenderer.class);
    private static final String[] NASHORN_OPTS = {"--persistent-code-cache", "--optimistic-types"};

    private final Class<T> clazz;
    private final List<File> jsFiles;
    private final Optional<String> jsNamespace;

    @Override
    public T get() {
        return ofNullable(getInterface()).orElseThrow(() -> new IllegalStateException("Could not create instance of " + clazz));
    }

    public static <T> Builder<T> builder(Class<T> clazz, List<File> jsFiles) {
        return new NashornRenderer.Builder<>(clazz, jsFiles);
    }

    public static class Builder<T> {
        // Required parameters
        private final Class<T> clazz;
        private final List<File> jsFiles;

        // Optional parameters
        private Optional<String> jsNamespace = Optional.empty();

        public Builder(Class<T> clazz, List<File> jsFiles) {
            this.clazz = clazz;
            this.jsFiles = jsFiles;
        }


        public Builder<T> jsNamespace(String ns) {
            this.jsNamespace = ofNullable(ns);
            return this;
        }

        public Supplier<T> build() {
            return new NashornRenderer<>(this);
        }
    }

    private NashornRenderer(Builder<T> builder) {
        clazz = builder.clazz;
        jsFiles = builder.jsFiles;
        jsNamespace = builder.jsNamespace;
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

    private static void load(NashornScriptEngine engine, File f) {
        try {
            LOG.debug("Loading JS file {}", f.getName());
            engine.eval(new URLReader(f.toURI().toURL()));
        } catch (IOException | ScriptException e) {
            throw new RuntimeException("Error loading JS file " + f.getName(), e);
        }
    }


    private static void logTiming(String msg, long elapsedTime) {
        LOG.info("{}: {} ms", msg, NANOSECONDS.toMillis(elapsedTime));
    }
}
