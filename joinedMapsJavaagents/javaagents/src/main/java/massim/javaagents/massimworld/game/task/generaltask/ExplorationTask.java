package massim.javaagents.massimworld.game.task.generaltask;

import massim.javaagents.massimworld.actions.ClearAction;
import massim.javaagents.massimworld.actions.MassimAction;
import massim.javaagents.massimworld.actions.MoveAction;
import massim.javaagents.massimworld.agent.MassimTeam4Agent;
import massim.javaagents.massimworld.game.Game;
import massim.javaagents.massimworld.game.task.MassimTask;
import massim.javaagents.massimworld.game.task.gametask.subtask.Subtask;
import massim.javaagents.massimworld.map.Direction;
import massim.javaagents.massimworld.map.MassimMap;

import java.util.ArrayList;
import java.util.List;

public class ExplorationTask extends MassimTask {

    Direction direction;//= Direction.getRandomDirection();

    Direction subdirection = null;//Direction.getRandomOrthogonalDirection(direction);
    public ExplorationTask(Direction dir) {
        super("exploration");
        direction = dir;
    }

    public MassimAction getNextAction(MassimTeam4Agent agent, MassimMap massimMap) {
        if (agent.getAdjacentCell(direction).isAllowed()) {
            return new MoveAction(direction);
        } else {
            return new ClearAction(direction);
        }
    }
}
