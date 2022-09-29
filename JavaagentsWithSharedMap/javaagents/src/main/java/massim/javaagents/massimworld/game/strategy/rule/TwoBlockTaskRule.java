package massim.javaagents.massimworld.game.strategy.rule;

import massim.javaagents.massimworld.agent.MassimTeam4Agent;
import massim.javaagents.massimworld.game.Game;
import massim.javaagents.massimworld.game.task.MassimTask;
import massim.javaagents.massimworld.game.task.TaskRequirement;
import massim.javaagents.massimworld.game.task.agenttask.AgentTask;
import massim.javaagents.massimworld.game.task.agenttask.composite.GetBlockToGoalZoneTask;
import massim.javaagents.massimworld.game.task.agenttask.subtask.*;
import massim.javaagents.massimworld.game.task.gametask.GameTaskCostEvaluator;
import massim.javaagents.massimworld.game.task.gametask.TwoBlockTask;
import massim.javaagents.massimworld.map.MassimMap;
import massim.javaagents.massimworld.map.things.BlockType;
import massim.javaagents.massimworld.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static massim.javaagents.massimworld.game.role.RoleType.WORKER;

public class TwoBlockTaskRule extends GameRule {

    private static final Logger LOG = LoggerFactory.getLogger(TwoBlockTaskRule.class);

    public TwoBlockTaskRule(int priority) {
        super("twoBlockTaskRule", priority);
    }

    Map<MassimMap, TwoBlockTask> twoBlockTasksByMassimMap = new HashMap<>();

