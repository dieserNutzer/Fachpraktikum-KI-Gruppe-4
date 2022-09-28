package massim.javaagents.massimworld.percepts.agent;

import eis.iilang.Percept;
import massim.javaagents.massimworld.agent.AgentState;
import massim.javaagents.massimworld.map.Coordinates;

public class AttachedPercept extends AgentPercept {
    Coordinates coordinates;

    public AttachedPercept(Percept percept) {
        coordinates = Coordinates.of(readInt(percept, 0), readInt(percept,1));
    }

    @Override
    public void updateAgentState(AgentState agentState) {
        agentState.addAttachedCoordinates(coordinates);
    }
}

