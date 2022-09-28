package massim.javaagents.massimworld.percepts.agent;

import eis.iilang.Percept;
import massim.javaagents.massimworld.agent.AgentState;

public class ActionIdPercept extends AgentPercept {

    private long actionId;

    public ActionIdPercept(Percept percept) {
        actionId = readInt(percept, 0);
    }

    @Override
    public void updateAgentState(AgentState agentState) {
        agentState.setActionId(actionId);
    }
}

