package massim.javaagents.massimworld.game.task.gametask;

import massim.javaagents.massimworld.game.task.MassimTask;
import massim.javaagents.massimworld.game.task.TaskRequirement;
import massim.javaagents.massimworld.game.task.agenttask.AgentTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class GameTask extends MassimTask {

    protected int priority;

    protected final int deadline;
    protected final int reward;
    protected final List<TaskRequirement> requirements;

    protected List<AgentTask> agentTasks = new ArrayList<>();

    protected int involvedAgentNumber;

    public double getTaskValue() {
        if (getReward() == 0 || getStepEstimation() == 0 || involvedAgentNumber == 0) {
            return 0;
        } else {
            return getReward() / (getStepEstimation() * involvedAgentNumber);
        }
    }

    public GameTask(String name, int deadline, int reward, List<TaskRequirement> requirements, int involvedAgentNumber, int priority) {
        super(name);
        this.deadline = deadline;
        this.reward = reward;
        this.requirements = requirements;
        this.involvedAgentNumber = involvedAgentNumber;
        this.priority = priority;

    }

    public int remainingActiveSteps(int step) {
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

    public int getPriority() {
        return priority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GameTask)) return false;
        GameTask gameTask = (GameTask) o;
        return name.equals(gameTask.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
