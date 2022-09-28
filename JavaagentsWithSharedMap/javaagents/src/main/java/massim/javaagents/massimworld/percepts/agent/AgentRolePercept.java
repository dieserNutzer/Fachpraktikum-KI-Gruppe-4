package massim.javaagents.massimworld.percepts.agent;

import eis.iilang.Percept;
import massim.javaagents.massimworld.agent.AgentState;
import massim.javaagents.massimworld.game.Game;
import massim.javaagents.massimworld.game.role.RoleType;

public class AgentRolePercept extends AgentPercept {

    RoleType roleType;

    public AgentRolePercept(Percept percept) {
        String roleName = readString(percept,0);
        this.roleType = RoleType.getByName(roleName);
    }

    @Override
    public void updateAgentState(AgentState agentState) {
        agentState.setRole(Game.game().getGameRoles().getGameRoleByType(roleType));
    }

}
