package massim.javaagents.massimworld.percepts.agent;

import eis.iilang.Percept;
import massim.javaagents.massimworld.agent.AgentState;

public class TimestampPercept extends AgentPercept {

    private final long timestamp;

    public TimestampPercept(Percept percept) {
        timestamp = readInt(percept, 0);
    }

    @Override
    public void updateAgentState(AgentState agentState) {
        agentState.setTimestamp(timestamp);
    }
}

