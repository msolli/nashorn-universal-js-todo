package service;

import java.util.function.Supplier;

public interface JsRenderer<T> {
//    Supplier<String> render(JsComponent component, JsComponentState state);

    T getProxyObject();
}
