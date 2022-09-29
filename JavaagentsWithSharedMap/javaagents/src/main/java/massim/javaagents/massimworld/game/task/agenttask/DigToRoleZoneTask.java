package massim.javaagents.massimworld.game.task.agenttask;

import massim.javaagents.massimworld.actions.ActionResult;
import massim.javaagents.massimworld.actions.ActionType;
import massim.javaagents.massimworld.actions.MassimAction;
import massim.javaagents.massimworld.actions.MoveAction;
import massim.javaagents.massimworld.agent.MassimTeam4Agent;
import massim.javaagents.massimworld.game.task.MassimTask;
import massim.javaagents.massimworld.map.Coordinates;
import massim.javaagents.massimworld.map.things.BlockType;

import java.util.ArrayList;
import java.util.List;

public class DigToRoleZoneTask extends AgentTask {

    private final List<Coordinates> roleZoneCoordinates;

    private List<MoveAction> movesToBlock = new ArrayList<>();

    public DigToRoleZoneTask(BlockType blockType, List<Coordinates> roleZoneCoordinates, List<MoveAction> movesToBlock) {
        super("moveToBlockTask");
        this.roleZoneCoordinates = roleZoneCoordinates;
        this.movesToBlock = movesToBlock;
    }

//    public MoveToDispenserTask(BlockType blockType) {
//        this.blockType = blockType;
//    }


    public List<MoveAction> getMovesToBlock() {
        return movesToBlock;
    }

    public void setMovesToBlock(List<MoveAction> movesToBlock) {
        this.movesToBlock = movesToBlock;
    }

    @Override
    public void update(MassimTeam4Agent agent) {
        if (ActionType.MOVE.equals(agent.getAgentState().getLastActionType()) && ActionResult.SUCCESS.equals(agent.getAgentState().getLastActionResult())) {
            movesToBlock.remove(0);
            if (movesToBlock.isEmpty()) {
                setFinished();
            }
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
        return movesToBlock.get(0);
    }

    @Override
    public int getStepEstimation() {
        return movesToBlock.size();
    }

    @Override
    public String toString() {
        String movesString = "";
        for(MoveAction ma: movesToBlock) {
            movesString += ma.toString() + "\n";
        }
        return "MoveToBlockTask{" +
                ", movesToBlock=" +  movesString +
                ", name='" + name + '\'' +
                ", taskState=" + taskState +
                '}';
    }
}
