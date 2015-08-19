package no.smallinternet.universal_js_todo.service;

public interface ITodoComponents<S> {
    S renderTodoApp(String data, String path, String queryString);
    // ...more JS methods
}
