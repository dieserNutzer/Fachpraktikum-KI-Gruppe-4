package massim.javaagents.massimworld.percepts.agent;

import eis.iilang.Percept;
import massim.javaagents.massimworld.agent.AgentState;

public class AgentNamePercept extends AgentPercept {

    private final String agentName;

    public AgentNamePercept(Percept percept) {
        agentName = readString(percept, 0);
    }

    @Override
    public void updateAgentState(AgentState agentState) {
        agentState.setAgentName(agentName);
    }
}
