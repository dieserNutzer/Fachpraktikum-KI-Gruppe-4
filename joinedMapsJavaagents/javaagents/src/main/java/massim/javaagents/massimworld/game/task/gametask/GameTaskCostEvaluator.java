package massim.javaagents.massimworld.game.task.gametask;

import massim.javaagents.massimworld.actions.MoveAction;
import massim.javaagents.massimworld.agent.MassimTeam4Agent;
import massim.javaagents.massimworld.game.task.gametask.subtask.GetBlockFromDispenserSubtask;
import massim.javaagents.massimworld.game.task.gametask.subtask.GetToGoalZoneSubtask;
import massim.javaagents.massimworld.game.task.gametask.subtask.GoToDispenserSubtask;
import massim.javaagents.massimworld.map.Coordinates;
import massim.javaagents.massimworld.map.route.RoutePlanner;

import java.util.List;
import java.util.Set;

public class GameTaskCostEvaluator {

    public static int evaluateGetBlockToGoalZone(String blockType, MassimTeam4Agent agent) {
        List<MoveAction> movesToBlock = acquireBlock(blockType, agent);
        List<MoveAction> movesToGoalZone = RoutePlanner.planRouteForAgentToTarget(agent, agent.getMap().getGoalZoneCoordinates());
        movesToBlock.addAll(movesToGoalZone);
        return movesToBlock.size();
    }

    public static int planOneBlockTask(OneBlockTask oneBlockTask, MassimTeam4Agent agent) {
        int costs = 0;
        String blockType = oneBlockTask.getBlockType();
        List<MoveAction> movesToBlock = acquireBlock(blockType, agent);
        costs += movesToBlock.size();
        oneBlockTask.setGoToDispenserSubtask(new GoToDispenserSubtask(movesToBlock));
        List<MoveAction> movesToGoalZone = RoutePlanner.planRouteForAgentToTarget(agent, agent.getMap().getGoalZoneCoordinates());
        costs += movesToGoalZone.size();
        oneBlockTask.setGetToGoalZoneTask(new GetToGoalZoneSubtask(movesToGoalZone));
        return costs;
    }

    private static List<MoveAction> acquireBlock(String blockType, MassimTeam4Agent agent) {
        if (agent.hasAttachedBlock() && agent.getAttachedBlock().getBlockType().equals(blockType)) {
            return List.of();
        } else {
            Set<Coordinates> dispenserCoordinates = agent.getMap().getDispenserCoordinates(blockType);
            List<MoveAction> movesToBlock = RoutePlanner.planRouteForAgentToTarget(agent, dispenserCoordinates);
            return movesToBlock;
        }
    }
}
