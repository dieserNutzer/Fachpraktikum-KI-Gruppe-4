package massim.javaagents.massimworld.map.route;

import aima.core.search.framework.SearchForActions;
import aima.core.search.framework.problem.GeneralProblem;
import aima.core.search.framework.problem.Problem;
import aima.core.search.framework.qsearch.GraphSearch;
import aima.core.search.informed.AStarSearch;
import massim.javaagents.massimworld.actions.MoveAction;
import massim.javaagents.massimworld.agent.MassimTeam4Agent;
import massim.javaagents.massimworld.map.Coordinates;
import massim.javaagents.massimworld.map.MassimMap;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class RoutePlanner {

    public static List<MoveAction> planRouteForAgentToTarget(MassimTeam4Agent agent, Set<Coordinates> target) {
        Problem<Coordinates, MoveAction> problem = new GeneralProblem<>(
                agent.getMap().getAgentPositionByAgent(agent),
                MassimFunctions.createActionsFunction(agent.getMap()),
                MassimFunctions.createResultFunction(agent.getMap()),
                target::contains);
        SearchForActions<Coordinates, MoveAction> search =
                new AStarSearch<>(new GraphSearch<>(), MassimFunctions.createManhattanDistanceFunction(target));
        Optional<List<MoveAction>> actions = search.findActions(problem);

        return actions.orElse(Collections.emptyList());
    }

    public static List<MoveAction> planRouteFromStartToTarget(MassimMap massimMap, Coordinates start, Set<Coordinates> target) {
        Problem<Coordinates, MoveAction> problem = new GeneralProblem<>(
                start,
                MassimFunctions.createActionsFunction(massimMap),
                MassimFunctions.createResultFunction(massimMap),
                target::contains);
        SearchForActions<Coordinates, MoveAction> search =
                new AStarSearch<>(new GraphSearch<>(), MassimFunctions.createManhattanDistanceFunction(target));
        Optional<List<MoveAction>> actions = search.findActions(problem);

        return actions.orElse(Collections.emptyList());
    }
}
