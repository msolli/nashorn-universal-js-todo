package renderer;

import static renderer.Util.capitalize;

public enum JsComponentImpl implements JsComponent {

    MOTIVATION("motivation", JsBundle.OWNER),
    MY_COMPANY("myCompany", JsBundle.CONTRACTOR);

    public static final String GLOBAL_PROPS_NAME = "props";
    public static final String GLOBAL_LOCATION_NAME = "location";

    static final String NAMESPACE = "FINN.oppdrag";

    private final String id;
    private final JsBundle bundle;
    private final String initFn;
    private final String renderFn;
    private final String domSelector;

    JsComponentImpl(String id, JsBundle bundle) {
        this.id = id;
        this.bundle = bundle;
        this.initFn = "init" + capitalize(id);
        this.renderFn = "render" + capitalize(id);
        this.domSelector = "document.getElementById(\"" + id + "\")";
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public JsBundle getBundle() {
        return bundle;
    }

    public String getInitFn() {
        return initFn;
    }

    @Override
    public String getRenderStatement() {
        return NAMESPACE + "." + renderFn + "()";
    }


    public String getDomSelector() {
        return domSelector;
    }
}
