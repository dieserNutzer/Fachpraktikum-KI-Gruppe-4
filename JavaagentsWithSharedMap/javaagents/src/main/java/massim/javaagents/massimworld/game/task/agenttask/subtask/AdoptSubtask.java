package massim.javaagents.massimworld.game.task.agenttask.subtask;

import massim.javaagents.massimworld.actions.AdoptAction;
import massim.javaagents.massimworld.actions.MassimAction;
import massim.javaagents.massimworld.agent.MassimTeam4Agent;
import massim.javaagents.massimworld.game.role.RoleType;
import massim.javaagents.massimworld.game.task.MassimTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static massim.javaagents.massimworld.actions.ActionResult.SUCCESS;
import static massim.javaagents.massimworld.actions.ActionType.ADOPT;

public class AdoptSubtask extends AgentSubtask {

    private static final Logger LOG = LoggerFactory.getLogger(AdoptSubtask.class);

    RoleType roleType;

    public AdoptSubtask(RoleType roleType) {
        super("adoptSubtask");
        this.roleType = roleType;
    }

    public RoleType getRoleType() {
        return roleType;
    }

    @Override
    public void update(MassimTeam4Agent agent) {
        if ((ADOPT.equals(agent.getAgentState().getLastActionType()) && SUCCESS.equals(agent.getAgentState().getLastActionResult()))
                || agent.getAgentState().getRole().getRoleType().equals(roleType)) {
            setFinished();
        } else if (ADOPT.equals(agent.getAgentState().getLastActionType()) && !SUCCESS.equals(agent.getAgentState().getLastActionResult())) {
            LOG.error("adoptSubtask failed with error {} and finished unsuccessfully", agent.getAgentState().getLastActionResult());
            setFinished();
        }
    }

    @Override
    public boolean hasSubtask() {
        return false;
    }

    @Override
    public MassimTask getCurrentSubtask() {
        return null;
    }

    @Override
    public void replan(MassimTeam4Agent agent) {}

    @Override
    public MassimAction getNextAction(MassimTeam4Agent agent) {
        return new AdoptAction(roleType);
    }

    @Override
    public int getStepEstimation() {
        return 1;
    }
}
