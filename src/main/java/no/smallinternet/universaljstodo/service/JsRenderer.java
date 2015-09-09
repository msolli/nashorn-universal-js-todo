package no.smallinternet.universaljstodo.service;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.PreDestroy;
import javax.servlet.ServletContext;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import no.smallinternet.universaljstodo.domain.TodoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import renderer.JsModel;
import renderer.JsReloader;
import renderer.NashornExecutor;

import static java.lang.Integer.min;
import static java.lang.Runtime.getRuntime;
import static java.util.Arrays.stream;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.stream.Collectors.toList;
import static renderer.NashornRenderer.builder;

@Service
public class JsRenderer implements WarmupListener {

    private static final Logger LOG = LoggerFactory.getLogger(JsRenderer.class);
    private static final String[] JS_FILES = {"/dist/todo-server.js"};
    private static final String JS_NAMESPACE = "TODO";
    private final ExecutorService pool;
    private final NashornExecutor<JsComponents> executor;
    private final AtomicReference<WarmupResult> lastWarmupResult = new AtomicReference<>();

    @Autowired
    public JsRenderer(ServletContext context, TodoRepository repository) {
        final int maximumPoolSize = min(getRuntime().availableProcessors(), 4);
        LOG.info("Initializing JsRenderer service - maxRenderThreads: {}", maximumPoolSize);

        pool = new ThreadPoolExecutor(1, maximumPoolSize, 4, HOURS, new ArrayBlockingQueue<>(2),
                new ThreadFactoryBuilder().setDaemon(true).setNameFormat("nashorn-renderer-%d").build(),
                (a, b) -> LOG.warn("JS execution rejected"));

        executor = new NashornExecutor<>(setupSupplier(getJsFiles(context::getRealPath), true), pool);
        JsWarmerUpper.create(executor, repository).targetDuration(0, MILLISECONDS).maxRuns(10000).onComplete(this).run();
    }

    private static Supplier<JsComponents> setupSupplier(Collection<File> jsFiles, boolean reload) {
        final Supplier<JsComponents> supplier = builder(JsComponents.class, jsFiles)
                .jsNamespace(JS_NAMESPACE)
                .build();
        return reload ? new JsReloader<>(JsComponents.class, supplier, jsFiles) : supplier;
    }

    private static List<File> getJsFiles(Function<String, String> pathFn) {
        return stream(JS_FILES)
                .map(pathFn)
                .map(File::new)
                .collect(toList());
    }

    public JsModel renderTodoApp(String data) {
        return new JsModel("todoApp", JS_NAMESPACE, data,
                executor.render(withTiming(todo -> todo.renderTodoApp(data), "renderTodoApp"), "", 10));
    }

    private <T, S> Function<T, S> withTiming(Function<T, S> fn, String name) {
        return t -> {
            final long start = System.nanoTime();
            try {
                return fn.apply(t);
            } finally {
                long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
                LOG.info("{}: {} ms", name, elapsed);
            }
        };
    }

    @PreDestroy
    public void shutdownExecutor() {
        pool.shutdownNow();
    }

    @Override
    public void warmupComplete(WarmupResult result) {
        lastWarmupResult.set(result);
    }

    public WarmupResult getLastWarmupResult() {
        return lastWarmupResult.get();
    }
}
