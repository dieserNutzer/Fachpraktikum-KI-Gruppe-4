package massim.javaagents.massimworld.percepts.agent;

import eis.iilang.Percept;
import massim.javaagents.massimworld.agent.AgentState;

public class LastActionPercept extends AgentPercept {

    private final String action;

    public LastActionPercept(Percept percept) {
        this.action = readString(percept, 0);
    }

    @Override
    public void updateAgentState(AgentState agentState) {
        agentState.setLastAction(action);
    }
}
