package massim.javaagents.massimworld.game.task.gametask;

import massim.javaagents.massimworld.actions.MoveAction;
import massim.javaagents.massimworld.agent.MassimTeam4Agent;
import massim.javaagents.massimworld.game.task.gametask.subtask.*;
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
        if (movesToBlock == null) {
            return -1;
        }
        costs += movesToBlock.size();
//        oneBlockTask.setGoToDispenserSubtask(new GoToDispenserSubtask(movesToBlock));
        Coordinates target = computeTargetCoordinates(agent.getMap().getAgentPositionByAgent(agent), movesToBlock);
        List<MoveAction> movesToGoalZone = RoutePlanner.planRouteFromStartToTarget(agent.getMap(), target, agent.getMap().getGoalZoneCoordinates());
        costs += movesToGoalZone.size();
//        oneBlockTask.setGetToGoalZoneTask(new GetToGoalZoneSubtask(movesToGoalZone));
        oneBlockTask.setSubtasks(List.of(
                new GoToDispenserSubtask(movesToBlock),
                new RequestBlockFromDispenserSubtask(oneBlockTask.getBlockType()),
                new AttachSubtask(),
                new GetToGoalZoneSubtask(movesToGoalZone),
                new OrientateSubTask(oneBlockTask.getRelativeBlockPosition()),
                new SubmitSubtask()));

        return costs;
    }

    static Coordinates computeTargetCoordinates(Coordinates start, List<MoveAction> moves) {
        Coordinates current = start;
        for (MoveAction move: moves){
            current = move.applyToCoordinates(current);
        }
        return current;
    }

    private static List<MoveAction> acquireBlock(String blockType, MassimTeam4Agent agent) {
        if (agent.hasAttachedBlock() && agent.getAttachedBlock().getBlockType().equals(blockType)) {
            return List.of();
        } else {
            Set<Coordinates> dispenserCoordinates = agent.getMap().getDispenserCoordinates(blockType);
            if (dispenserCoordinates.isEmpty()) {
                return null;
            }
            List<MoveAction> movesToBlock = RoutePlanner.planRouteForAgentToTarget(agent, dispenserCoordinates);
            return movesToBlock;
        }
    }
}
