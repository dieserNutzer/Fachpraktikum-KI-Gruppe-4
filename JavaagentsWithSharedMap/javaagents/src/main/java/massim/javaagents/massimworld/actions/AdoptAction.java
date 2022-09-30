package massim.javaagents.massimworld.actions;

import eis.iilang.Action;
import eis.iilang.Identifier;
import massim.javaagents.massimworld.game.role.RoleType;

public class AdoptAction extends MassimAction {

    private final RoleType roleType;

    public AdoptAction(RoleType roleType) {
        this.roleType = roleType;
    }

    @Override
    public Action createEisAction() {
        return new Action(ActionType.ADOPT.getName(), new Identifier(roleType.getName())) ;
    }

    @Override
    public String toString() {
        return "AdoptAction{" +
                "roleType=" + roleType +
                '}';
    }
}
