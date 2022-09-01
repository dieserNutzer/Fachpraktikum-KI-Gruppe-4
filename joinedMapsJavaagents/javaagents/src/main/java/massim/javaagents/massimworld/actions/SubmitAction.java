package massim.javaagents.massimworld.actions;

import eis.iilang.Action;
import eis.iilang.Identifier;

import java.util.List;

public class SubmitAction extends MassimAction {
    @Override
    public Action createEisAction() {
        return new Action("submit");
    }
}
