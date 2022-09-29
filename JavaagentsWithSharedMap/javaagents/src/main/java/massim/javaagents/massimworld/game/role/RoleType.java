package massim.javaagents.massimworld.game.role;

/**
 * Describes the given role types of a massim simulation and their names, used in eis.lang.Percept.
 */
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
