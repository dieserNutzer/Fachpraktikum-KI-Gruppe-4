package massim.javaagents.massimworld.percepts.agent;

import eis.iilang.Percept;
import massim.javaagents.massimworld.agent.AgentState;

public class RolePercept extends AgentPercept {

    String roleType;

    public RolePercept(Percept percept) {
        this.roleType = readString(percept,0);
    }

    @Override
    public void updateAgentState(AgentState agentState) {

    }
}
