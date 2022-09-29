package massim.javaagents.massimworld.actions;

import eis.iilang.Action;

import static massim.javaagents.massimworld.actions.ActionType.NO_ACTION;

public class NoActionAction extends MassimAction {

    public NoActionAction() {
    }

    @Override
    public Action createEisAction() {
        return new Action(NO_ACTION.getName());
    }
}
