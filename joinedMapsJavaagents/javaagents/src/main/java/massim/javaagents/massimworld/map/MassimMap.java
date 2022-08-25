package massim.javaagents.massimworld.map;

import massim.javaagents.massimworld.Coordinates;
import massim.javaagents.massimworld.agent.MassimTeam4Agent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MassimMap {

    private static final Logger LOG = LoggerFactory.getLogger(MassimMap.class);

    Map<MassimTeam4Agent, Coordinates> agentPositionsByAgent = new HashMap<>();

    Map<Coordinates, MassimCell> massimCellsByCoordinates = new HashMap<>();

    public MassimMap(MassimTeam4Agent agent) {
        this.agentPositionsByAgent.put(agent, Coordinates.of(0,0));
        this.massimCellsByCoordinates.put(Coordinates.of(0,0), new MassimCell(Coordinates.of(0,0)));
    }

    public Map<Coordinates, MassimCell> getMassimCellsByCoordinates() {
        return massimCellsByCoordinates;
    }

    public MassimCell getOrCreateBasicMassimCell(Coordinates coordinates, int step) {
        MassimCell cell = massimCellsByCoordinates.get(coordinates);
        if (cell == null) {
            cell = new MassimCell(coordinates, step);
            massimCellsByCoordinates.put(coordinates, cell);
        }
        return massimCellsByCoordinates.get(coordinates);
    }

    public Map<MassimTeam4Agent, Coordinates> getAgentPositionsByAgent() {
        return agentPositionsByAgent;
    }

    public Coordinates getAgentPositionByAgent(MassimTeam4Agent agent) {
        Coordinates agentPosition = getAgentPositionsByAgent().get(agent);
        if (agentPosition == null) {
            LOG.error("agent with pos null {}", agent.getName());
        }
        return agentPosition;
    }


    public MassimMapView getCurrentViewWithRelativeCoordinatesAndOffset(MassimTeam4Agent agent, Coordinates offset) {
        Coordinates agentPosition = getAgentPositionByAgent(agent);

        try {
            Set<Coordinates> relativeToAgentCoordinates = Coordinates.getAllCoordinatesWithinDistance(agentPosition, agent.getVision());
            Map<Coordinates, MassimCell> relativeToAgent = new HashMap<>();
            for (Coordinates coordinates: relativeToAgentCoordinates) {
                MassimCell currentCell = getMassimCell(coordinates);
                if (currentCell == null) {
                    LOG.error("found null cell for relative view");
                    continue;
                }
                MassimCell newCell = currentCell.createCopyShiftedBy(agentPosition.inverse().withOffset(offset));
                relativeToAgent.put(newCell.getCoordinates(), newCell);
            }

//            Map<Coordinates, MassimCell> relativeToAgent = Coordinates.getAllCoordinatesWithinDistance(agentPosition, agent.getVision()).stream()
//                    .map(coor -> getMassimCell(coor))
//                    .map(cell -> cell.shiftByCoordinates(agentPosition.inverse().withOffset(offset)))
//                    .collect(Collectors.toMap(MassimCell::getCoordinates, Function.identity()));


            return new MassimMapView(relativeToAgent);
        } catch (Exception e ) {
            e.printStackTrace();
//            throw e;
        }
        return null;
    }

    public boolean joinMapByAgent(MassimTeam4Agent thisAgent, MassimTeam4Agent otherAgent, Coordinates shift) {
        LOG.info("update cells of map of agent {} with cells of map of agent {}", thisAgent.getName(), otherAgent.getName());

        if (agentPositionsByAgent.containsKey(otherAgent)) {
            LOG.error("other agent {} is already joined", otherAgent.getName());
            otherAgent.setMap(this);
            return false;
        }

        MassimMap otherMap = otherAgent.getMap();
        Map<Coordinates, MassimCell> otherMassimCellsByCoordinates = otherMap.getMassimCellsByCoordinates();
        Coordinates otherAgentPositionInOtherMap = otherMap.getAgentPositionByAgent(otherAgent);
        Coordinates thisAgentPosition = getAgentPositionByAgent(thisAgent);
//        Coordinates otherAgentPositionInThisMap = thisAgentPosition.withOffset(shift).minus(otherAgentPositionInOtherMap);
        Coordinates transformPositionInOtherMapIntoThisMap = thisAgentPosition.withOffset(shift).minus(otherAgentPositionInOtherMap);
        LOG.info("this agent position {}, other agents position {}, shift {}, trafo {}", thisAgentPosition, otherAgentPositionInOtherMap, shift, transformPositionInOtherMapIntoThisMap);
        Map<Coordinates, MassimCell> newCellsByCoordinates = new HashMap<>();
        otherMassimCellsByCoordinates.keySet().stream()
                .forEach(coordinates -> {
                    Coordinates coordinatesInThisMap = coordinates.withOffset(transformPositionInOtherMapIntoThisMap);
                    MassimCell thisCell = getMassimCell(coordinatesInThisMap);
                    MassimCell otherCell = otherMap.getMassimCell(coordinates);
                    if (thisCell == null || thisCell.isOlderThan(otherCell)) {
                        newCellsByCoordinates.put(coordinatesInThisMap, MassimCell.withCoordinates(coordinatesInThisMap).update(otherCell));
                    }
                });
        massimCellsByCoordinates.putAll(newCellsByCoordinates);


        // update with agents of other map
        Map<MassimTeam4Agent, Coordinates> otherAgentPositionsByAgents =  otherMap.getAgentPositionsByAgent();
        otherAgentPositionsByAgents.keySet().stream()
                .forEach(currentOtherAgent -> {
                    Coordinates otherAgentCoordinatesInThisMap = thisAgent.getMap().getAgentPositionByAgent(thisAgent)
                            .withOffset(transformPositionInOtherMapIntoThisMap);
                    LOG.info("other agents position in this map {}, shift {}", otherAgentCoordinatesInThisMap, shift);
                    agentPositionsByAgent.put(currentOtherAgent, otherAgentCoordinatesInThisMap);
                });

        // update map of other agents with this map
        otherAgentPositionsByAgents.keySet().
                forEach(currentOtherAgent -> {
                    currentOtherAgent.setMap(this);
                    LOG.info("set map of agent {} to map of this agent {}",currentOtherAgent.getName(), thisAgent.getName() );
                });

        otherMap.clear();
        return true;
    }

    private void clear() {
        agentPositionsByAgent = new HashMap<>();
        massimCellsByCoordinates = new ConcurrentHashMap<>();
    }

    public void moveAgentPositionByOffset(MassimTeam4Agent agent, Coordinates shift) {
        agentPositionsByAgent.put(agent, agentPositionsByAgent.get(agent).withOffset(shift));
    }

    public boolean isUnknownAgent(Coordinates checkPosition) {
        return !agentPositionsByAgent.values().contains(checkPosition);
    }

    public MassimCell getMassimCell(Coordinates coordinates) {
        return massimCellsByCoordinates.get(coordinates);
    }




}
