package massim.javaagents.massimworld.actions;

public enum ActionType {

    SKIP("skip"),
    MOVE("move"),
    ATTACH("attach"),
    DETACH("detach"),
    ROTATE("rotate"),
    CONNECT("connect"),
    DISCONNECT("disconnect"),
    REQUEST("request"),
    SUBMIT("submit"),
    CLEAR("clear"),
    ADOPT("adapt"),
    SURVEY("survey"),
    NO_ACTION("no_action");

    private String name;

    ActionType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ActionType getByName(String name) {
        return switch (name) {
            case "skip" -> SKIP;
            case "move" -> MOVE;
            case "attach" -> ATTACH;
            case "detach" -> DETACH;
            case "rotate" -> ROTATE;
            case "connect" -> CONNECT;
            case "disconnect" -> DISCONNECT;
            case "request" -> REQUEST;
            case "submit" -> SUBMIT;
            case "clear" -> CLEAR;
            case "adapt" -> ADOPT;
            case "survey" -> SURVEY;
            case "no_action" -> NO_ACTION;
            default -> throw new IllegalArgumentException("unknown action type " + name);
        };
    }
}
