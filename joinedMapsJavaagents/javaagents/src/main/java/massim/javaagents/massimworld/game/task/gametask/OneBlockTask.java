package massim.javaagents.massimworld.game.task.gametask;

import massim.javaagents.massimworld.game.task.TaskRequirement;
import massim.javaagents.massimworld.game.task.gametask.subtask.*;

import java.util.ArrayList;
import java.util.List;

public class OneBlockTask extends GameTask {

    private List<Subtask> subtasks = new ArrayList<>();

    public OneBlockTask(String name, int deadline, int reward, List<TaskRequirement> requirements) {
        super(name, deadline, reward, requirements);
    }

    public OneBlockTask(String name, int deadline, int reward, List<TaskRequirement> requirements, GoToDispenserSubtask goToDispenserSubtask, GetToGoalZoneSubtask getToGoalZoneSubtask) {
        super(name, deadline, reward, requirements);
        subtasks.add(goToDispenserSubtask);
        subtasks.add(new GetBlockFromDispenserSubtask());
        subtasks.add(getToGoalZoneSubtask);
        subtasks.add(new OrientateSubTask());
        subtasks.add(new SubmitSubtask());
    }

    public String getBlockType() {
        return getRequirements().get(0).getBlockType();
    }

    public void setGoToDispenserSubtask(GoToDispenserSubtask goToDispenserSubtask) {
        subtasks.set(1, goToDispenserSubtask);
    }

    public void setGetToGoalZoneTask(GetToGoalZoneSubtask getToGoalZoneSubtask) {
        subtasks.set(2, getToGoalZoneSubtask);
    }

    public int getEstimatedSteps() {
        return ((GoToDispenserSubtask) subtasks.get(0)).getRemainingSteps() + ((GetToGoalZoneSubtask) subtasks.get(1)).getRemainingSteps() + 2;
    }

    public OneBlockTask copy() {
        return new OneBlockTask(name, deadline, reward, requirements);
    }
}
