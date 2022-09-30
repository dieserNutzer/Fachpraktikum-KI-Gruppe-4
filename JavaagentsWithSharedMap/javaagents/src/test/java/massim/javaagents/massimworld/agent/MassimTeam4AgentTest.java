package massim.javaagents.massimworld.agent;

import eis.iilang.*;
import massim.javaagents.massimworld.game.Game;
import massim.javaagents.massimworld.map.Coordinates;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MassimTeam4AgentTest {

    @Test
    @DisplayName("agent update states with map percept updates map")
    public void testUpdateStatesByPerceptionWithDispenserPerceptUpdatesMap() {
        MassimTeam4Agent agent = new MassimTeam4Agent("testAgent", null);
        agent.setPercepts(
                List.of(new Percept("thing", new Numeral(2), new Numeral(2), new Identifier("obstacle"))),
                List.of()
        );

        agent.updateStatesByPerception();
        assertEquals(61, agent.getMap().getMassimCellsByCoordinates().size());
        assertTrue(agent.getMap().getMassimCell(Coordinates.of(2,2)).getThing().isObstacle());
    }

    @Test
    @DisplayName("agent update states with agent percept updates agent")
    public void testUpdateStatesByPerceptionWithDeactivatedPerceptUpdatesAgent() {
        MassimTeam4Agent agent = new MassimTeam4Agent("testAgent", null);
        agent.setPercepts(
                List.of(new Percept("deactivated", new Identifier("true"))),
                List.of()
        );

        agent.updateStatesByPerception();

        assertTrue(agent.getAgentState().isDeactivated());
    }

    @Test
    @DisplayName("agent update states with game percept updates game")
    public void testUpdateStatesByPerceptionWithStepPerceptUpdatesGame() {

        MassimTeam4Agent agent = new MassimTeam4Agent("testAgent", null);
        agent.setPercepts(
                List.of(new Percept("step", new Numeral(42))),
                List.of()
        );

        agent.updateStatesByPerception();
        assertEquals(42, Game.getCurrentStep());
    }
}