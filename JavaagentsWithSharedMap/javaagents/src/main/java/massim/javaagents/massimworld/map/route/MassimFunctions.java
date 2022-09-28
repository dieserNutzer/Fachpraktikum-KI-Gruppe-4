package massim.javaagents.massimworld.map.route;

import aima.core.search.framework.Node;
import massim.javaagents.massimworld.actions.MoveAction;
import massim.javaagents.massimworld.map.Coordinates;
import massim.javaagents.massimworld.map.MassimMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

import static massim.javaagents.massimworld.map.Direction.*;


public class MassimFunctions {

	// diefiniert die Funktionen, die auf einem Feld ausgeführt werden können und eine Änderung des Zustands (Agentenposition)
	// bewirken
	public static Function<Coordinates, List<MoveAction>> createActionsFunction(MassimMap massimMap) {
		return state -> {
			List<MoveAction> actions = new ArrayList<>();
			Coordinates pos = moveNorth(state, massimMap);
			if (!pos.equals(state))
				actions.add(new MoveAction(NORTH));
			
			pos = moveWest(state, massimMap);
			if (!pos.equals(state))
				actions.add(new MoveAction(WEST));
			
			pos = moveEast(state, massimMap);
			if (!pos.equals(state))
				actions.add(new MoveAction(EAST));

			pos = moveSouth(state, massimMap);
			if (!pos.equals(state))
				actions.add(new MoveAction(SOUTH));

			return actions;
		};
	}

	// Weist einer Agentenpositon und einer Aktion eine Folge Agentenposition zu.
	public static BiFunction<Coordinates, MoveAction, Coordinates> createResultFunction(MassimMap massimMap) {
		return (position, action) -> {
			Coordinates result = switch (action.getDirection()) {
				case NORTH -> moveNorth(position, massimMap);
				case WEST -> moveWest(position, massimMap);
				case EAST -> moveEast(position, massimMap);
				case SOUTH -> moveSouth(position, massimMap);
			};
			return result;
		};
	}

	// Distanz gemäß Manhatten metrik
	public static ToDoubleFunction<Node<Coordinates, MoveAction>> createManhattanDistanceFunction
			(Set<Coordinates> goals) {
		return node -> {
			Coordinates curr = node.getState();
			return goals.stream().
					mapToInt(goal -> Math.abs(goal.getX() - curr.getX()) + Math.abs(goal.getY() - curr.getY())).min().
					orElse(Integer.MAX_VALUE);
		};
	}


	public static Coordinates moveNorth(Coordinates coordinates, MassimMap massimMap) {
		return massimMap.isAllowed(coordinates.getAdjacent(NORTH)) ? coordinates.getAdjacent(NORTH) : coordinates;
	}

	public static Coordinates moveWest(Coordinates coordinates, MassimMap massimMap) {
		return massimMap.isAllowed(coordinates.getAdjacent(WEST)) ? coordinates.getAdjacent(WEST) : coordinates;
	}

	public static Coordinates moveEast(Coordinates coordinates, MassimMap massimMap) {
		return massimMap.isAllowed(coordinates.getAdjacent(EAST)) ? coordinates.getAdjacent(EAST) : coordinates;
	}

	public static Coordinates moveSouth(Coordinates coordinates, MassimMap massimMap) {
		return massimMap.isAllowed(coordinates.getAdjacent(SOUTH)) ? coordinates.getAdjacent(SOUTH) : coordinates;
	}
}
