package massim.javaagents.massimworld.actions;

import eis.iilang.Action;
import eis.iilang.Numeral;
import massim.javaagents.massimworld.map.Direction;

public class ClearAction extends MassimAction {

    // TODO handle list of directions
    private final Direction direction;

    public ClearAction(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    public Action createEisAction() {
        return new Action("clear", new Numeral(direction.getOffset().getX()), new Numeral(direction.getOffset().getY()));
    }
}
