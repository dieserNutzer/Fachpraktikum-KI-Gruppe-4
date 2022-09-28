package massim.javaagents.massimworld.percepts.agent;

import massim.javaagents.massimworld.agent.AgentState;
import massim.javaagents.massimworld.game.Game;
import massim.javaagents.massimworld.map.MassimCell;
import massim.javaagents.massimworld.percepts.MassimPercept;

public abstract class AgentPercept extends MassimPercept {

    @Override
    public void updateGame(Game game) {

    }

    @Override
    public void updateMassimCell(MassimCell massimCell) {

    }

    @Override
    public int getProcessOrder() {
        return 10;
    }

}
