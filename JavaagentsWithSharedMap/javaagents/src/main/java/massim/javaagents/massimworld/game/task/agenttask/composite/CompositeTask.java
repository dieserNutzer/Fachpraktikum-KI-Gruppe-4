package massim.javaagents.massimworld.game.task.agenttask.composite;

import massim.javaagents.massimworld.actions.MassimAction;
import massim.javaagents.massimworld.actions.SkipAction;
import massim.javaagents.massimworld.agent.MassimTeam4Agent;
import massim.javaagents.massimworld.game.task.MassimTask;
import massim.javaagents.massimworld.game.task.agenttask.AgentTask;

import java.util.ArrayList;
import java.util.List;

/**
 * A task that is composed of different subtasks.
 */
public abstract class CompositeTask extends AgentTask {

    protected List<AgentTask> subtasks = new ArrayList<>();

    public CompositeTask(String name, List<AgentTask> subtasks) {
        super(name);
        this.subtasks = subtasks;
    }

    public List<AgentTask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(List<AgentTask> subtasks) {
        this.subtasks = subtasks;
    }

    @Override
    public void update(MassimTeam4Agent agent) {
        if (subtasks != null && !subtasks.isEmpty()) {
            subtasks.get(0).update(agent);
            if (subtasks.get(0).isFinished(agent)) {
                subtasks.remove(0);
                if (subtasks.isEmpty()) {
                    setFinished();
                }
            } else if (subtasks.get(0).isCanceled()) {
                setCanceled();
            }
        } else {
            setFinished();
        }
    }

    @Override
    public boolean hasSubtask() {
        return !subtasks.isEmpty();
    }

    @Override
    public MassimTask getCurrentSubtask() {
        return subtasks.get(0);
    }

    @Override
    public void replan(MassimTeam4Agent agent) {

    }

    @Override
    public MassimAction getNextAction(MassimTeam4Agent agent) {
        if (subtasks.isEmpty() || isFinished(agent) || isCanceled()) {
            setFinished();
            return new SkipAction();
        }
        return subtasks.get(0).getNextAction(agent);
    }

    @Override
    public int getStepEstimation() {
        return subtasks.stream().mapToInt(AgentTask::getStepEstimation).sum();
    }
}
