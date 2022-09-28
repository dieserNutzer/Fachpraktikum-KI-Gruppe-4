package massim.javaagents.massimworld.game.strategy.rule;

import massim.javaagents.massimworld.agent.MassimTeam4Agent;
import massim.javaagents.massimworld.game.Game;
import massim.javaagents.massimworld.game.role.RoleType;
import massim.javaagents.massimworld.game.task.MassimTask;
import massim.javaagents.massimworld.game.task.agenttask.ExplorationTask;
import massim.javaagents.massimworld.game.task.agenttask.composite.AcquireRoleTask;
import massim.javaagents.massimworld.game.task.gametask.GameTaskCostEvaluator;
import massim.javaagents.massimworld.game.task.gametask.OneBlockTask;
import massim.javaagents.massimworld.map.Coordinates;
import massim.javaagents.massimworld.percepts.MassimPerceptReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class OneBlockTaskRule extends GameRule {

    private static final Logger LOG = LoggerFactory.getLogger(OneBlockTaskRule.class);

    public OneBlockTaskRule(int priority) {
        super("oneBlockTaskRule", priority);
    }

    @Override
    public boolean isFeasible(MassimTeam4Agent agent) {
        if (agent.getMap().containsGoalZone() &&
            agent.getCurrentRoleType() == RoleType.WORKER &&
            (agent.getCurrentTask() instanceof ExplorationTask || agent.getCurrentTask().isFinished(agent) || !agent.getCurrentTask().isFeasible() || agent.getCurrentTask().isCanceled()) &&
            !Game.game().getOneBlockTasks().isEmpty()) {

            List<OneBlockTask> oneBlockTasks = new ArrayList<>();
            for (OneBlockTask oneBlockTask : Game.game().getOneBlockTasks()) {
                OneBlockTask currentOneBlockTask = GameTaskCostEvaluator.getFeasibleOneBlockTask(oneBlockTask, agent);
                oneBlockTasks.add(currentOneBlockTask);
            }
            return !oneBlockTasks.isEmpty();
        } else {
            return false;
        }
    }

    @Override
    public MassimTask apply(MassimTeam4Agent agent) {
        List<OneBlockTask> oneBlockTasks = new ArrayList<>();
        for (OneBlockTask oneBlockTask : Game.game().getOneBlockTasks()) {
            OneBlockTask currentBlockTask = GameTaskCostEvaluator.getFeasibleOneBlockTask(oneBlockTask, agent);
            if (currentBlockTask == null) {
                continue;
            }
            oneBlockTasks.add(currentBlockTask);
        }

//        List<OneBlockTask> oneBlockTasks = Game.game().getOneBlockTasks().stream()
//                .map(task -> GameTaskCostEvaluator.getFeasibleOneBlockTask(task, agent))
//                .sorted(Comparator.comparing(OneBlockTask::getStepEstimation))
//                .toList();

        if (oneBlockTasks.isEmpty()) {
            LOG.warn("OneBlockTaskRule apply returns no task for agent {} ", agent.getName());
            return null;
        } else {
            List<OneBlockTask> descOneBlockTasks = oneBlockTasks.stream().sorted(Comparator.comparing(OneBlockTask::getTaskValue).reversed()).toList();
            return descOneBlockTasks.get(0);
        }
    }
}
