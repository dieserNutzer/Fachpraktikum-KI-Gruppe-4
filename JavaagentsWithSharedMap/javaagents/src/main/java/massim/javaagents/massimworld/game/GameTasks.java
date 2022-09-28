package massim.javaagents.massimworld.game;

import massim.javaagents.massimworld.game.task.gametask.GameTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GameTasks {

    private static final Logger LOG = LoggerFactory.getLogger(GameTasks.class);

    Set<GameTask> tasks = new HashSet<>();

    public List<GameTask> getActiveTasks(int step)  {
        List<GameTask> activeTasks = tasks.stream()
                .filter(task -> task.getDeadline() > step)
                .collect(Collectors.toList());
        activeTasks.stream().map(GameTask::getName).forEach(taskname -> {
//            LOG.info("got active task {} ", taskname);
        });
        return activeTasks;
    }

    public void addTask(GameTask task) {
        tasks.add(task);
    }

    public Set<GameTask> getTasks() {
        return tasks;
    }
}