    @Override
    public boolean isFeasible(MassimMap massimMap, Map<MassimTeam4Agent, MassimTask> tasksByAgent) {
        long startTime = System.currentTimeMillis();

        if (!massimMap.containsGoalZone() && !Game.game().getTwoBlockTasks().isEmpty()) {
            return false;
        }
        List<MassimTeam4Agent> workerAgents = massimMap.getAgentPositionsByAgent().keySet().stream().filter(agent -> agent.getCurrentRoleType() == WORKER).toList();

        if (workerAgents.size() < 2) {
            return false;
        }

        List<TwoBlockTask> twoBlockTasks = new ArrayList<>();
        for (TwoBlockTask twoBlockTask : Game.game().getTwoBlockTasks()) {
            List<TaskRequirement> adjacentRequirements = twoBlockTask.getRequirements().stream().filter(req -> req.getRelPosition().containsZeroAndOne()).toList();
            if (adjacentRequirements.size() != 1) {
                continue;
            }

            TaskRequirement adjacentRequirement = null;
            TaskRequirement secondRequirement = null;
            for (TaskRequirement requirement: twoBlockTask.getRequirements()) {
                if (requirement.getRelPosition().containsZeroAndOne()) {
                    adjacentRequirement = requirement;
                } else {
                    secondRequirement = requirement;
                }
            }
            if (adjacentRequirement == null || secondRequirement == null) {
                LOG.error("found requirement null adjacentRequirement: {}, secondRequirement: {}", adjacentRequirement, secondRequirement);
            }


            List<BlockType> blockTypes = twoBlockTask.getRequirements().stream().map(req -> req.getBlockType()).toList();

            List<Pair<MassimTeam4Agent, GetBlockToGoalZoneTask>> block1Subtasks = new ArrayList<>();
            BlockType blockType1 = blockTypes.get(0);

            for (MassimTeam4Agent agent : workerAgents) {
                Pair<MassimTeam4Agent, GetBlockToGoalZoneTask> currentTask = GameTaskCostEvaluator.getFeasibleGetBlockToGoalZoneTask(blockType1, agent);
                block1Subtasks.add(currentTask);
            }
            if (block1Subtasks.isEmpty()) {
                return false;
            }

            List<Pair<MassimTeam4Agent, GetBlockToGoalZoneTask>> block2Subtasks = new ArrayList<>();
            BlockType blockType2 = blockTypes.get(0);
            for (MassimTeam4Agent agent : workerAgents) {
                Pair<MassimTeam4Agent, GetBlockToGoalZoneTask> currentTask = GameTaskCostEvaluator.getFeasibleGetBlockToGoalZoneTask(blockType2, agent);
                block2Subtasks.add(currentTask);
            }

            if (block2Subtasks.isEmpty()) {
                return false;
            }

            block1Subtasks.stream().sorted(Comparator.comparing(pair -> pair.getSecond().getStepEstimation()));
            block2Subtasks.stream().sorted(Comparator.comparing(pair -> pair.getSecond().getStepEstimation()));

            int minEstimatedSteps = 1_000;
            Pair<MassimTeam4Agent, GetBlockToGoalZoneTask> agentAndTaskPairMinTask1 = null;
            Pair<MassimTeam4Agent, GetBlockToGoalZoneTask> agentAndTaskPairMinTask2 = null;
            for (Pair<MassimTeam4Agent, GetBlockToGoalZoneTask> task1 : block1Subtasks) {
                for (Pair<MassimTeam4Agent, GetBlockToGoalZoneTask> task2 : block1Subtasks) {
                    if (task1 == null || task1.getFirst() == null || task2 == null || task1.getFirst().equals(task2.getFirst())) {
                        continue;
                    }
                    if (task1.getSecond().getStepEstimation() + task2.getSecond().getStepEstimation() < minEstimatedSteps) {
                        agentAndTaskPairMinTask1 = task1;
                        agentAndTaskPairMinTask2 = task2;
                        minEstimatedSteps = task1.getSecond().getStepEstimation() + task2.getSecond().getStepEstimation();
                    }
                }
            }

            if (agentAndTaskPairMinTask1 == null || agentAndTaskPairMinTask2 == null) {
                continue;
            }

            List<AgentTask> agentTasks1;
            List<AgentTask> agentTasks2;
            if (adjacentRequirement.getBlockType() == blockType2) {
                Pair<MassimTeam4Agent, GetBlockToGoalZoneTask> swap = agentAndTaskPairMinTask1;
                agentAndTaskPairMinTask1 = agentAndTaskPairMinTask2;
                agentAndTaskPairMinTask2 = swap;

            }

            agentTasks1 = new ArrayList<>(List.of(
                    agentAndTaskPairMinTask1.getSecond(),
                    new RotateSubtask(adjacentRequirement.getRelPosition()),
                    new LeadCombineSubtask(agentAndTaskPairMinTask2.getFirst(), adjacentRequirement, secondRequirement),
                    new ConnectSubtask(agentAndTaskPairMinTask2.getFirst(), adjacentRequirement),
                    new SubmitSubtask(twoBlockTask.getName())
            ));

            agentTasks2 = new ArrayList<>(List.of(
                    agentAndTaskPairMinTask2.getSecond(),
                    new SupportCombineSubtask(agentAndTaskPairMinTask1.getFirst(), adjacentRequirement, secondRequirement),
                    new ConnectSubtask(agentAndTaskPairMinTask1.getFirst(), secondRequirement),
                    new DetachSubtask(secondRequirement.getBlockType())
            ));

            twoBlockTask.setAgentTasks(new HashMap<>(Map.of(
                    agentAndTaskPairMinTask1.getFirst(), agentTasks1,
                    agentAndTaskPairMinTask2.getFirst(), agentTasks2)));

            twoBlockTasks.add(twoBlockTask);
            if (System.currentTimeMillis() - startTime > 1_000) {
                LOG.info("TwoBlockTaskRule:isFeasible stopped after {} milli seconds", System.currentTimeMillis() - startTime);
            }
        }
        if (twoBlockTasks.isEmpty()) {
            return false;
        }

        List<TwoBlockTask> descTwoBlockTaskList = twoBlockTasks.stream().sorted(Comparator.comparing(TwoBlockTask::getTaskValue).reversed()).toList();
        twoBlockTasksByMassimMap.put(massimMap, descTwoBlockTaskList.get(0));

        long endTime = System.currentTimeMillis();
        LOG.info("TwoBlockTaskRule:isFeasible took {} milli seconds", endTime - startTime);

        return true;
    }

    @Override
    public MassimTask apply(MassimMap massimMap, Map<MassimTeam4Agent, MassimTask> tasksByAgentt) {
        if (!twoBlockTasksByMassimMap.isEmpty()) {
            return twoBlockTasksByMassimMap.get(massimMap);
        } else {
            return null;
        }
    }

    @Override
    public boolean isFeasible(MassimTeam4Agent agent) {
        return false;
    }

    @Override
    public MassimTask apply(MassimTeam4Agent agent) {
        return null;
    }
}
