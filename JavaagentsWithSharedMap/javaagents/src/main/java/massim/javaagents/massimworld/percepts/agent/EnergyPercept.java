package massim.javaagents.massimworld.percepts.agent;

import eis.iilang.Percept;
import massim.javaagents.massimworld.agent.AgentState;

public class EnergyPercept extends AgentPercept {

    private final int energy;

    public EnergyPercept(Percept percept) {
        energy = readInt(percept, 0);
    }

    @Override
    public void updateAgentState(AgentState agentState) {
        agentState.setEnergy(energy);
    }
}

