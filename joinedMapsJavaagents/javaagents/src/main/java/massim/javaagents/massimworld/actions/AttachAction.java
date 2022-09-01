package massim.javaagents.massimworld.actions;

import eis.iilang.Action;
import eis.iilang.Identifier;
import massim.javaagents.massimworld.map.Direction;

import java.util.List;

public class AttachAction extends MassimAction {

    Direction direction;

    public AttachAction(Direction direction) {
        this.direction = direction;
    }

    @Override
    public Action createEisAction() {
        return new Action("attach", List.of(new Identifier(direction.getSymbol())));
    }
}
