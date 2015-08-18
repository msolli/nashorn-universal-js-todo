package renderer;

import java.util.function.Supplier;

import static renderer.Util.capitalize;

/*
* View model for rendered JS component. Handles output of:
*   - rendered JS
*   - its container element (with id)
*   - init statement for client (with data as JSON)
*   - path for bundle containing JS code for the client?
* */

public final class JsModel {
    private final String id;
    private final String ns;
    private final JsComponentState state;
    private final Supplier<String> rendered;

    private JsModel(String id, String ns, JsComponentState state, Supplier<String> rendered) {
        this.id = id;
        this.ns = ns;
        this.state = state;
        this.rendered = rendered;
    }

    public String getContainerWithMarkup() {
        return "<div id=\"" + id + "\">" + rendered.get() + "</div>";
    }

    /**
     * Returns a JavaScript statement that can be used in view template to initialize a JS module.
     * <p>
     * Example output: TODO.initTodoApp({}, document.getElementById("todoApp"));
     *
     * @return a JS statement
     */
    public String getInitFnCall() {
        return ns + "." + getInitFn() + "(" + getInitFnArgs() + ");";
    }

    public static JsModel createModelWithState(String id, String ns, String data, String location, Supplier<String> rendered) {
        return new JsModel(id, ns, new JsComponentState(data, location), rendered);
    }

    public String getInitFn() {
        return "init" + capitalize(id);
    }

    public String getInitFnArgs() {
        return state.getData() + ", " + "document.getElementById(\"" + id + "\")";
    }
}
