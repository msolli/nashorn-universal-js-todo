package renderer;

public final class JsComponentState {
    private static final String NO_LOCATION = "";
    private final String data;
    private final String location;

    public JsComponentState(String data, String location) {
        this.data = data;
        this.location = location;
    }

    public JsComponentState(String data) {
        this.data = data;
        this.location = NO_LOCATION;
    }

    public String getData() {
        return data;
    }

    public String getLocation() {
        return location;
    }
}
