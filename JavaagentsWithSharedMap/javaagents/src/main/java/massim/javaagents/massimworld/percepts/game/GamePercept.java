package massim.javaagents.massimworld.percepts.game;

import massim.javaagents.massimworld.agent.AgentState;
import massim.javaagents.massimworld.map.MassimCell;
import massim.javaagents.massimworld.percepts.MassimPercept;

public abstract class GamePercept extends MassimPercept {

    int order = 1;

    @Override
    public void updateAgentState(AgentState agentState) {

    }

    @Override
    public void updateMassimCell(MassimCell massimCell) {

    }

    @Override
    public int getProcessOrder() {
        return 0;
    }
}
