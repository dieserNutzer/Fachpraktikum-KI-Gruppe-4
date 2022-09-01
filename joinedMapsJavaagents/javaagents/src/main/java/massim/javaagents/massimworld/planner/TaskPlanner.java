package massim.javaagents.massimworld.planner;

import massim.javaagents.massimworld.agent.MassimTeam4Agent;
import massim.javaagents.massimworld.game.Game;
import massim.javaagents.massimworld.game.task.MassimTask;
import massim.javaagents.massimworld.game.task.gametask.GameTaskCostEvaluator;
import massim.javaagents.massimworld.game.task.gametask.OneBlockTask;
import massim.javaagents.massimworld.game.task.generaltask.ExplorationTask;
import massim.javaagents.massimworld.map.Direction;
import massim.javaagents.massimworld.map.MassimMap;

import java.util.*;

public class TaskPlanner {

    Map<MassimTeam4Agent, MassimTask> tasksByAgent = new HashMap<>();

    int stepOfLastTaskAssignment = 0;

    private MassimMap massimMap;

    public TaskPlanner(MassimTeam4Agent agent) {
        tasksByAgent.put(agent, new ExplorationTask(Direction.getNext()));
        massimMap = agent.getMap();
    }

    private MassimMap getMassimMap() {
        return massimMap;
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

    public void planTaskforAgents() {
        // evaluate current tasks
        tasksByAgent.keySet().forEach(agent -> {
            if (tasksByAgent.get(agent) == null) {
                throw new IllegalStateException("agent has no task: " + agent.getName());
            }
        });

        // evaluate available tasks
        if (!massimMap.containsGoalZone()) {
            int i = 1;// keep going
//            for (MassimTeam4Agent agent: tasksByAgent.keySet()) {
//                tasksByAgent.put(agent, new ExplorationTask(Direction.getNext()));
//            }
        } else if (!Game.game().getOneBlockTasks().isEmpty()) {
            for (MassimTeam4Agent agent: tasksByAgent.keySet()) {
                List<OneBlockTask> oneBlockTasks = new ArrayList<>();
                for (OneBlockTask oneBlockTask: Game.game().getOneBlockTasks()) {
                    OneBlockTask copy = oneBlockTask.copy();
                    int costs = GameTaskCostEvaluator.planOneBlockTask(copy, agent);
                    if (costs > -1) {
                        oneBlockTasks.add(copy);
                    }
                }
                if (!oneBlockTasks.isEmpty()) {
                    OneBlockTask agentTask = oneBlockTasks.stream()
                            .sorted(Comparator.comparing(OneBlockTask::getEstimatedSteps).reversed())
                            .findFirst().get();
                    tasksByAgent.put(agent, agentTask);
                }
            }
        } else {
            int j = 1;
        }


        // apply rules



    }
//
//    public List<MoveAction> planRouteForAgentToTarget(MassimTeam4Agent agent, Set<Coordinates> target) {
//        Problem<Coordinates, MoveAction> problem = new GeneralProblem<>(
//                agent.getMap().getAgentPositionByAgent(agent),
//                MassimFunctions.createActionsFunction(getMassimMap()),
//                MassimFunctions.createResultFunction(getMassimMap()),
//                target::contains);
//        SearchForActions<Coordinates, MoveAction> search =
//                new AStarSearch<>(new GraphSearch<>(), MassimFunctions.createManhattanDistanceFunction(target));
//        Optional<List<MoveAction>> actions = search.findActions(problem);
//
//        return actions.orElse(Collections.emptyList());
//    }


    public void joinTaskPlanner(TaskPlanner otherTaskPlanner) {
        tasksByAgent.putAll(otherTaskPlanner.getTasksByAgent());
        otherTaskPlanner
                .getTasksByAgent()
                .keySet()
                .forEach(otherAgent -> {
                    otherAgent.setTaskPlanner(this);
                });
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
