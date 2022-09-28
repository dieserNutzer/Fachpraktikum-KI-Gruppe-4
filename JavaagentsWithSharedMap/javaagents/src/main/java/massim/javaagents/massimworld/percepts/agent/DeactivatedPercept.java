package massim.javaagents.massimworld.percepts.agent;

import eis.iilang.Percept;
import massim.javaagents.massimworld.agent.AgentState;

public class DeactivatedPercept extends AgentPercept {
    private final boolean deactivated;

    public DeactivatedPercept(Percept percept) {
        this.deactivated = readBoolean(percept, 0);
    }

    @Override
    public void updateAgentState(AgentState agentState) {
        agentState.setDeactivated(deactivated);
    }
}
