package massim.javaagents.massimworld.game.task;

import massim.javaagents.massimworld.agent.MassimTeam4Agent;

import java.util.HashMap;
import java.util.Map;

public class TaskPlanner {

    Map<MassimTeam4Agent, MassimTask> tasksByAgent = new HashMap<>();

    public TaskPlanner(MassimTeam4Agent agent) {
        tasksByAgent.put(agent, new ExplorationMassimTask());
    }

    public TaskPlanner(Map<MassimTeam4Agent, MassimTask> tasksByAgent) {
        this.tasksByAgent.putAll(tasksByAgent);
    }

    public MassimTask getTaskForAgent(MassimTeam4Agent agent) {
        MassimTask task = tasksByAgent.getOrDefault(agent, null);
        if (task == null) {
            throw new IllegalArgumentException("AgentNotFound");
        }
        return task;
    }

    public void joinTaskPlanner(TaskPlanner otherTaskPlanner) {
        tasksByAgent.putAll(otherTaskPlanner.getTasksByAgent());
        otherTaskPlanner
                .getTasksByAgent()
                .keySet()
                .forEach(otherAgent -> {
                    otherAgent.setTaskPlanner(this);
                });
        otherTaskPlanner.getTasksByAgent().keySet().forEach(massimTeam4Agent -> {
            massimTeam4Agent.setTaskPlanner(this);
        }

        );
        otherTaskPlanner.invalidate();
    }

    protected void invalidate() {
        tasksByAgent = null;
    }

    public Map<MassimTeam4Agent, MassimTask> getTasksByAgent() {
        return tasksByAgent;
    }

    public MassimTask getTaskByAgent(MassimTeam4Agent agent) {
        return tasksByAgent.get(agent);
    }

}
