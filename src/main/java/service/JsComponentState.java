package service;

import java.util.Map;

public final class JsComponentState {
    private static final String NO_LOCATION = "";
    private final Map data;
    private final String location;

    public JsComponentState(Map<String, Object> data, String location) {
        this.data = data;
        this.location = location;
    }

    public JsComponentState(Map<String, Object> data) {
        this.data = data;
        this.location = NO_LOCATION;
    }

    public Map getData() {
        return data;
    }

    public String getLocation() {
        return location;
    }
}
