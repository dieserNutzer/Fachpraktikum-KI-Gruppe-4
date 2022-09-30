package massim.javaagents.massimworld.game.task.agenttask.subtask;

import massim.javaagents.massimworld.actions.ConnectAction;
import massim.javaagents.massimworld.actions.MassimAction;
import massim.javaagents.massimworld.agent.MassimTeam4Agent;
import massim.javaagents.massimworld.game.task.TaskRequirement;
import massim.javaagents.massimworld.map.Direction;

import static massim.javaagents.massimworld.actions.ActionResult.SUCCESS;
import static massim.javaagents.massimworld.actions.ActionType.CONNECT;

public class ConnectSubtask extends AgentSubtask {

    MassimTeam4Agent otherAgent;

    TaskRequirement requirement;

    public ConnectSubtask(MassimTeam4Agent otherAgent, TaskRequirement requirement) {
        super("connectSubtask");
        this.otherAgent = otherAgent;
        this.requirement = requirement;
    }

    @Override
    public void update(MassimTeam4Agent agent) {
        if (CONNECT.equals(agent.getAgentState().getLastActionType()) &&
            SUCCESS.equals(agent.getAgentState().getLastActionResult())) {
            setFinished();
        }
    }

    @Override
    public MassimAction getNextAction(MassimTeam4Agent agent) {
        Direction direction = null;
        return new ConnectAction(otherAgent, requirement.getRelPosition());
    }
}
