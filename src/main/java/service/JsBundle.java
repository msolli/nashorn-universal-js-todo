package service;

enum JsBundle {

    CONTRACTOR("contractor"),
    OWNER("owner");

    private final String id;

    JsBundle(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getBundlePath() {
        return "/jsdist/" + this.id + ".js";
    }
}
