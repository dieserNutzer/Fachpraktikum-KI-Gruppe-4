package massim.javaagents.massimworld.game.task.agenttask;

import massim.javaagents.massimworld.actions.*;
import massim.javaagents.massimworld.agent.MassimTeam4Agent;
import massim.javaagents.massimworld.game.task.MassimTask;
import massim.javaagents.massimworld.map.things.BlockType;

import java.util.ArrayList;
import java.util.List;

import static massim.javaagents.massimworld.actions.ActionResult.SUCCESS;
import static massim.javaagents.massimworld.actions.ActionType.MOVE;

public class MoveToDispenserTask extends AgentTask {

    private final BlockType blockType;

    private List<MoveAction> movesToDispenser = new ArrayList<>();

    public MoveToDispenserTask(BlockType blockType, List<MoveAction> movesToDispenser) {
        super("moveToDispenserTask");
        this.blockType = blockType;
        this.movesToDispenser = movesToDispenser;
    }

//    public MoveToDispenserTask(BlockType blockType) {
//        this.blockType = blockType;
//    }

    public BlockType getBlockType() {
        return blockType;
    }

    public List<MoveAction> getMovesToDispenser() {
        return movesToDispenser;
    }

    public void setMovesToDispenser(List<MoveAction> movesToDispenser) {
        this.movesToDispenser = movesToDispenser;
    }

    @Override
    public void update(MassimTeam4Agent agent) {
        if (lastMoveActionSuccessful(agent)) {
            movesToDispenser.remove(0);
            if (movesToDispenser.isEmpty()) {
                setFinished();
            }
        }
    }

    private boolean lastMoveActionSuccessful(MassimTeam4Agent agent) {
        // TODO implement stable
        return MOVE.equals(agent.getLastActionType()) &&
               SUCCESS.equals(agent.getLastActionResult()) &&
               !movesToDispenser.isEmpty() &&
               movesToDispenser.get(0).getDirection().getSymbol().equals(agent.getAgentState().getLastActionParams().get(0));
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
        if (movesToDispenser.isEmpty()) {
            setFinished();
            return new SkipAction();
        }
        return movesToDispenser.get(0);
    }

    @Override
    public int getStepEstimation() {
        return movesToDispenser.size();
    }
}
