package massim.javaagents.massimworld.game;

import massim.javaagents.massimworld.game.task.MassimTask;
import massim.javaagents.massimworld.game.task.gametask.GameTask;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GameTasks {

    List<GameTask> tasks = new ArrayList<>();

    public List<GameTask> getActiveTasks(int step)  {
        List<GameTask> activeTasks = tasks.stream()
                .filter(task -> task.getDeadline() < step)
                .collect(Collectors.toList());
        tasks = activeTasks;
        return tasks;
    }

    public void addTask(GameTask task) {
        tasks.add(task);
    }


}
