package massim.javaagents.massimworld.game.strategy.rule;

import massim.javaagents.massimworld.agent.MassimTeam4Agent;
import massim.javaagents.massimworld.game.task.MassimTask;
import massim.javaagents.massimworld.game.task.agenttask.ExplorationTask;
import massim.javaagents.massimworld.map.Direction;

public class TaskFinishedRemoveTaskRule extends GameRule {

    public TaskFinishedRemoveTaskRule(int priority) {
        super("taskFinishedRemoveRule", priority);
    }

    @Override
    public boolean isFeasible(MassimTeam4Agent agent) {
        return agent.getCurrentTask().isFinished(agent);
    }

    @Override
    public MassimTask apply(MassimTeam4Agent agent) {
        return new ExplorationTask(Direction.getNext());
    }
}
