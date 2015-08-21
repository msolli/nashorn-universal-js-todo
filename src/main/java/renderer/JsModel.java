package renderer;

import java.util.function.Supplier;

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
    private final String data;
    private final Supplier<String> rendered;

    public JsModel(String id, String ns, String data, Supplier<String> rendered) {
        this.id = id;
        this.ns = ns;
        this.data = data;
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

    public String getInitFn() {
        return "init" + capitalize(id);
    }

    public String getInitFnArgs() {
        return data + ", " + "document.getElementById(\"" + id + "\")";
    }

    private static String capitalize(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }
        return new StringBuilder(strLen)
                .append(Character.toTitleCase(str.charAt(0)))
                .append(str.substring(1))
                .toString();
    }
}
