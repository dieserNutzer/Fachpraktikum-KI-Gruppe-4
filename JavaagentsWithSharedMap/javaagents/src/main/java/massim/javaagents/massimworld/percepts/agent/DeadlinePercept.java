package massim.javaagents.massimworld.percepts.agent;

import eis.iilang.Percept;
import massim.javaagents.massimworld.agent.AgentState;

public class DeadlinePercept extends AgentPercept {

    private final long deadline;

    public DeadlinePercept(Percept percept) {
        deadline = readInt(percept, 0);
    }

    @Override
    public void updateAgentState(AgentState agentState) {
        agentState.setDeadline(deadline);
    }
}

