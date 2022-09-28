package massim.javaagents.massimworld.game.task.agenttask.subtask;

import massim.javaagents.massimworld.actions.ActionResult;
import massim.javaagents.massimworld.actions.ActionType;
import massim.javaagents.massimworld.actions.MassimAction;
import massim.javaagents.massimworld.actions.RequestAction;
import massim.javaagents.massimworld.agent.MassimTeam4Agent;
import massim.javaagents.massimworld.game.task.MassimTask;
import massim.javaagents.massimworld.game.task.gametask.GameTaskCostEvaluator;
import massim.javaagents.massimworld.map.Direction;
import massim.javaagents.massimworld.map.things.BlockType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestSubtask extends AgentSubtask {

    private static final Logger LOG = LoggerFactory.getLogger(RequestSubtask.class);

    Direction direction;

    BlockType blockType;

    public Direction getDirection() {
        return direction;
    }

    public RequestSubtask(BlockType blockType) {
        super("requestSubtask");
        this.blockType = blockType;
    }

    @Override
    public void update(MassimTeam4Agent agent) {
        if (ActionType.REQUEST.equals(agent.getAgentState().getLastActionType()) && ActionResult.SUCCESS.equals(agent.getAgentState().getLastActionResult())) {
            setFinished();
        }
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
    public void replan(MassimTeam4Agent agent) {}

    @Override
    public MassimAction getNextAction(MassimTeam4Agent agent) {
        Direction direction = agent.getMap().getAdjacentDispenserDirection(agent.getCurrentCoordinates(), blockType);
        if (direction == null) {
            LOG.error("no adjacent dispenser for block type {} found", blockType);
            cancelTask();
        }
        if (agent.getMap().getAdjacentCell(agent.getCurrentCoordinates(), direction).containsBlockOfType(blockType)) {
            setFinished();
        } else if (!agent.getMap().getAdjacentCell(agent.getCurrentCoordinates(), direction).isEmpty()) {
            cancelTask();
        }
        return new RequestAction(direction);
    }

    @Override
    public int getStepEstimation() {
        return 1;
    }
}
