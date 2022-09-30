package massim.javaagents.massimworld.game.task.agenttask.subtask;

import massim.javaagents.massimworld.actions.AttachAction;
import massim.javaagents.massimworld.actions.MassimAction;
import massim.javaagents.massimworld.agent.MassimTeam4Agent;
import massim.javaagents.massimworld.map.Direction;
import massim.javaagents.massimworld.map.things.BlockType;

import static massim.javaagents.massimworld.actions.ActionResult.SUCCESS;
import static massim.javaagents.massimworld.actions.ActionType.ATTACH;

public class AttachSubtask extends AgentSubtask {

    private final BlockType blockType;

    public AttachSubtask(BlockType blockType) {
        super("attachSubtask");
        this.blockType = blockType;
    }

    @Override
    public void update(MassimTeam4Agent agent) {
        if (ATTACH.equals(agent.getAgentState().getLastActionType()) &&
            SUCCESS.equals(agent.getAgentState().getLastActionResult())) {
            setFinished();
        }
    }

    @Override
    public MassimAction getNextAction(MassimTeam4Agent agent) {
        Direction direction = agent.getMap().getAdjacentDispenserDirection(agent.getCurrentCoordinates(), blockType);
        return new AttachAction(direction);
    }
}
