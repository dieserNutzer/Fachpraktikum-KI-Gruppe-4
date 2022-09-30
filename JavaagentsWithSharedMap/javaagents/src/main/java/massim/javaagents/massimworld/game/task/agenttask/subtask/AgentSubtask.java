package massim.javaagents.massimworld.game.task.agenttask.subtask;

import massim.javaagents.massimworld.actions.MassimAction;
import massim.javaagents.massimworld.agent.MassimTeam4Agent;
import massim.javaagents.massimworld.game.task.MassimTask;
import massim.javaagents.massimworld.game.task.agenttask.AgentTask;
import massim.javaagents.massimworld.game.task.agenttask.composite.CompositeTask;
import massim.javaagents.massimworld.map.MassimMap;


/**
 * Class describing a subtask to achieve a {@link CompositeTask}.
 * Mainly tasks of this type are corresponding one-to-one to the execution of an {@link MassimAction},
 * where the parameters have to be inferred by the agent on his own (by use of his {@link MassimMap}).
 *
 */
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
