package renderer;

public interface JsComponent {
    String getId();

    JsBundle getBundle();

    String getRenderStatement();
}
