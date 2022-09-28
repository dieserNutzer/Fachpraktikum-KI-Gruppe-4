package massim.javaagents.massimworld.actions;

import eis.iilang.Action;
import eis.iilang.Identifier;
import massim.javaagents.massimworld.map.Direction;

public class DetachAction extends MassimAction{

    Direction direction;

    public DetachAction(Direction direction) {
        this.direction = direction;
    }

    @Override
    public Action createEisAction() {
        return new Action("detach", new Identifier(direction.getSymbol()));
    }
}
