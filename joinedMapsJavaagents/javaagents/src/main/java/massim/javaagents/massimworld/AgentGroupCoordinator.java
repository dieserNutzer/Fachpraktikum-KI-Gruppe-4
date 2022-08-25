package massim.javaagents.massimworld;

import aima.core.agent.impl.SimpleAgent;
import massim.javaagents.aimamassimworld.MassimAction;
import massim.javaagents.aimamassimworld.MassimPercept;
import massim.javaagents.massimworld.agent.MassimTeam4Agent;
import massim.javaagents.massimworld.game.Game;
import massim.javaagents.massimworld.map.MassimCell;
import massim.javaagents.massimworld.map.MassimMap;
import massim.javaagents.massimworld.percepts.map.EntityPercept;
import massim.javaagents.massimworld.percepts.map.MapPercept;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class AgentGroupCoordinator extends SimpleAgent<MassimPercept, MassimAction> {

    private static final Logger LOG = LoggerFactory.getLogger(AgentGroupCoordinator.class);

    private static final AgentGroupCoordinator agentGroupCoordinator = new AgentGroupCoordinator();

    List<AgentEncounter> encounters = new ArrayList<>();

    public static AgentGroupCoordinator getAgentGroupCoordinator() {
        return agentGroupCoordinator;
    }

    public AgentGroupCoordinator() {
    }

    public void tellPerception(MassimTeam4Agent agent, List<MapPercept> mapPercepts) {

        if (Game.getCurrentStep() == 3) {
            int step = 3;
        }

        MassimMap massimMap = agent.getMap();

        Coordinates agentPrevPos = massimMap.getAgentPositionByAgent(agent);
        Coordinates agentMove = agent.getAgentState().getLastActionOffset();
        massimMap.moveAgentPositionByOffset(agent, agentMove);
        Coordinates agentCurrentPos = massimMap.getAgentPositionByAgent(agent);
        LOG.info("Game step {}: agent: {} moved from {} by {} to {}", Game.getCurrentStep(), agent.getName(), agentPrevPos, agentMove, agentCurrentPos);

        Coordinates agentCoordinates = massimMap.getAgentPositionByAgent(agent);

        for (MapPercept percept : mapPercepts) {
            Coordinates coor = agentCoordinates.withOffset(percept.getCoordinates());
            MassimCell currentCell = massimMap.getOrCreateBasicMassimCell(coor, Game.getCurrentStep());
            percept.updateMassimCell(currentCell);

            // TODO Heuristik verbessern
            if (perceptIsUnkownEncounter(percept)) {
                AgentEncounter encounter = new AgentEncounter(agent,percept.getCoordinates());
                encounters.add(encounter);
            }
        }

    }

    // TODO team name
    private boolean perceptIsUnkownEncounter(MapPercept percept) {
       if (percept instanceof EntityPercept ep) {
           if (ep.isOfTeam("4") && !ep.getCoordinates().equals(Coordinates.ZERO)) {
               return true;
           }
       }
       return false;
    }

    public void evaluateEncounters() {
        LOG.info("########################### Game step {} ", Game.getCurrentStep());
        if (encounters.isEmpty()) {
            return;
        }
        List<AgentEncounter> newEncounters = encounters.stream()
                .filter(AgentEncounter::isWithUnknownAgent).toList();

        for (AgentEncounter encounter: newEncounters) {
            for (AgentEncounter otherEncounter: newEncounters) {
                if (encounter.agentsHaveInverseRelativePositions(otherEncounter) && encounter.isWithUnknownAgent() && encounter.viewIntersectionIsEqual(otherEncounter)) {
                    MassimTeam4Agent agent = encounter.getAgent();
                    MassimTeam4Agent otherAgent = otherEncounter.getAgent();
                    LOG.info("join map and planner of " + agent.getName() + " with of " + otherAgent.getName() );
                    boolean joinedMaps = agent.getMap().joinMapByAgent(agent, otherAgent, encounter.getOtherAgentRelativePosition());
                    if (joinedMaps) {
                        agent.getTaskPlanner().joinTaskPlanner(otherAgent.getTaskPlanner());
                    } else {
                        LOG.error("maps already joined");
                    }
                    LOG.info("joined map and planner of this " + agent.getName() + " with of other " + otherAgent.getName() + " done " + joinedMaps);
                }
            }
        }
        encounters = new ArrayList<>();
    }

}