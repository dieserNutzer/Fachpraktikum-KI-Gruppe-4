package massim.javaagents.aimamassimworld;

import aima.core.search.framework.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.Collections;

/**
 * Enthaelt statische Funktionen zu Bewegungsaktionen eines Agenten auf einem Grid.
 * Es kann eine Liste der durchfuehrbaren Bewegungsaktionen abgefragt werden,
 * sowie das Ergebnis einer Bewegungsfunktion. Ausserdem kann die kuerzeste Entfernung
 * (gemaess Manhattan-Metrik) zu einem Ziel erfragt werden.
 * Klasse und Methoden sind angelehnt an das Wumpusworld-Beispiel des AIMA-Pakets.
 *
 * @author Florian Dollinger
 */
public class MassimFunctions {

	/**
	 * diefiniert die Funktionen, die auf einem Feld ausgefuehrt werden koennen und eine Aenderung des Zustands (Agentenposition)
	 * bewirken
	 *
	 *
	 * @author Florian Dollinger
	 */
	public static Function<AgentPosition, List<MassimAction>> createActionsFunction(MassimGrid field) {
		return state -> {
			List<MassimAction> actions = new ArrayList<>();
			AgentPosition pos = field.moveNorth(state);
			if (!pos.equals(state))
				actions.add(MassimAction.NORTH);
			
			pos = field.moveWest(state);
			if (!pos.equals(state))
				actions.add(MassimAction.WEST);
			
			pos = field.moveEast(state);
			if (!pos.equals(state))
				actions.add(MassimAction.EAST);
			
			pos = field.moveSouth(state);
			if (!pos.equals(state))
				actions.add(MassimAction.SOUTH);
			
			// Reihenfolge der Aktionen zufaellig anordnen, damit nicht eine Laufrichtungrichtung bevorzugt wird. 
			Collections.shuffle(actions);
			
			return actions;
		};
	}

	/**
	 * Weist einer Agentenpositon und einer Aktion eine darauf resultierende Agentenposition zu.
	 *
	 *
	 * @author Florian Dollinger
	 */
	public static BiFunction<AgentPosition, MassimAction, AgentPosition> createResultFunction(MassimGrid field) {
		return (state, action) -> {
			AgentPosition result = state;
			switch (action) {
				case NORTH:
					result = field.moveNorth(state);
					break;
				case WEST:
					result = field.moveWest(state);
					break;
				case EAST:
					result = field.moveEast(state);
					break;
				case SOUTH:
					result = field.moveSouth(state);
					break;
				default:
					result = state;
					break;
			}
			return result;
		};
	}

	/**
	 * Gibt die kleinste Entfernung von der aktuellen Agentenposition zu einer Zielposition zurueck
	 *
	 *
	 * @author Florian Dollinger
	 */
	public static ToDoubleFunction<Node<AgentPosition, MassimAction>> createManhattanDistanceFunction
			(Set<AgentPosition> goals) {
		return node -> {
			AgentPosition curr = node.getState();
			return goals.stream().
					mapToInt(goal -> Math.abs(goal.getX() - curr.getX()) + Math.abs(goal.getY() - curr.getY())).min().
					orElse(Integer.MAX_VALUE);
		};
	}
}