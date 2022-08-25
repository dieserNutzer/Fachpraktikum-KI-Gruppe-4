package massim.javaagents.massimworld.game.task;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Tasks {

    List<MassimTask> tasks = new ArrayList<>();

    public List<MassimTask> getActiveTasks(int step)  {
        List<MassimTask> activeTasks = tasks.stream()
                .filter(task -> task.getDeadline() < step)
                .collect(Collectors.toList());
        tasks = activeTasks;
        return tasks;
    }

    public void addTask(MassimTask task) {
        tasks.add(task);
    }


}
