package massim.javaagents.massimworld.actions;

import eis.iilang.Action;
import eis.iilang.Identifier;
import massim.javaagents.massimworld.map.Direction;

public class AttachAction extends MassimAction {

    Direction direction;

    public AttachAction(Direction direction) {
        this.direction = direction;
    }

    @Override
    public Action createEisAction() {
        return new Action("attach", new Identifier(direction.getSymbol()));
    }

    @Override
    public String toString() {
        return "AttachAction{" +
                "direction=" + direction +
                '}';
    }
}
