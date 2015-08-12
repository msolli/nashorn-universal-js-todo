package todo;

import java.util.Map;

public interface TodoComponents<T> {
    T renderTodoApp(Map<String, Object> data, String location);
}
