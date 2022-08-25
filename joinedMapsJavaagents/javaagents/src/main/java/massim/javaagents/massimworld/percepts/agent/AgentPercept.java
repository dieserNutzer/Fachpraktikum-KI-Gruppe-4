package massim.javaagents.massimworld.percepts.agent;

import massim.javaagents.massimworld.agent.AgentState;
import massim.javaagents.massimworld.map.MassimCell;
import massim.javaagents.massimworld.percepts.MassimPercept;

public abstract class AgentPercept extends MassimPercept {
    public abstract void updateAgentState(AgentState agentState);

    @Override
    public void updateMassimCell(MassimCell massimCell) {

    }
}
