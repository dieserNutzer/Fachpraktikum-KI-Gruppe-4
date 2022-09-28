package massim.javaagents.massimworld.actions;

import eis.iilang.Action;

public class SkipAction extends MassimAction {

    @Override
    public Action createEisAction() {
        return new Action("skip");
    }
}
