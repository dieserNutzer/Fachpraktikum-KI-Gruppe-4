package massim.javaagents.massimworld;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Coordinates {

    public static Coordinates ZERO = Coordinates.of(0,0);
    private final int x;
    private final int y;

    private Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static Coordinates of(int x, int y) {
        return new Coordinates(x, y);
    }

    public Coordinates withOffset(Coordinates offset) {
        if (offset != null) {
            return new Coordinates(x + offset.getX(), y + offset.getY());
        } else {
            // TODO handle case
            return Coordinates.of(x, y);
        }
    }

    public Coordinates withOffset(int offsetX, int offsetY) {
        return new Coordinates(this.x + offsetX, y + offsetY);
    }

    public Coordinates minus(Coordinates c) {
        return withOffset(c.inverse());
    }

    public Coordinates inverse() {
        return Coordinates.of(-x, -y);
    }

    public int manhattanDistance(Coordinates other) {
        return Math.abs(x - other.getX()) + Math.abs(y - other.getY());
    }

    public boolean isInverse(Coordinates other) {
        return (x + other.getX() == 0) && (y + other.getY() == 0);
    }

    public boolean haveTogetherZeroSum(Coordinates c1, Coordinates c2, Coordinates c3) {
        return (getX() + c2.getX() + c3.getX() == 0) && (getY() + c2.getY() + c3.getY() == 0);
    }

    public static boolean haveZeroSum(Coordinates c1, Coordinates c2, Coordinates c3) {
        return (c1.getX() + c2.getX() + c3.getX() == 0) && (c1.getY() + c2.getY() + c3.getY() == 0);
    }

    public static Set<Coordinates> getAllRelativeCoordinatesWithinDistance(int distance) {
        Set<Coordinates> coordinates = new HashSet<>();
        for (int i = -distance; i <= distance; i++) {
            int k;
            if (i < 0) {
                k = distance + i;
            } else {
                k = distance - i;
            }

            for (int j = -k; j <= k; j++) {
                coordinates.add(Coordinates.of(i, j));
            }
        }
        return coordinates;
    }

    public static Set<Coordinates> getAllCoordinatesWithinDistance(Coordinates position, int distance) {
        return getAllRelativeCoordinatesWithinDistance(distance).stream()
                .map(c -> c.withOffset(position))
                .collect(Collectors.toSet());
    }



    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return " Coor(" + x + "," + y + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
