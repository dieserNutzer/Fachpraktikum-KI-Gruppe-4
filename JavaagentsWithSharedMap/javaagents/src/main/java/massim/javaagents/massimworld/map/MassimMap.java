package massim.javaagents.massimworld.map;

import massim.javaagents.massimworld.actions.MoveAction;
import massim.javaagents.massimworld.agent.MassimTeam4Agent;
import massim.javaagents.massimworld.map.things.AgentThing;
import massim.javaagents.massimworld.map.things.BlockType;
import massim.javaagents.massimworld.map.view.MassimMapView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * The map for the massim simulation environment, works as knowledge base for the agents,
 * that are connected to the given instance.
 */
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
        if (cell == null || cell.getLastUpdatedStep() < step) {
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
            return new MassimMapView(relativeToAgent);
        } catch (Exception e ) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Joins the current map of otherAgent with the map of thisAgent.
     * @param thisAgent the agent of the current map, that had encountered otherAgent.
     * @param otherAgent the other agent of the encounter
     * @param shift the position of otherAgent realtive to thisAgent
     * @return true iff the map of otherAgent was successfully joined
     */
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
        Coordinates transformPositionInOtherMapIntoThisMap = thisAgentPosition.withOffset(shift).minus(otherAgentPositionInOtherMap);
        LOG.info("this agent position {}, other agents position in other map {}, shift {}, trafo {}", thisAgentPosition, otherAgentPositionInOtherMap, shift, transformPositionInOtherMapIntoThisMap);
        Map<Coordinates, MassimCell> newCellsByCoordinates = new HashMap<>();
        otherMassimCellsByCoordinates.keySet().stream()
                .forEach(otherCoordinates -> {
                    Coordinates coordinatesInThisMap = otherCoordinates.withOffset(transformPositionInOtherMapIntoThisMap);
                    MassimCell thisCell = getMassimCell(coordinatesInThisMap);
                    MassimCell otherCell = otherMap.getMassimCell(otherCoordinates);
                    if (thisCell == null || thisCell.isOlderThan(otherCell)) {
                        newCellsByCoordinates.put(coordinatesInThisMap, MassimCell.withCoordinates(coordinatesInThisMap).update(otherCell));
                    }
                });
        massimCellsByCoordinates.putAll(newCellsByCoordinates);


        // update with agents of other map
        Map<MassimTeam4Agent, Coordinates> otherAgentPositionsByAgents =  otherMap.getAgentPositionsByAgent();
        otherAgentPositionsByAgents.keySet().stream()
                .forEach(currentOtherAgent -> {
                    Coordinates otherAgentCoordinatesInThisMap = currentOtherAgent.getCurrentCoordinates().withOffset(transformPositionInOtherMapIntoThisMap);
                    LOG.info("other agent {} position in this map {}, shift {}", currentOtherAgent.getName(), otherAgentCoordinatesInThisMap, shift);
                    agentPositionsByAgent.put(currentOtherAgent, otherAgentCoordinatesInThisMap);
                });

        // update map of other agents with this map
        otherAgentPositionsByAgents.keySet().
                forEach(currentOtherAgent -> {
                    currentOtherAgent.setMap(this);
                    LOG.info("set map of agent {} to map of this agent {}",currentOtherAgent.getName(), thisAgent.getName() );
                });

        otherMap.clear();

        // check relative positions of agents
        if (!agentPositionsByAgent.get(thisAgent).withOffset(shift).equals(agentPositionsByAgent.get(otherAgent))) {
            LOG.error("thisAgent position with shift doesn't match other agent position");
            LOG.error("thisAgent  position {} + shift {} = ", agentPositionsByAgent.get(thisAgent), shift, agentPositionsByAgent.get(thisAgent).withOffset(shift));
            LOG.error("otherAgent position {}", agentPositionsByAgent.get(otherAgent));
            LOG.error("transformation {}", transformPositionInOtherMapIntoThisMap);
        }

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
        return !agentPositionsByAgent.containsValue(checkPosition);
    }

    public MassimCell getMassimCell(Coordinates coordinates) {
        return massimCellsByCoordinates.get(coordinates);
    }


    public boolean isAllowed(Coordinates adjacent) {
        MassimCell cell = massimCellsByCoordinates.get(adjacent);
        return cell != null && cell.isAllowed();
    }

    public boolean containsGoalZone() {
        return massimCellsByCoordinates.values().stream()
                .anyMatch(MassimCell::isGoalZone);
    }

    public boolean containsRoleZone() {
        return massimCellsByCoordinates.values().stream()
                .anyMatch(MassimCell::isRoleZone);
    }

    public Set<Coordinates> getGoalZoneCoordinates() {
        return massimCellsByCoordinates.values().stream()
                .filter(MassimCell::isGoalZone)
                .map(MassimCell::getCoordinates)
                .collect(Collectors.toSet());
    }

    public Set<Coordinates> getRoleZoneCoordinates() {
        return massimCellsByCoordinates.values().stream()
                .filter(MassimCell::isRoleZone)
                .map(MassimCell::getCoordinates)
                .collect(Collectors.toSet());
    }


    public MassimCell getAdjacentCell(Coordinates coordinates, Direction direction) {
        return massimCellsByCoordinates.get(coordinates.getAdjacent(direction));
    }

    public Set<Coordinates> getDispenserCoordinates(BlockType blockType) {
        Set<Coordinates> dispenserCellCoordinates = massimCellsByCoordinates.values().stream()
                .filter(cell -> cell.containsDispenser(blockType))
                .map(MassimCell::getCoordinates)
                .collect(Collectors.toSet());
        Set<Coordinates> adjacentFreeCellsCoordinates = new HashSet<>();
        dispenserCellCoordinates.forEach(coor -> {
            if (getAdjacentCell(coor, Direction.EAST) != null && getAdjacentCell(coor, Direction.EAST).isAllowed()) {
                adjacentFreeCellsCoordinates.add(getAdjacentCell(coor, Direction.EAST).getCoordinates());
            }
            if (getAdjacentCell(coor, Direction.SOUTH) != null && getAdjacentCell(coor, Direction.SOUTH).isAllowed()) {
                adjacentFreeCellsCoordinates.add(getAdjacentCell(coor, Direction.SOUTH).getCoordinates());
            }
            if (getAdjacentCell(coor, Direction.WEST) != null && getAdjacentCell(coor, Direction.WEST).isAllowed()) {
                adjacentFreeCellsCoordinates.add(getAdjacentCell(coor, Direction.WEST).getCoordinates());
            }
            if (getAdjacentCell(coor, Direction.NORTH) != null && getAdjacentCell(coor, Direction.NORTH).isAllowed()) {
                adjacentFreeCellsCoordinates.add(getAdjacentCell(coor, Direction.NORTH).getCoordinates());
            }
        });
        return adjacentFreeCellsCoordinates;
    }

    public boolean isAdjacentToDispenser(Coordinates coordinates, BlockType blockType) {
        return Coordinates.getAdjacentCoordinates(coordinates).stream()
                .filter(c -> massimCellsByCoordinates.get(c).containsDispenser(blockType))
                .findFirst().
                isPresent();
    }

    public Direction getAdjacentDispenserDirection(Coordinates coordinates, BlockType blockType) {
        Coordinates dispenserCoordinates = Coordinates.getAdjacentCoordinates(coordinates).stream()
                .filter(c -> massimCellsByCoordinates.get(c).containsDispenser(blockType))
                .map(c -> c.minus(coordinates))
                .findFirst().orElse(null);
        if (dispenserCoordinates == null) {
            LOG.error("getAdjacentDispenserDirection found null");
            return Direction.getNext();
        }
        return Direction.getByOffset(dispenserCoordinates);
    }

    public Direction getDirectionFromCoordinatesTowardsDirection(Coordinates coordinates, Direction direction) {
        MassimCell proposal = getAdjacentCell(coordinates, Direction.getOrthogonalDirectionClockwise(direction));
        if (proposal.isEmpty()) {
            return Direction.getOrthogonalDirectionClockwise(direction);
        }
        proposal = getAdjacentCell(coordinates, Direction.getOrthogonalDirectionCounterClockwise(direction));
        if (proposal.isEmpty()) {
            return Direction.getOrthogonalDirectionCounterClockwise(direction);
        }
        proposal = getAdjacentCell(coordinates, Direction.getOppositeDirection(direction));
        if (proposal.isEmpty()) {
            return Direction.getOppositeDirection(direction);
        }
        return null;
    }

    public void applyMoveActionForAgent(MoveAction moveAction, MassimTeam4Agent agent) {
        massimCellsByCoordinates.get(agent.getCurrentCoordinates()).clear();
        MassimCell afterAgentMove = massimCellsByCoordinates.get(agent.getCurrentCoordinates().withOffset(moveAction.getMoveOffset()));
        afterAgentMove.setThing(new AgentThing(agent.getName()));
    }

    public MassimCell getNextEmptyCellInDirection(Coordinates currentPosition, Direction direction) {
        int viewBorder = 5;
        for (int i = 1; i <= viewBorder; i++) {
            MassimCell target = massimCellsByCoordinates.get(currentPosition.withOffset(direction.getOffset().times(i)));
            if (target.isEmpty()) {
                return target;
            }
            for (int j = 1; j <= i-2; j++) {
                target = massimCellsByCoordinates.get(currentPosition.withOffset(direction.getOffset().times(i)).withOffset(Direction.getOrthogonalDirectionClockwise(direction).getOffset().times(j)));
                if (target != null && target.isEmpty()) {
                    return target;
                }
                target = massimCellsByCoordinates.get(currentPosition.withOffset(direction.getOffset().times(i)).withOffset(Direction.getOrthogonalDirectionCounterClockwise(direction).getOffset().times(j)));
                if (target != null && target.isEmpty()) {
                    return target;
                }
            }
        }
        return null;
    }
}
