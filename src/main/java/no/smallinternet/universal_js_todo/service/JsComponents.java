package no.smallinternet.universal_js_todo.service;

import java.io.File;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import renderer.JsModel;
import renderer.NashornRenderer;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

@Service
public class JsComponents implements ITodoComponents<JsModel> {

    public interface TodoComponents extends ITodoComponents<Supplier<String>> {}

    private final TodoComponents jsRenderer;

    private static final Logger LOG = LoggerFactory.getLogger(JsComponents.class);
    private static final String[] JS_FILES = {"/dist/todo-server.js"};
    private static final String jsNamespace = "TODO";

    @Autowired
    public JsComponents(ServletContext context) {
        final boolean isReloadingEnabled = true;
        LOG.info("Initializing JsComponents service...");

        List<File> jsFiles = getJsFiles(context::getRealPath);

        NashornRenderer.Builder<TodoComponents, Supplier<String>> builder =
                new NashornRenderer.Builder<>(TodoComponents.class, jsFiles, "")
                        .poolSize(1)
                        .jsNamespace(jsNamespace);

        if (isReloadingEnabled) {
            builder.enableReloading();
        }
        jsRenderer = builder.build();
    }

    private static List<File> getJsFiles(Function<String, String> pathFn) {
        return stream(JS_FILES)
                .map(pathFn)
                .map(File::new)
                .collect(toList());
    }

    @Override
    public JsModel renderTodoApp(String data, String location) {
        return JsModel.createModelWithState("todoApp", jsNamespace, data, location,
                jsRenderer.renderTodoApp(data, location));
    }
}
