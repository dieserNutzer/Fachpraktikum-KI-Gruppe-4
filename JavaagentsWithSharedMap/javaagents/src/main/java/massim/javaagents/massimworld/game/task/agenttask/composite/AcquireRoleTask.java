package massim.javaagents.massimworld.game.task.agenttask.composite;

import massim.javaagents.massimworld.agent.MassimTeam4Agent;
import massim.javaagents.massimworld.game.role.RoleType;
import massim.javaagents.massimworld.game.task.agenttask.AgentTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Task to acquire a role of the given {@link RoleType}.
 */
public class AcquireRoleTask extends CompositeTask {

    private final RoleType roleType;

    public AcquireRoleTask(RoleType roleType, List<AgentTask> agentTasks) {
        super("acquireRoleTask", agentTasks);
        this.roleType = roleType;
        super.subtasks = agentTasks;
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
