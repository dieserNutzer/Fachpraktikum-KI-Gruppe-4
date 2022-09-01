package massim.javaagents.massimworld.game.task.gametask.subtask;

import massim.javaagents.massimworld.actions.MassimAction;
import massim.javaagents.massimworld.actions.MoveAction;
import massim.javaagents.massimworld.agent.MassimTeam4Agent;

import java.util.ArrayList;
import java.util.List;

public class GetToGoalZoneSubtask extends Subtask {

    List<MoveAction> movesToGoalZone = new ArrayList<>();

    public GetToGoalZoneSubtask() {
    }

    public GetToGoalZoneSubtask(List<MoveAction> movesToGoalZone) {
        this.movesToGoalZone = movesToGoalZone;
    }

    @Override
    public MassimAction getAction(MassimTeam4Agent agent) {
        if (hasAction()) {
            return movesToGoalZone.remove(0);
        } else {
            return null;
        }
    }

    public boolean hasAction() {
        return !movesToGoalZone.isEmpty();
    }

    public int getRemainingSteps() {
        return movesToGoalZone.size();
    }
}

