package massim.javaagents.massimworld.game.task.gametask.subtask;

import massim.javaagents.massimworld.actions.MassimAction;
import massim.javaagents.massimworld.agent.MassimTeam4Agent;

public abstract class Subtask {

    public abstract MassimAction getAction(MassimTeam4Agent agent);
}
