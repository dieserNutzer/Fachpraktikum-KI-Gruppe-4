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

    public static Direction getOrthogonalDirectionClockwise(Direction direction) {
        return switch (direction) {
            case EAST -> SOUTH;
            case SOUTH -> WEST;
            case WEST -> NORTH;
            case NORTH -> EAST;
        };
    }

    public static Direction getOrthogonalDirectionCounterClockwise(Direction direction) {
        return switch (direction) {
            case EAST -> NORTH;
            case SOUTH -> EAST;
            case WEST -> SOUTH;
            case NORTH -> WEST;
        };
    }

    public static Direction getOppositeDirection(Direction direction) {
        return switch (direction) {
            case WEST -> EAST;
            case NORTH -> SOUTH;
            case EAST -> WEST;
            case SOUTH -> NORTH;
        };
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
            case "e" -> EAST;
            case "s" -> SOUTH;
            case "w" -> WEST;
            case "n" -> NORTH;
            default -> throw new IllegalArgumentException("unknown direction " + symbol);
        };
    }

    public static Direction getByIntValue(int number) {
        return switch (number) {
            case 0 -> EAST;
            case 1 -> SOUTH;
            case 2 -> WEST;
            case 3 -> NORTH;
            default -> throw new IllegalArgumentException("unknown direction int " + number);
        };
    }

    public static Direction getByOffset(Coordinates coordinates) {
            if (Coordinates.of(1,0).equals(coordinates)) return EAST;
            if ( Coordinates.of(0, 1).equals(coordinates)) return SOUTH;
            if (Coordinates.of(-1, 0).equals(coordinates)) return WEST;
            if ( Coordinates.of(0, -1).equals(coordinates)) return NORTH;
            throw new IllegalArgumentException("unknown direction offset " + coordinates);
    }


}
