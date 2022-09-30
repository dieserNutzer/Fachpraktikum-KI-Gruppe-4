package massim.javaagents.massimworld.game.task.gametask;

import massim.javaagents.massimworld.actions.MoveAction;
import massim.javaagents.massimworld.agent.MassimTeam4Agent;
import massim.javaagents.massimworld.game.role.RoleType;
import massim.javaagents.massimworld.game.task.agenttask.AgentTask;
import massim.javaagents.massimworld.game.task.agenttask.MoveToDispenserTask;
import massim.javaagents.massimworld.game.task.agenttask.MoveToGoalZoneTask;
import massim.javaagents.massimworld.game.task.agenttask.MoveToRoleZoneTask;
import massim.javaagents.massimworld.game.task.agenttask.composite.AcquireRoleTask;
import massim.javaagents.massimworld.game.task.agenttask.composite.GetBlockToGoalZoneTask;
import massim.javaagents.massimworld.game.task.agenttask.subtask.*;
import massim.javaagents.massimworld.map.Coordinates;
import massim.javaagents.massimworld.map.route.RoutePlanner;
import massim.javaagents.massimworld.map.things.BlockType;
import massim.javaagents.massimworld.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Finds possible tasks for an agent and, if succesful, returns a task with
 * a proposal to fullfill it. This may contain several subtasks, some of them might be
 * planned by the agent at execution time.
 */
public class GameTaskCostEvaluator {

    private static final Logger LOG = LoggerFactory.getLogger(GameTaskCostEvaluator.class);

    public static int evaluateGetBlockToGoalZone(BlockType blockType, MassimTeam4Agent agent) {
        List<MoveAction> movesToBlock = moveToDispenser(blockType, agent);
        List<MoveAction> movesToGoalZone = RoutePlanner.planRouteForAgentToTarget(agent, agent.getMap().getGoalZoneCoordinates());
        movesToBlock.addAll(movesToGoalZone);
        return movesToBlock.size();
    }

    public static OneBlockTask getFeasibleOneBlockTask(OneBlockTask oneBlockTask, MassimTeam4Agent agent) {
        LOG.info("getFeasibleOneBlockTask for agent {} of block type ", agent.getName(), oneBlockTask.getBlockType());
        BlockType blockType = oneBlockTask.getBlockType();

        List<AgentTask> agentTasks = new ArrayList<>();
        Coordinates coordinatesAfterAcquiringBlock = agent.getCurrentCoordinates();

        if (!agent.hasAttachedBlock(oneBlockTask.getBlockType())) {
            if (agent.hasAttachedBlock()) {
                LOG.info("agent {} has attached block of wrong block type", agent.getName());
                agentTasks.add( new DetachSubtask(blockType));
            }

            if (agent.isAdjacentToDispenser(blockType)) {
                LOG.info("agent {} is adjacent to dispenser of block type {} ", agent.getName(), blockType);
            } else {
                List<MoveAction> movesToBlock = moveToDispenser(blockType, agent);
                if (movesToBlock == null) {
                    LOG.info("agent {} has no path to dispenser of block type {} ", agent.getName(), blockType);
                    return null;
                }
                LOG.info("agent {} has path to dispenser of block type {} of length {}", agent.getName(), blockType, movesToBlock.size());
                agentTasks.add(new MoveToDispenserTask(blockType, movesToBlock));
                coordinatesAfterAcquiringBlock = computeTargetCoordinates(agent.getMap().getAgentPositionByAgent(agent), movesToBlock);
            }

            LOG.info("agent {} add request and attach subtask", agent.getName());
            agentTasks.addAll(List.of(
                    new RequestSubtask(blockType),
                    new AttachSubtask(blockType))
            );
        } else {
            LOG.info("agent {} has attached block of block type {} ", agent.getName(), blockType);
        }

        if (agent.isInGoalZone() && agent.hasAttachedBlock(blockType)) {
            LOG.info("agent {} is in goal zone", agent.getName());
        } else {
            List<MoveAction> movesToGoalZone = RoutePlanner.planRouteFromStartToTarget(agent.getMap(), coordinatesAfterAcquiringBlock, agent.getMap().getGoalZoneCoordinates());
            if (movesToGoalZone == null) {
                LOG.info("agent {} has no path to goal zone", agent.getName());
                return null;
            }
            LOG.info("agent {} has path to goal zone of length {}", agent.getName(), movesToGoalZone.size());
            agentTasks.add(new MoveToGoalZoneTask(movesToGoalZone));
        }

        agentTasks.addAll(List.of(
                new RotateSubtask(oneBlockTask.getRelativeBlockPosition()),
                new SubmitSubtask(oneBlockTask.getName()))
        );

        LOG.info("getFeasibleOneBlockTask for agent {} returned task of block type {} and subtasks {} ", agent.getName(), blockType, agentTasks.size());
        return new OneBlockTask(
                oneBlockTask.getDeadline(),
                oneBlockTask.getReward(),
                oneBlockTask.getRequirements(),
                agentTasks
            );
    }

