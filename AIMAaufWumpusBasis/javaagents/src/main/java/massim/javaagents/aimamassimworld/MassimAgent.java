package massim.javaagents.aimamassimworld;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;


import aima.core.agent.impl.SimpleAgent;
import aima.core.search.framework.SearchForActions;
import aima.core.search.framework.problem.GeneralProblem;
import aima.core.search.framework.problem.Problem;
import aima.core.search.framework.qsearch.GraphSearch;
import aima.core.search.informed.AStarSearch;
import aima.core.util.SetOps;

public class MassimAgent extends SimpleAgent<MassimPercept, MassimAction> {
	
	private MassimKnowledgeBase kb;
	protected AgentPosition start;
	protected AgentPosition currentPosition;
	protected int t = 0;
	protected Queue<MassimAction> plan = new LinkedList<>(); // FIFOQueue
	
	public MassimAgent()
	{
		start = new AgentPosition(64,92); // setze Agenten in die "Mitte" seines Grids
		currentPosition = new AgentPosition(64, 92);
		//TODO
		// Alle Werte in diesem Block sind für Turnier 3 auf feste groesse gesetzt.
		// Höhe und Breite jeweils doppelt so groß wie das original gewählt, Agenten
		// in die Mitte gesetzt. So sollte er nicht über den Rand des MassimGrid hinaus können.
		kb = new MassimKnowledgeBase(128, 184); 
	}
	
	public MassimAction planScoutingAction(List<MassimPercept> p) {
		Set<MassimCell> unknown = null;
		Set<MassimCell> allowed = null;
		kb.tellTemporalPhysicsSentences(t);
		
		if (plan.isEmpty()) {
		//MassimGrid grid = new MassimGrid(kb.getFieldXDimension(), kb.getFieldYDimension());
		
		currentPosition = kb.askCurrentPosition(t);
		for (MassimPercept percept : p)
			kb.makePerceptSentence(percept, currentPosition);
		allowed = kb.askAllowedFields(t);
		unknown = kb.askUnknownFields(t);
		
		plan.addAll(planRouteToFieldElements(SetOps.intersection(unknown, allowed), allowed));

		}
		
		MassimAction action = plan.remove();
		/// TELL(KB, MAKE-ACTION-SENTENCE(action, t))
		kb.makeActionSentence(action, t);
		/// t <- t+1
		t = t + 1;
		/// return action
		//return Optional.of(action);
		return action;
	}
	
	public MassimKnowledgeBase getKB()
	{
		return kb;
	}
	
	public List<MassimAction> planRouteToFieldElements(Set<MassimCell> goals, Set<MassimCell> allowed) {
		final Set<AgentPosition> goalPositions = new LinkedHashSet<>();
		for (MassimCell goalCell : goals) {
			int x = goalCell.getX();
			int y = goalCell.getY();
			goalPositions.add(new AgentPosition(x, y));
		}
		
		return planRoute(goalPositions, allowed);
	}
	
	public List<MassimAction> planRoute(Set<AgentPosition> goals, Set<MassimCell> allowed) {
		MassimGrid grid = new MassimGrid(kb.getFieldXDimension(), kb.getFieldYDimension()).setAllowed(allowed);
		Problem<AgentPosition, MassimAction> problem = new GeneralProblem<>(currentPosition,
				MassimFunctions.createActionsFunction(grid),
				MassimFunctions.createResultFunction(grid), goals::contains);
		SearchForActions<AgentPosition, MassimAction> search =
				new AStarSearch<>(new GraphSearch<>(), MassimFunctions.createManhattanDistanceFunction(goals));
		Optional<List<MassimAction>> actions = search.findActions(problem);

		return actions.orElse(Collections.emptyList());
	}
	

}
