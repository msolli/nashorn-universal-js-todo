package renderer;

public interface JsRenderer<T> {
//    Supplier<String> render(JsComponent component, JsComponentState state);

    T getProxyObject();
}
