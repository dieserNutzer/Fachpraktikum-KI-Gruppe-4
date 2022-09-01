package massim.javaagents.massimworld.actions;

import eis.iilang.Action;
import eis.iilang.Identifier;
import massim.javaagents.massimworld.map.Direction;

import java.util.List;

public class RequestAction extends MassimAction {
    Direction direction;

    public RequestAction(Direction direction) {
        this.direction = direction;
    }

    @Override
    public Action createEisAction() {
        return new Action("request", List.of(new Identifier(direction.getSymbol())));
    }
}
