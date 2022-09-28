package massim.javaagents.massimworld.game.strategy.rule;

import massim.javaagents.massimworld.agent.MassimTeam4Agent;
import massim.javaagents.massimworld.game.task.MassimTask;
import massim.javaagents.massimworld.game.task.agenttask.ExplorationTask;
import massim.javaagents.massimworld.map.Direction;

public class TaskNotFeasibleRemoveTaskRule extends GameRule{

    public TaskNotFeasibleRemoveTaskRule(int priority) {
        super("taskNotFeasibleRemoveRule",priority);
    }

    @Override
    public boolean isFeasible(MassimTeam4Agent agent) {
        return !agent.getCurrentTask().isFeasible();
    }

    @Override
    public MassimTask apply(MassimTeam4Agent agent) {
        return new ExplorationTask(Direction.getNext());
    }
}
