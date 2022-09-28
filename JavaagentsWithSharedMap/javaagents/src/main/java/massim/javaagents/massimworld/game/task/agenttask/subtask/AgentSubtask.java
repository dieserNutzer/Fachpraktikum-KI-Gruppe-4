package massim.javaagents.massimworld.game.task.agenttask.subtask;

import massim.javaagents.massimworld.agent.MassimTeam4Agent;
import massim.javaagents.massimworld.game.task.MassimTask;
import massim.javaagents.massimworld.game.task.agenttask.AgentTask;

public abstract class AgentSubtask extends AgentTask {


    public AgentSubtask(String name) {
        super(name);
    }

    @Override
    public boolean hasSubtask() {
        return false;
    }

    @Override
    public MassimTask getCurrentSubtask() {
        return null;
    }

    @Override
    public void replan(MassimTeam4Agent agent) {}


    @Override
    public int getStepEstimation() {
        return 1;
    }
}
