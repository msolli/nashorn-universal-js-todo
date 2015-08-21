package no.smallinternet.universal_js_todo.service;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.PreDestroy;
import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import renderer.JsModel;
import renderer.JsReloader;
import renderer.NashornExecutor;
import renderer.NashornRenderer;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

@Service
public class JsComponents {

    private static final Logger LOG = LoggerFactory.getLogger(JsComponents.class);
    private static final String[] JS_FILES = {"/dist/todo-server.js"};
    private static final String jsNamespace = "TODO";
    private final ExecutorService pool;
    private final NashornExecutor<TodoComponents> executor;

    @Autowired
    public JsComponents(ServletContext context) {
        LOG.info("Initializing JsComponents service...");

        List<File> jsFiles = getJsFiles(context::getRealPath);

        this.pool = Executors.newFixedThreadPool(2);

        final Supplier<TodoComponents> s = NashornRenderer.builder(TodoComponents.class, jsFiles)
                .jsNamespace(jsNamespace).build();
        final JsReloader<TodoComponents> reloader = new JsReloader<>(TodoComponents.class, s, jsFiles);
        executor = new NashornExecutor<>(reloader, pool, 1000);
    }

    private static List<File> getJsFiles(Function<String, String> pathFn) {
        return stream(JS_FILES)
                .map(pathFn)
                .map(File::new)
                .collect(toList());
    }

    public JsModel renderTodoApp(String data, String path, String queryString) {
        return new JsModel("todoApp", jsNamespace, data,
                executor.render(withTiming(todo -> todo.renderTodoApp(data, path, queryString), "renderTodoApp"), ""));
    }

    private <T, S> Function<T, S> withTiming(Function<T, S> fn, String name) {
        return t -> {
            final long start = System.currentTimeMillis();
            try {
                return fn.apply(t);
            } finally {
                LOG.info("{}: {} ms", name, System.currentTimeMillis() - start);
            }
        };
    }

    @PreDestroy
    public void shutdownExecutor() {
        pool.shutdownNow();
    }
}
