package massim.javaagents.massimworld.game.task.agenttask.subtask;

import massim.javaagents.massimworld.actions.MassimAction;
import massim.javaagents.massimworld.actions.SubmitAction;
import massim.javaagents.massimworld.agent.MassimTeam4Agent;
import massim.javaagents.massimworld.game.task.MassimTask;

import static massim.javaagents.massimworld.actions.ActionResult.*;
import static massim.javaagents.massimworld.actions.ActionType.SUBMIT;

public class SubmitSubtask extends AgentSubtask {

    String taskName;
    public SubmitSubtask(String taskName) {
        super("submitSubtask");
        this.taskName = taskName;
    }

    @Override
    public void update(MassimTeam4Agent agent) {
        if (SUBMIT.equals(agent.getAgentState().getLastActionType()) &&
            SUCCESS.equals(agent.getAgentState().getLastActionResult())) {
            setFinished();
        } else if (SUBMIT.equals(agent.getAgentState().getLastActionType()) &&
                   FAILED.equals(agent.getAgentState().getLastActionResult()) ) {
            setCanceled();
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
        return new SubmitAction(taskName);
    }

    @Override
    public int getStepEstimation() {
        return 1;
    }

}
