package massim.javaagents.massimworld.map;

import java.util.Random;

public enum Direction {

    EAST("e"),
    SOUTH("s"),
    WEST("w"),
    NORTH("n");

    private final String symbol;

    Direction(String symbol) {
        this.symbol = symbol;
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
