package massim.javaagents.massimworld.map.things;

public class Obstacle extends Thing {

    private static final Obstacle obstacle = new Obstacle();

    public static Obstacle obstacle() {
        return obstacle;
    }

    @Override
    public boolean isObstacle() {
        return true;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return o instanceof Obstacle;
    }
}
