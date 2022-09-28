package massim.javaagents.massimworld.game.strategy.rule;

import massim.javaagents.massimworld.agent.MassimTeam4Agent;
import massim.javaagents.massimworld.game.role.RoleType;
import massim.javaagents.massimworld.game.task.MassimTask;
import massim.javaagents.massimworld.game.task.agenttask.ExplorationTask;
import massim.javaagents.massimworld.game.task.gametask.GameTaskCostEvaluator;
import massim.javaagents.massimworld.map.Direction;
import massim.javaagents.massimworld.map.MassimMap;

public class ExploreRule extends GameRule {

    public ExploreRule(int priority) {
        super("exploreRule", priority);
    }

    @Override
    public boolean isFeasible(MassimTeam4Agent agent) {
        MassimMap map = agent.getMap();
        return !map.containsGoalZone() &&
               (agent.getCurrentTask().isFinished(agent) || !agent.getCurrentTask().isFeasible() || agent.getCurrentTask().isCanceled()) &&
               !agent.getCurrentTask().getName().equals("explorationTask");
    }

    @Override
    public MassimTask apply(MassimTeam4Agent agent) {
        return new ExplorationTask(Direction.getNext());
    }
}
