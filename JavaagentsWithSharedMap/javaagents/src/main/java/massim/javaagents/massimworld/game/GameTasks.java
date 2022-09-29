package massim.javaagents.massimworld.game;

import massim.javaagents.massimworld.game.task.gametask.GameTask;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Container for {@link GameTask}s.
 */
public class GameTasks {

    Set<GameTask> tasks = new HashSet<>();

    /**
     * Returns the list of active game tasks at the given simulation step.
     * @param step the simulation step of the request
     * @return List of active {@link GameTask}s
     */
    public List<GameTask> getActiveTasks(int step)  {
        List<GameTask> activeTasks = tasks.stream()
                .filter(task -> task.getDeadline() > step)
                .collect(Collectors.toList());
        return activeTasks;
    }

    public void addTask(GameTask task) {
        tasks.add(task);
    }

    public Set<GameTask> getTasks() {
        return tasks;
    }
}
