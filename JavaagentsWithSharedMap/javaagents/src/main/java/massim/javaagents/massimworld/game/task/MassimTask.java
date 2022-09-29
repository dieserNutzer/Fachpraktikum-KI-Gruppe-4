package massim.javaagents.massimworld.game.task;

import massim.javaagents.massimworld.actions.MassimAction;
import massim.javaagents.massimworld.agent.MassimTeam4Agent;

import static massim.javaagents.massimworld.game.task.TaskState.CANCELED;
import static massim.javaagents.massimworld.game.task.TaskState.FINISHED;

public abstract class MassimTask {

    protected final String name;

    protected TaskState taskState;

    public MassimTask(String name) {
        this.name = name;

    }

    public String getName() {
        return name;
    }

    public boolean isFinished(MassimTeam4Agent agent) {
        return taskState == FINISHED;
    }

    protected void setFinished() {
        taskState = FINISHED;
    }

    public abstract void update(MassimTeam4Agent agent);

    public abstract boolean hasSubtask();

    public abstract MassimTask getCurrentSubtask();

    protected void setCanceled() {
        taskState = CANCELED;
    }

    public boolean isCanceled() {
        return taskState == CANCELED;
    }

    public abstract void replan(MassimTeam4Agent agent);

    public abstract MassimAction getNextAction(MassimTeam4Agent agent);

    public abstract int getStepEstimation();

    public boolean isFeasible() {
        return true;
    }

    @Override
    public String toString() {
        return "MassimTask{" +
                "name='" + name + '\'' +
                ", taskState=" + taskState +
                '}';
    }
}
