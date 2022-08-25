package massim.javaagents.massimworld.game.task;

import java.util.List;

public class MassimTask {

    private final String name;
    private final int deadline;
    private final int reward;
    private final List<TaskRequirement> requirements;

    public MassimTask(String name, int deadline, int reward, List<TaskRequirement> requirements) {
        this.name = name;
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
}
