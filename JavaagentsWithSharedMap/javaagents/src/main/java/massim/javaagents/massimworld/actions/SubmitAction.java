package massim.javaagents.massimworld.actions;

import eis.iilang.Action;
import eis.iilang.Identifier;

import java.util.List;

public class SubmitAction extends MassimAction {

    String taskName;

    public SubmitAction(String taskName) {
        this.taskName = taskName;
    }

    @Override
    public Action createEisAction() {
        return new Action("submit", List.of(new Identifier(taskName)));
    }
}
