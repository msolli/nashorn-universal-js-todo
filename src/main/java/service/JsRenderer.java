package service;

import java.util.function.Supplier;

public interface JsRenderer {
    Supplier<String> render(JsComponent component, JsComponentState state);
}