    public static Pair<MassimTeam4Agent, GetBlockToGoalZoneTask> getFeasibleGetBlockToGoalZoneTask(BlockType blockType, MassimTeam4Agent agent) {
        LOG.info("getFeasibleOneBlockTask for agent {} of block type ", agent.getName(), blockType);

        List<AgentTask> agentTasks = new ArrayList<>();
        Coordinates coordinatesAfterAcquiringBlock = agent.getCurrentCoordinates();

        if (!agent.hasAttachedBlock(blockType)) {
            if (agent.hasAttachedBlock()) {
                LOG.info("agent {} has attached block of wrong block type", agent.getName());
                agentTasks.add( new DetachSubtask(agent.getFirstAttachedBlock().getSecond().getBlockType()));
            }

            if (agent.isAdjacentToDispenser(blockType)) {
                LOG.info("agent {} is adjacent to dispenser of block type {} ", agent.getName(), blockType);
            } else {
                List<MoveAction> movesToBlock = moveToDispenser(blockType, agent);
                if (movesToBlock == null) {
                    LOG.info("agent {} has no path to dispenser of block type {} ", agent.getName(), blockType);
                    return null;
                }
                LOG.info("agent {} has path to dispenser of block type {} of length {}", agent.getName(), blockType, movesToBlock.size());
                agentTasks.add(new MoveToDispenserTask(blockType, movesToBlock));
                coordinatesAfterAcquiringBlock = computeTargetCoordinates(agent.getMap().getAgentPositionByAgent(agent), movesToBlock);
            }

            LOG.info("agent {} add request and attach subtask", agent.getName());
            agentTasks.addAll(List.of(
                    new RequestSubtask(blockType),
                    new AttachSubtask(blockType))
            );
        } else {
            LOG.info("agent {} has attached block of block type {} ", agent.getName(), blockType);
        }

        if (agent.isInGoalZone() && agent.hasAttachedBlock(blockType)) {
            LOG.info("agent {} is in goal zone", agent.getName());
        } else {
            List<MoveAction> movesToGoalZone = RoutePlanner.planRouteFromStartToTarget(agent.getMap(), coordinatesAfterAcquiringBlock, agent.getMap().getGoalZoneCoordinates());
            if (movesToGoalZone == null) {
                LOG.info("agent {} has no path to goal zone", agent.getName());
                return null;
            }
            LOG.info("agent {} has path to goal zone of length {}", agent.getName(), movesToGoalZone.size());
            agentTasks.add(new MoveToGoalZoneTask(movesToGoalZone));
        }

        LOG.info("getFeasibleOneBlockTask for agent {} returned task of block type {} and subtasks {} ", agent.getName(), blockType, agentTasks.size());
        return Pair.of(agent, new GetBlockToGoalZoneTask(
                "getBlockToGoalZone",
                blockType,
                agentTasks
        ));
    }



    public static AcquireRoleTask planAcquireRoleTask(RoleType roleType, MassimTeam4Agent agent) {
        List<AgentTask> agentTasks = new ArrayList<>();
        if (agent.getMap().getMassimCell(agent.getCurrentCoordinates()).isRoleZone()) {
            LOG.info("agent {} has is in role zone {} ", agent.getName());
        } else {
            List<MoveAction> movesToRoleZone = RoutePlanner.planRouteFromStartToTarget(agent.getMap(), agent.getCurrentCoordinates(), agent.getMap().getRoleZoneCoordinates());
            if (movesToRoleZone == null || movesToRoleZone.isEmpty()) {
                LOG.info("agent {} has no path to role zone {} ", agent.getName());
                return null;
            }
            agentTasks.add(new MoveToRoleZoneTask(movesToRoleZone));
        }
        agentTasks.add(new AdoptSubtask(roleType));

        return new AcquireRoleTask(roleType, agentTasks);
    }



    private static List<MoveAction> moveToDispenser(BlockType blockType, MassimTeam4Agent agent) {
            Set<Coordinates> dispenserCoordinates;
            try {
                dispenserCoordinates = agent.getMap().getDispenserCoordinates(blockType);
            } catch (NullPointerException e) {
                LOG.error("agent.getMap().getDispenserCoordinates got null pointer exception");
                dispenserCoordinates = Set.of();
            }
            if (dispenserCoordinates.isEmpty()) {
                return null;
            }
            List<MoveAction> movesToBlock = RoutePlanner.planRouteForAgentToTarget(agent, dispenserCoordinates);
            return movesToBlock;
    }

    private static Coordinates computeTargetCoordinates(Coordinates start, List<MoveAction> moves) {
        Coordinates current = start;
        for (MoveAction move: moves){
            current = move.applyToCoordinates(current);
        }
        return current;
    }


}
