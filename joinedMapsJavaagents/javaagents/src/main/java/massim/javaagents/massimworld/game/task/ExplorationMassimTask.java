package massim.javaagents.massimworld.game.task;

import massim.javaagents.massimworld.game.Game;
import massim.javaagents.massimworld.game.task.subtask.ExplorationSubtask;
import massim.javaagents.massimworld.game.task.subtask.Subtask;
import massim.javaagents.massimworld.map.Direction;

import java.util.ArrayList;
import java.util.List;

public class ExplorationMassimTask extends MassimTask {

    List<Subtask> subtasks = List.of(new ExplorationSubtask());

    Direction direction = Direction.getRandomDirection();

    Direction subdirection = null;//Direction.getRandomOrthogonalDirection(direction);
    public ExplorationMassimTask() {
        super("exploration", Game.game().getSteps(), 0, new ArrayList<>());
    }

    Subtask getNextSubtask() {
        return subtasks.get(0);
    }
}
