package service;

import java.util.function.Supplier;

import static service.Util.*;

public final class JsModel {
    private final JsComponentImpl component;
    private final JsComponentState state;
    private final Supplier<String> rendered;

    public JsModel(JsComponentImpl component, JsComponentState state, JsRenderer jsRenderer) {
        this.component = component;
        this.state = state;
        this.rendered = jsRenderer.render(this.component, state);
    }

    public String getId() {
        return component.getId();
    }

    public String getBundlePath() {
        return component.getBundle().getBundlePath();
    }

    public String getContainerWithMarkup() {
        return "<div id=\"" + getId() + "\">" + this.rendered.get() + "</div>";
    }

    /**
     * Returns a JavaScript require() statement that can be used in JSP to require and initialize a JS module.
     *
     * Example output: FINN.oppdrag.initMotivation({}, document.getElementById("motivation"));
     *
     * @return a JS statement
     * */
    public String getInitFnCall() {
        return "FINN.oppdrag." + component.getInitFn() + "(" + getInitFnArgs() + ");";
    }

    private String getInitFnArgs() {
        return toJson(state.getData()) + ", " + component.getDomSelector();
    }

}
