package massim.javaagents.massimworld.game.task.agenttask;

import massim.javaagents.massimworld.actions.ClearAction;
import massim.javaagents.massimworld.actions.MassimAction;
import massim.javaagents.massimworld.actions.MoveAction;
import massim.javaagents.massimworld.agent.MassimTeam4Agent;
import massim.javaagents.massimworld.game.strategy.GameStrategy;
import massim.javaagents.massimworld.game.task.MassimTask;
import massim.javaagents.massimworld.map.Coordinates;
import massim.javaagents.massimworld.map.Direction;
import massim.javaagents.massimworld.map.MassimCell;
import massim.javaagents.massimworld.map.route.RoutePlanner;
import massim.javaagents.massimworld.map.things.AgentThing;
import massim.javaagents.massimworld.map.things.Dispenser;
import massim.javaagents.massimworld.map.things.Obstacle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ExplorationTask extends AgentTask {

    private static final Logger LOG = LoggerFactory.getLogger(ExplorationTask.class);

    Direction direction;//= Direction.getRandomDirection();

    List<MoveAction> movePlan = new ArrayList<>();

    Direction subdirection = null;//Direction.getRandomOrthogonalDirection(direction);
    public ExplorationTask(Direction dir) {
        super("explorationTask");
        direction = dir;
    }

    @Override
    public MassimAction getNextAction(MassimTeam4Agent agent) {
        if (agent.getAdjacentCell(direction) == null) {
            LOG.error("agent {} get adjacent cell in direction {} returns  null", agent.getName(), direction.getSymbol());
        }
        if (!movePlan.isEmpty()) {
            return movePlan.remove(0);
        }
//        if ("move".equals(agent.getAgentState().getLastAction()) && !"success".equals(agent.getAgentState().getLastActionResult())) {
//            Direction.getNext();
//        }
//        if (agent.getMap().containsGoalZone() &&
//                !RoutePlanner.existsPathForAgentToTarget(agent.getMap(), agent.getCurrentCoordinates(), agent.getMap().getGoalZoneCoordinates()) &&
//                agent.getMap().
//        ) {
//
//        }
        if (agent.getAdjacentCell(direction) != null && (agent.getAdjacentCell(direction).getThing() instanceof Obstacle)) {
            MassimCell massimCell = agent.getMap().getNextEmptyCellInDirection(agent.getCurrentCoordinates(), direction);
            if (massimCell != null) {
                List<MoveAction> movePlanProposal = RoutePlanner.planRouteForAgentToTarget(agent, Set.of(massimCell.getCoordinates()));
                if (!movePlan.isEmpty() && movePlan.size() < 5) {
                    movePlan = movePlanProposal;
                    return movePlan.remove(0);
                }
            }
            return new ClearAction(direction);
        } else if (agent.getAdjacentCell(direction) != null &&
                (//agent.getAdjacentCell(direction).getThing() instanceof Dispenser ||
                 agent.getAdjacentCell(direction).getThing() instanceof AgentThing)) {
            direction = Direction.getNext();
            return getNextAction(agent);
        } else {
            return new MoveAction(direction);
        }
    }

    @Override
    public void update(MassimTeam4Agent agent) {

    }

    @Override
    public boolean hasSubtask() {
        return false;
    }

    @Override
    public MassimTask getCurrentSubtask() {
        return null;
    }

    @Override
    public void replan(MassimTeam4Agent agent) {

    }

    @Override
    public int getStepEstimation() {
        return 0;
    }
}
