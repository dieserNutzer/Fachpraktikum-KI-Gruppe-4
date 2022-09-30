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
import massim.javaagents.massimworld.percepts.agent.AgentNamePercept;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Contains route planning methods for different start and target fields on a given {@link MassimMap}.
 */
public class RoutePlanner {

    private static final Logger LOG = LoggerFactory.getLogger(AgentNamePercept.class);

    public static List<MoveAction> planRouteForAgentToTarget(MassimTeam4Agent agent, Set<Coordinates> target) {
        Problem<Coordinates, MoveAction> problem = new GeneralProblem<>(
                agent.getMap().getAgentPositionByAgent(agent),
                MassimFunctions.createActionsFunction(agent.getMap()),
                MassimFunctions.createResultFunction(agent.getMap()),
                target::contains);
        SearchForActions<Coordinates, MoveAction> search =
                new AStarSearch<>(new GraphSearch<>(), MassimFunctions.createManhattanDistanceFunction(target));
        Optional<List<MoveAction>> actions = search.findActions(problem);

        if (!actions.isPresent() || actions.get().isEmpty()) {
            LOG.info("planRouteForAgentToTarget found no path for {}", agent.getName() );
        } else {
            LOG.info("planRouteForAgentToTarget found  moves: {} for agent {}", actions.get().size(), agent.getName());
        }

        return actions.orElse(Collections.emptyList());
    }

    public static boolean existsPathForAgentToTarget(MassimMap massimMap, Coordinates start, Set<Coordinates> target) {
        List<MoveAction> moves = planRouteFromStartToTarget(massimMap, start, target);
        if (moves == null || moves.isEmpty()) {
            LOG.info("existsPAthFromStartToTarget found no path");
            return false;
        } else {
            return true;
        }
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

        if (!actions.isPresent() || actions.get().isEmpty()) {
            LOG.info("planRouteFromStartToTarget found no path");
        } else {
            LOG.info("planRouteFromStartToTarget found  moves: {}", actions.get().size());
        }

        return actions.orElse(null);
    }

    public static boolean existsPathFromStartToTarget(MassimMap massimMap, Coordinates start, Set<Coordinates> target) {
        List<MoveAction> moves = planRouteFromStartToTarget(massimMap, start, target);
        if (moves == null || moves.isEmpty()) {
            LOG.info("existsPAthFromStartToTarget found no path");
            return false;
        } else {
            return true;
        }
    }
}
