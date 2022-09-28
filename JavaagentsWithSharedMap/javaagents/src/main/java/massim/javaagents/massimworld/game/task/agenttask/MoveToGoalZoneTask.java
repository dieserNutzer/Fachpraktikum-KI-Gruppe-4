package massim.javaagents.massimworld.game.task.agenttask;

import massim.javaagents.massimworld.actions.*;
import massim.javaagents.massimworld.agent.MassimTeam4Agent;
import massim.javaagents.massimworld.game.task.MassimTask;

import java.util.ArrayList;
import java.util.List;

import static massim.javaagents.massimworld.actions.ActionResult.FAILED_PATH;
import static massim.javaagents.massimworld.actions.ActionResult.SUCCESS;
import static massim.javaagents.massimworld.actions.ActionType.MOVE;

public class MoveToGoalZoneTask extends AgentTask {

    private List<MoveAction> movesToGoalZone = new ArrayList<>();

    public MoveToGoalZoneTask(List<MoveAction> movesToGoalZone) {
        super("moveToGoalZoneTask");
        this.movesToGoalZone = movesToGoalZone;
    }

//    public MoveToGoalZoneTask(BlockType blockType) {
//        this.blockType = blockType;
//    }


    public List<MoveAction> getMovesToGoalZone() {
        return movesToGoalZone;
    }

    public void setMovesToGoalZone(List<MoveAction> movesToBlock) {
        this.movesToGoalZone = movesToBlock;
    }

    @Override
    public void update(MassimTeam4Agent agent) {
        if (MOVE.equals(agent.getAgentState().getLastActionType()) &&
                SUCCESS.equals(agent.getAgentState().getLastActionResult()) &&
                movesToGoalZone != null && !movesToGoalZone.isEmpty() && movesToGoalZone.get(0).getDirection().getSymbol().equals(agent.getAgentState().getLastActionParams().get(0))) {
            movesToGoalZone.remove(0);
            if (movesToGoalZone.isEmpty()) {
                setFinished();
            }
        } else if (MOVE.equals(agent.getAgentState().getLastActionType()) &&
                FAILED_PATH.equals(agent.getAgentState().getLastActionResult()) &&
                        agent.getAgentState().hasAttachedBlock()){// && movesToGoalZone.size() >= 2) {
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
    public void replan(MassimTeam4Agent agent) {

    }

    @Override
    public MassimAction getNextAction(MassimTeam4Agent agent) {
        if (movesToGoalZone.isEmpty()) {
            setFinished();
            return new SkipAction();
        }
        return movesToGoalZone.get(0);
    }

    @Override
    public int getStepEstimation() {
        return movesToGoalZone.size();
    }
}

