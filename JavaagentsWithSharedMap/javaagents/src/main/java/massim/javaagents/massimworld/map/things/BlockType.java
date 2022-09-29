package massim.javaagents.massimworld.map.things;

/**
 * Enumerates the different block types of the massim simulation.
 */
public enum BlockType {

    B0("b0"),
    B1("b1"),
    B2("b2");

    private final String typeName;

    BlockType(String typeName) {
        this.typeName = typeName;
    }

    public static BlockType getByTypeName(String typeName) {
        return switch (typeName) {
            case "b0" -> B0;
            case "b1" -> B1;
            case "b2" -> B2;
            default -> throw new IllegalArgumentException("unknown block type name " + typeName);
        };
    }

}
