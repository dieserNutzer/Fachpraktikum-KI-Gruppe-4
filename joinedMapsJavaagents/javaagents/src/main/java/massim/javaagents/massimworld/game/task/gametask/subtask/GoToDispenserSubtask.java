package massim.javaagents.massimworld.game.task.gametask.subtask;

import massim.javaagents.massimworld.actions.MassimAction;
import massim.javaagents.massimworld.actions.MoveAction;
import massim.javaagents.massimworld.agent.MassimTeam4Agent;

import java.util.ArrayList;
import java.util.List;

public class GoToDispenserSubtask extends Subtask {

    List<MoveAction> movesToDispenser = new ArrayList<>();


    public GoToDispenserSubtask() {
    }

    public GoToDispenserSubtask(List<MoveAction> movesToDispenser) {
        this.movesToDispenser = movesToDispenser;
    }


    @Override
    public MassimAction getAction(MassimTeam4Agent agent) {
        if (hasAction()) {
            return movesToDispenser.remove(0);
        } else {
            return null;
        }
    }

    public boolean hasAction() {
        return !movesToDispenser.isEmpty();
    }

    public int getRemainingSteps() {
        return movesToDispenser.size();
    }
}
