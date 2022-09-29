package massim.javaagents.massimworld.game.task.agenttask.subtask;

import massim.javaagents.massimworld.actions.AttachAction;
import massim.javaagents.massimworld.actions.DetachAction;
import massim.javaagents.massimworld.actions.MassimAction;
import massim.javaagents.massimworld.actions.NoActionAction;
import massim.javaagents.massimworld.agent.MassimTeam4Agent;
import massim.javaagents.massimworld.map.Direction;
import massim.javaagents.massimworld.map.things.BlockType;

import static massim.javaagents.massimworld.actions.ActionResult.SUCCESS;
import static massim.javaagents.massimworld.actions.ActionType.ATTACH;
import static massim.javaagents.massimworld.actions.ActionType.DETACH;

public class DetachSubtask extends AgentSubtask {

    private BlockType attachedBlockType;

    public DetachSubtask(BlockType attachedBlockType) {
        super("detachSubtask");
        this.attachedBlockType = attachedBlockType;
    }

    @Override
    public void update(MassimTeam4Agent agent) {
        if (DETACH.equals(agent.getAgentState().getLastActionType()) &&
            SUCCESS.equals(agent.getAgentState().getLastActionResult())) {
            setFinished();
        }
    }

    @Override
    public MassimAction getNextAction(MassimTeam4Agent agent) {
        if (agent.getFirstAttachedBlock(attachedBlockType) != null) {
            Direction direction = Direction.getByOffset(agent.getFirstAttachedBlock(attachedBlockType).getFirst());
            return new DetachAction(direction);
        } else {
            setCanceled();
            return new NoActionAction();
        }
    }
}
