package massim.javaagents.massimworld.actions;

public enum Rotation {

    CW("cw", "clockwise"),
    CCW("ccw", "counterclockwise");

    private final String symbol;

    private final String description ;

    Rotation(String symbol, String description) {
        this.symbol = symbol;
        this.description = description;
    }

    @Override
    public String toString() {
        return symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getDescription() {
        return description;
    }

    public static Rotation getOppositeRotation(Rotation rotation) {
        if (rotation == CW) {
            return CCW;
        } else {
            return CW;
        }
    }

    public static Rotation getBySymbol(String symbol) {
        return switch (symbol) {
            case "cw" -> CW;
            case "ccw" -> CCW;
            default -> throw new IllegalArgumentException("unknown rotation " + symbol);
        };
    }

}
