package massim.javaagents.massimworld.planner;

import massim.javaagents.massimworld.agent.MassimTeam4Agent;
import massim.javaagents.massimworld.game.strategy.GameStrategy;
import massim.javaagents.massimworld.game.task.MassimTask;
import massim.javaagents.massimworld.game.task.agenttask.ExplorationTask;
import massim.javaagents.massimworld.map.Direction;
import massim.javaagents.massimworld.map.MassimMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * The task planner for a group of agents sharing a {@link MassimMap}.
 * Maps every contained agent to his current task.
 * Uses this map for further task planning.
 */
public class TaskPlanner {

    private static final Logger LOG = LoggerFactory.getLogger(TaskPlanner.class);

    Map<MassimTeam4Agent, MassimTask> tasksByAgent = new HashMap<>();

    private MassimMap massimMap;

    public TaskPlanner(MassimTeam4Agent agent) {
        tasksByAgent.put(agent, new ExplorationTask(Direction.getNext()));
        massimMap = agent.getMap();
    }

    public Map<MassimTeam4Agent, MassimTask> getTasksByAgent() {
        return tasksByAgent;
    }

    public MassimTask getTaskByAgent(MassimTeam4Agent agent) {
        return tasksByAgent.get(agent);
    }

    public void planTaskforAgentsWithRuleSet() {
//        GameStrategy.applyRules(massimMap, tasksByAgent);

        tasksByAgent.keySet().forEach(agent -> {
            MassimTask massimTask = GameStrategy.applyRules(agent);
            if (massimTask != null) {
                tasksByAgent.put(agent, massimTask);
            }
        });
    }

    /**
     * Joins the other TaskPlanner to the current one,
     * @param otherTaskPlanner
     */
    public void joinTaskPlanner(TaskPlanner otherTaskPlanner) {
        tasksByAgent.putAll(otherTaskPlanner.getTasksByAgent());
        otherTaskPlanner
                .getTasksByAgent()
                .keySet()
                .forEach(otherAgent -> {
                    otherAgent.setTaskPlanner(this);
                });
        otherTaskPlanner.invalidate();

    }

    protected void invalidate() {
        tasksByAgent = null;
    }

}
