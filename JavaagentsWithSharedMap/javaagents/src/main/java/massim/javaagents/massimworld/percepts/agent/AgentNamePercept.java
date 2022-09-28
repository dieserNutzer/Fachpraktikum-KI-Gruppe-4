package massim.javaagents.massimworld.percepts.agent;

import eis.iilang.Percept;
import massim.javaagents.massimworld.actions.ActionType;
import massim.javaagents.massimworld.agent.AgentState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AgentNamePercept extends AgentPercept {

    private static final Logger LOG = LoggerFactory.getLogger(AgentNamePercept.class);

    private String agentName;

    public AgentNamePercept(Percept percept) {
        agentName = readString(percept, 0);
    }

    @Override
    public void updateAgentState(AgentState agentState) {
        agentState.setAgentName(agentName);
    }
}
