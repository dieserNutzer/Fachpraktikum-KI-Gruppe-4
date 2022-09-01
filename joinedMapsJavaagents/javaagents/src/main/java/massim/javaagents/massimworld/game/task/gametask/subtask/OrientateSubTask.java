package massim.javaagents.massimworld.game.task.gametask.subtask;

import massim.javaagents.massimworld.actions.MassimAction;
import massim.javaagents.massimworld.agent.MassimTeam4Agent;
import massim.javaagents.massimworld.map.Coordinates;

public class OrientateSubTask extends Subtask {

    Coordinates relativeBlockPosition;

    public OrientateSubTask(Coordinates relativeBlockPosition) {
        this.relativeBlockPosition = relativeBlockPosition;
    }

    public Coordinates getBlockOrientation() {
        return relativeBlockPosition;
    }

    @Override
    public MassimAction getAction(MassimTeam4Agent agent) {
        return null;
    }


}
