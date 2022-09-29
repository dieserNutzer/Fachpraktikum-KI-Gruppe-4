package massim.javaagents.massimworld.actions;

import eis.iilang.Action;
import eis.iilang.Identifier;
import massim.javaagents.massimworld.map.Coordinates;
import massim.javaagents.massimworld.map.Direction;

import java.util.List;

/**
 * Action for moving one step in the given direction.
 */
public class MoveAction extends MassimAction {
    private final Direction direction;

    public MoveAction(Direction direction) {
        this.direction = direction;
    }

    @Override
    public Coordinates getMoveOffset() {
        if (true) {
            switch (direction) {
                case EAST: {
                    return Coordinates.of(1, 0);
                }
                case SOUTH: {
                    return Coordinates.of(0, 1);
                }
                case WEST: {
                    return Coordinates.of(-1, 0);
                }
                case NORTH: {
                    return Coordinates.of(0, -1);
                }
                default: throw new IllegalStateException("MoveAction");
            }
        } else {
            return Coordinates.ZERO;
        }
    }

    public Direction getDirection() {
        return direction;
    }

    public Coordinates applyToCoordinates(Coordinates coordinates) {
        return coordinates.withOffset(getMoveOffset());
    }

    @Override
    public Action createEisAction() {
        return new Action("move", List.of(new Identifier(direction.getSymbol())));
    }

    @Override
    public String toString() {
        return "MoveAction{" +
                "direction=" + direction +
                '}';
    }
}
