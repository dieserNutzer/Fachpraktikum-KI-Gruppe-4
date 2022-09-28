package massim.javaagents.massimworld.game.task;

import massim.javaagents.massimworld.actions.MassimAction;
import massim.javaagents.massimworld.agent.MassimTeam4Agent;

public abstract class MassimTask {

    protected final String name;

    protected boolean finished = false;

    protected boolean cancelled = false;

    public MassimTask(String name) {
        this.name = name;

    }

    public String getName() {
        return name;
    }

    public boolean isFinished(MassimTeam4Agent agent) {
        return finished;
    }

    protected void setFinished() {
        finished = true;
    }

    public abstract void update(MassimTeam4Agent agent);

    public abstract boolean hasSubtask();

    public abstract MassimTask getCurrentSubtask();

    protected void cancelTask() {
        cancelled = true;
    }

    public boolean isCanceled() {
        return cancelled;
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
                ", finished=" + finished +
                '}';
    }
}
