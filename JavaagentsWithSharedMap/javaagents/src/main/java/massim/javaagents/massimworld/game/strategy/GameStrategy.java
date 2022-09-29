package massim.javaagents.massimworld.game.strategy;

import massim.javaagents.massimworld.agent.MassimTeam4Agent;
import massim.javaagents.massimworld.game.strategy.rule.*;
import massim.javaagents.massimworld.game.task.MassimTask;
import massim.javaagents.massimworld.map.MassimMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class GameStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(GameStrategy.class);

    private static List<GameRule> gameRules = List.of(
//            new TaskFinishedRemoveTaskRule(200),
//            new TaskCanceledRemoveTaskRule(200),
//            new TaskNotFeasibleRemoveTaskRule(200),

//            new TwoBlockTaskRule(200),

            new OneBlockTaskRule(100),
            new AcquireWorkerRule(100),

            new ExplorationRule(10)
    );

    public static void applyRules(MassimMap massimMap, Map<MassimTeam4Agent, MassimTask> tasksByAgent) {

        for (GameRule rule : gameRules) {
            boolean applyRules = true;
            while (applyRules) {
                if (rule.isFeasible(massimMap, tasksByAgent)) {
                    MassimTask task = rule.apply(massimMap, tasksByAgent);
                    if (task != null) {
                        LOG.info("rule: {} -> task {}", rule.getRuleName(), task.getName());
                    } else {
                        LOG.info("rule: {} - no more apllications found", rule.getRuleName());
                        applyRules = false;
                    }
                } else {
                    applyRules = false;
                }
            }
        }
    }

    public static MassimTask applyRules(MassimTeam4Agent agent) {
        for (GameRule rule: gameRules) {
            if (rule.isFeasible(agent)) {
                MassimTask task = rule.apply(agent);
                if (task != null) {
                    LOG.info("rule: {} agent: {} -> task {}", rule.getRuleName(), agent.getName(), task.getName());
                }
                return task;
            }
        }
        return null;
    }

}
