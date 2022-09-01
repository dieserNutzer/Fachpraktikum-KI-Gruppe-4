package massim.javaagents.massimworld.game.task.gametask;

import massim.javaagents.massimworld.game.task.MassimTask;
import massim.javaagents.massimworld.game.task.TaskRequirement;

import java.util.List;

public class GameTask extends MassimTask {

    protected final int deadline;
    protected final int reward;
    protected final List<TaskRequirement> requirements;

    public GameTask(String name, int deadline, int reward, List<TaskRequirement> requirements) {
        super(name);
        this.deadline = deadline;
        this.reward = reward;
        this.requirements = requirements;
    }

    public int remainingSteps(int step) {
        return deadline - step;
    }

    public boolean isActive(int step) {
        return deadline < step;
    }

    public String getName() {
        return name;
    }

    public int getDeadline() {
        return deadline;
    }

    public int getReward() {
        return reward;
    }

    public List<TaskRequirement> getRequirements() {
        return requirements;
    }
//    public boolean isFeasible(MassimMap massimMap) {
//        if (massimMap.containsGoalZone() &&
//                massimMap.getNumberOfAgents() >= requirements.size() &&
//                requirements.forEach( taskRequirement -> {
//                    massimMap.comtainsDispenser(taskRequirement.getBlockType())
//                });
//
//        )
//    }
}
