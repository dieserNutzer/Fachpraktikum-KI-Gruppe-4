package massim.javaagents.massimworld.game.strategy.rule;

import massim.javaagents.massimworld.agent.MassimTeam4Agent;
import massim.javaagents.massimworld.game.role.RoleType;
import massim.javaagents.massimworld.game.task.MassimTask;
import massim.javaagents.massimworld.game.task.agenttask.composite.AcquireRoleTask;
import massim.javaagents.massimworld.game.task.gametask.GameTaskCostEvaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static massim.javaagents.massimworld.game.role.RoleType.WORKER;

public class AcquireWorkerRule extends GameRule {

    private static final Logger LOG = LoggerFactory.getLogger(AcquireWorkerRule.class);

    public AcquireWorkerRule(int priority) {
        super("acquireWorkerRule", priority);
    }

    @Override
    public boolean isFeasible(MassimTeam4Agent agent) {
        return !agent.getCurrentRoleType().equals(WORKER) &&
               agent.getMap().containsRoleZone() &&
               GameTaskCostEvaluator.planAcquireRoleTask(WORKER, agent) != null &&
               agent.getAgentState().getRole().getRoleType().equals(RoleType.DEFAULT) &&
               !agent.getCurrentTask().getName().equals("acquireRoleTask") &&
               !agent.getCurrentTask().getName().equals("oneBlockTask");
    }

    @Override
    public MassimTask apply(MassimTeam4Agent agent) {
        AcquireRoleTask task = GameTaskCostEvaluator.planAcquireRoleTask(WORKER, agent);
        if (task == null) {
            LOG.error("AcquireWorkerRule apply returns no task");
        }
        return task;
    }
}
