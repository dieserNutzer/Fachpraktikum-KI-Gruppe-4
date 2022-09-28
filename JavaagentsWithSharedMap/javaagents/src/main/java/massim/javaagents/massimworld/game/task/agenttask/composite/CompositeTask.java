package massim.javaagents.massimworld.game.task.agenttask.composite;

import massim.javaagents.massimworld.actions.MassimAction;
import massim.javaagents.massimworld.actions.SkipAction;
import massim.javaagents.massimworld.agent.MassimTeam4Agent;
import massim.javaagents.massimworld.game.task.MassimTask;
import massim.javaagents.massimworld.game.task.agenttask.AgentTask;

import java.util.ArrayList;
import java.util.List;

public abstract class CompositeTask extends AgentTask {

    protected List<AgentTask> agentTasks = new ArrayList<>();

    public CompositeTask(String name, List<AgentTask> agentTasks) {
        super(name);
        this.agentTasks = agentTasks;
    }

    public List<AgentTask> getAgentTasks() {
        return agentTasks;
    }

    public void setAgentTasks(List<AgentTask> agentTasks) {
        this.agentTasks = agentTasks;
    }

    @Override
    public void update(MassimTeam4Agent agent) {
        if (agentTasks != null && !agentTasks.isEmpty()) {
            agentTasks.get(0).update(agent);
            if (agentTasks.get(0).isFinished(agent)) {
                agentTasks.remove(0);
                if (agentTasks.isEmpty()) {
                    setFinished();
                }
            } else if (agentTasks.get(0).isCanceled()) {
                cancelTask();
            }
        } else {
            setFinished();
        }
    }

    @Override
    public boolean hasSubtask() {
        return !agentTasks.isEmpty();
    }

    @Override
    public MassimTask getCurrentSubtask() {
        return agentTasks.get(0);
    }

    @Override
    public void replan(MassimTeam4Agent agent) {

    }

    @Override
    public MassimAction getNextAction(MassimTeam4Agent agent) {
        if (agentTasks.isEmpty() || isFinished(agent) || isCanceled()) {
            setFinished();
            return new SkipAction();
        }
        return agentTasks.get(0).getNextAction(agent);
    }

    @Override
    public int getStepEstimation() {
        return agentTasks.stream().mapToInt(AgentTask::getStepEstimation).sum();
    }
}
