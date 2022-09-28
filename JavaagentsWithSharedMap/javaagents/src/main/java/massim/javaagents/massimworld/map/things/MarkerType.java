package massim.javaagents.massimworld.map.things;

import massim.javaagents.massimworld.actions.ActionType;

public enum MarkerType {

    CLEAR("clear"),        // the cell is about to be cleared
    CLEAR_IMMEDIATE("ci"), // "clear_immediate" - a clear event will clear the cell in 2 steps or less
    CLEAR_PERIMETER("cp"); // "clear_perimeter" - the cell is in the perimeter of a clear event (i.e. new obstacles may be generated there as part of the event)

    private final String typeName;

    MarkerType(String typeName) {
        this.typeName = typeName;
    }

    public static MarkerType getByTypeName(String typeName) {
        return switch (typeName) {
            case "clear" -> CLEAR;
            case "ci" -> CLEAR_IMMEDIATE;
            case "cp" -> CLEAR_PERIMETER;
            default -> throw new IllegalArgumentException("unknown marker type name " + typeName);
        };
    }

}
