package massim.javaagents.massimworld.game.task.gametask.subtask;

import massim.javaagents.massimworld.actions.MassimAction;
import massim.javaagents.massimworld.agent.MassimTeam4Agent;

public class RequestBlockFromDispenserSubtask extends Subtask {

    String blockType;

    public RequestBlockFromDispenserSubtask(String blockType) {
        this.blockType = blockType;
    }

    @Override
    public MassimAction getAction(MassimTeam4Agent agent) {
        return null;
    }

    public String getBlockType() {
        return blockType;
    }
}
