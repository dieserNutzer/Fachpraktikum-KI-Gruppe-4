package massim.javaagents.massimworld.game.task.subtask;

import eis.iilang.Action;
import massim.javaagents.massimworld.agent.MassimTeam4Agent;

public abstract class Subtask {

    public abstract Action getAction(MassimTeam4Agent agent);
}
