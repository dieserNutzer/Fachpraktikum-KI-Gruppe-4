package massim.javaagents.massimworld.game.role;

import massim.javaagents.massimworld.map.Direction;

public enum RoleType {

    DEFAULT("default"),
    WORKER("worker"),
    CONSTRUCTOR("constructor"),
    EXPLORER("explorer"),
    DIGGER("digger");

    private String name;

    RoleType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static RoleType getByName(String name) {
        return switch (name) {
            case "default" -> DEFAULT;
            case "worker" -> WORKER;
            case "constructor" -> CONSTRUCTOR;
            case "explorer" -> EXPLORER;
            case "digger" -> DIGGER;
            default -> throw new IllegalArgumentException("unknown role type " + name);
        };
    }
}
