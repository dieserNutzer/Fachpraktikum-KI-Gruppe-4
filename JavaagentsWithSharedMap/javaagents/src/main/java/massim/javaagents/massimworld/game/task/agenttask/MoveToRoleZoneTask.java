package massim.javaagents.massimworld.game.task.agenttask;

import massim.javaagents.massimworld.actions.*;
import massim.javaagents.massimworld.agent.MassimTeam4Agent;
import massim.javaagents.massimworld.game.task.MassimTask;

import java.util.ArrayList;
import java.util.List;

public class MoveToRoleZoneTask extends AgentTask {

    private List<MoveAction> movesToRoleZone = new ArrayList<>();

    public MoveToRoleZoneTask(List<MoveAction> movesToRoleZone) {
        super("moveToRoleZoneTask");
        this.movesToRoleZone = movesToRoleZone;
    }


    public List<MoveAction> getMovesToRoleZone() {
        return movesToRoleZone;
    }

    public void setMovesToRoleZone(List<MoveAction> movesToBlock) {
        this.movesToRoleZone = movesToBlock;
    }

    @Override
    public void update(MassimTeam4Agent agent) {
        if (ActionType.MOVE.equals(agent.getAgentState().getLastActionType()) && ActionResult.SUCCESS.equals(agent.getAgentState().getLastActionResult())) {
            movesToRoleZone.remove(0);
            if (movesToRoleZone.isEmpty()) {
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
        if (movesToRoleZone.isEmpty()) {
            setFinished();
            return new SkipAction();
        }
        return movesToRoleZone.get(0);
    }

    @Override
    public int getStepEstimation() {
        return movesToRoleZone.size();
    }
}

