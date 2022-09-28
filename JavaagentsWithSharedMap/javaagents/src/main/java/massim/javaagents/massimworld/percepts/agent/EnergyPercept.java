package massim.javaagents.massimworld.percepts.agent;

import eis.iilang.Percept;
import massim.javaagents.massimworld.agent.AgentState;
import massim.javaagents.massimworld.game.Game;
import massim.javaagents.massimworld.map.Coordinates;

import java.util.List;

public class EnergyPercept extends AgentPercept {

    private int energy;

    public EnergyPercept(Percept percept) {
        energy = readInt(percept, 0);
    }

    @Override
    public void updateAgentState(AgentState agentState) {
        agentState.setEnergy(energy);
    }
}

