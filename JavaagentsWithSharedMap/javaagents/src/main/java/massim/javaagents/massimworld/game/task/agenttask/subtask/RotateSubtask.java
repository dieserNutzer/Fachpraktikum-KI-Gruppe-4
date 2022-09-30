package massim.javaagents.massimworld.game.task.agenttask.subtask;

import massim.javaagents.massimworld.actions.MassimAction;
import massim.javaagents.massimworld.actions.RotateAction;
import massim.javaagents.massimworld.actions.Rotation;
import massim.javaagents.massimworld.actions.SkipAction;
import massim.javaagents.massimworld.agent.MassimTeam4Agent;
import massim.javaagents.massimworld.map.Coordinates;

import static massim.javaagents.massimworld.actions.ActionResult.SUCCESS;
import static massim.javaagents.massimworld.actions.ActionType.ROTATE;

public class RotateSubtask extends AgentSubtask {

    Coordinates relativeBlockPosition;

    Rotation rotation;

    public RotateSubtask() {
        super("rotateSubtask");
    }

    public RotateSubtask(Coordinates relativeBlockPosition) {
        super("rotateSubtask");
        rotation = Rotation.CCW;
        this.relativeBlockPosition = relativeBlockPosition;
    }

    @Override
    public void update(MassimTeam4Agent agent) {
        if (ROTATE.equals(agent.getAgentState().getLastActionType()) && !SUCCESS.equals(agent.getAgentState().getLastActionResult())) {
            rotation = Rotation.getOppositeRotation(rotation);
        }
        if (agent.hasAttachedBlock() && relativeBlockPosition.equals(agent.getAttachedBlockRelativePosition())) {
            setFinished();
        }
    }

    @Override
    public MassimAction getNextAction(MassimTeam4Agent agent) {
        if (agent.hasAttachedBlock() && relativeBlockPosition.equals(agent.getAttachedBlockRelativePosition())) {
            setFinished();
            return new SkipAction();
        }
        return new RotateAction(Rotation.CW);
    }
}
