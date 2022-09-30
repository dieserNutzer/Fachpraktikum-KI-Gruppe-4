package massim.javaagents.massimworld.game.strategy.rule;

import massim.javaagents.massimworld.agent.MassimTeam4Agent;
import massim.javaagents.massimworld.game.task.MassimTask;
import massim.javaagents.massimworld.map.MassimMap;

import java.util.Map;

/**
 *  GameRules should lead the task planning
 */
public abstract class GameRule {

    protected String ruleName;

    protected int priority;

    protected GameRule(String ruleName, int priority) {
        this.ruleName = ruleName;
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    public String getRuleName() {
        return ruleName;
    }

    /**
     * Checks if the given concrete rule is applicable for the agent.
     * This may include conditions of the given game state,
     * the agent state, his current tasks, and conditions of the agent's map.
     * @param agent the agent for whom the applicability is checked.
     * @return true iff the given rule is applicable
     */
    public abstract boolean isFeasible(MassimTeam4Agent agent);

    /**
     * Applies the given concrete rule to the agent and give him the resulting task.
     * @param agent the agent to whom the rule is applied.
     * @return the resulting task of the concrete rule.
     */
    public abstract MassimTask apply(MassimTeam4Agent agent);

    /**
     * Returns true iff the current rule is feasible for a {@link MassimMap} and a task assigment
     * @param massimMap the massimMap of the agents given by the task assigment
     * @param tasksByAgent the current task assigment
     * @return true iff the task is feasible
     */
    public boolean isFeasible(MassimMap massimMap, Map<MassimTeam4Agent, MassimTask> tasksByAgent) {
        return false;
    }

    /**
     * Applies the given concrete rule to the agent and give him the resulting task.
     * @param massimMap the agent to whom the rule is applied.
     * @return the resulting task of the concrete rule.
     */
    public MassimTask apply(MassimMap massimMap, Map<MassimTeam4Agent, MassimTask> tasksByAgent) {
        return null;
    };
}
