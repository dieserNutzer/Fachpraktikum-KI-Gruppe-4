package massim.javaagents.massimworld.game.task.agenttask.composite;

import massim.javaagents.massimworld.agent.MassimTeam4Agent;
import massim.javaagents.massimworld.game.role.RoleType;
import massim.javaagents.massimworld.game.task.agenttask.AgentTask;
import massim.javaagents.massimworld.game.task.gametask.GameTaskCostEvaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class AcquireRoleTask extends CompositeTask {

    private static final Logger LOG = LoggerFactory.getLogger(AcquireRoleTask.class);

    private final RoleType roleType;

    public AcquireRoleTask(RoleType roleType, List<AgentTask> agentTasks) {
        super("acquireRoleTask", agentTasks);
        this.roleType = roleType;
        super.agentTasks = agentTasks;
    }

    public AcquireRoleTask(RoleType roleType) {
        super("acquireRoleTask", new ArrayList<>());
        this.roleType = roleType;
    }


    public RoleType getRoleType() {
        return roleType;
    }



    @Override
    public void replan(MassimTeam4Agent agent) {

    }



}
