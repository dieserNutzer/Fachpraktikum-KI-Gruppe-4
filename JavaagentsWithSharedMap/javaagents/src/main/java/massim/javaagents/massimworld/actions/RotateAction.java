package massim.javaagents.massimworld.actions;

import eis.iilang.Action;
import eis.iilang.Identifier;

import java.util.List;

public class RotateAction extends MassimAction {

    Rotation rotation;

    public RotateAction(Rotation rotation) {
        this.rotation = rotation;
    }

    @Override
    public Action createEisAction() {
        return new Action("rotate", new Identifier(rotation.getSymbol()));
    }

    @Override
    public String toString() {
        return "RotateAction{" +
                "rotation=" + rotation +
                '}';
    }
}
