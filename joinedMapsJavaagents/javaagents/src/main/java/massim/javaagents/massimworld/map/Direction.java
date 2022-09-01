package massim.javaagents.massimworld.map;

import java.util.Random;

public enum Direction {

    EAST("e", Coordinates.of(1,0)),
    SOUTH("s", Coordinates.of(0, 1)),
    WEST("w", Coordinates.of(-1, 0)),
    NORTH("n", Coordinates.of(0, -1));

    private static int current = 0;

    private final String symbol;

    private final Coordinates offset;

    Direction(String symbol, Coordinates offset) {
        this.symbol = symbol;
        this.offset = offset;
    }

    public static Direction getRandomDirection() {
        return getByIntValue((new Random(System.currentTimeMillis())).nextInt(4));
    }

    @Override
    public String toString() {
        return symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public Coordinates getOffset() {
        return offset;
    }

    public static Direction getNext() {
       current = current == 3 ? 0 : current +1;
        return getByIntValue(current);
    }

    public static Direction getBySymbol(String symbol) {
        return switch (symbol) {
            case "e" -> Direction.EAST;
            case "s" -> Direction.SOUTH;
            case "w" -> Direction.WEST;
            case "n" -> Direction.NORTH;
            default -> throw new IllegalArgumentException("unknown direction " + symbol);
        };
    }



    public static Direction getByIntValue(int number) {
        return switch (number) {
            case 0 -> Direction.EAST;
            case 1 -> Direction.SOUTH;
            case 2 -> Direction.WEST;
            case 3 -> Direction.NORTH;
            default -> throw new IllegalArgumentException("unknown indirection int " + number);
        };
    }
}
