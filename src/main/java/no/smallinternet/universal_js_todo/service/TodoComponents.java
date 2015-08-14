package no.smallinternet.universal_js_todo.service;

import java.util.Map;

public interface TodoComponents<T> {
    T renderTodoApp(Map<String, Object> data, String location);
}
