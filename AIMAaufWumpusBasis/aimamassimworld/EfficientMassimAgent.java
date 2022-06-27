package massim.javaagents.aimamassimworld;

import aima.core.search.framework.SearchForActions;
import aima.core.search.framework.problem.GeneralProblem;
import aima.core.search.framework.problem.Problem;
import aima.core.search.framework.qsearch.GraphSearch;
import aima.core.search.informed.AStarSearch;
import aima.core.util.SetOps;

import java.util.*;

/**
 * Artificial Intelligence A Modern Approach (3rd Edition): page 270.<br>
 * <br>
 *
 * <pre>
 * <code>
 * function HYBRID-WUMPUS-AGENT(percept) returns an action
 *   inputs: percept, a list, [stench, breeze, glitter, bump, scream]
 *   persistent: KB, a knowledge base, initially the temporal "wumpus physics"
 *               t, a counter, initially 0, indicating time
 *               plan, an action sequence, initially empty
 *
 *   TELL(KB, MAKE-PERCEPT-SENTENCE(percept, t))
 *   TELL the KB the temporal "physics" sentences for time t
 *   safe <- {[x, y] : ASK(KB, OK<sup>t</sup><sub>x,y</sub>) = true}
 *   if ASK(KB, Glitter<sup>t</sup>) = true then
 *      plan <- [Grab] + PLAN-ROUTE(current, {[1,1]}, safe) + [Climb]
 *   if plan is empty then
 *      unvisited <- {[x, y] : ASK(KB, L<sup>t'</sup><sub>x,y</sub>) = false for all t' &le; t}
 *      plan <- PLAN-ROUTE(current, unvisited &cap; safe, safe)
 *   if plan is empty and ASK(KB, HaveArrow<sup>t</sup>) = true then
 *      possible_wumpus <- {[x, y] : ASK(KB, ~W<sub>x,y</sub>) = false}
 *      plan <- PLAN-SHOT(current, possible_wumpus, safe)
 *   if plan is empty then //no choice but to take a risk
 *      not_unsafe <- {[x, y] : ASK(KB, ~OK<sup>t</sup><sub>x,y</sub>) = false}
 *      plan <- PLAN-ROUTE(current, unvisited &cap; not_unsafe, safe)
 *   if plan is empty then
 *      plan <- PLAN-ROUTE(current, {[1,1]}, safe) + [Climb]
 *   action <- POP(plan)
 *   TELL(KB, MAKE-ACTION-SENTENCE(action, t))
 *   t <- t+1
 *   return action
 *
 * --------------------------------------------------------------------------------
 *
 * function PLAN-ROUTE(current, goals, allowed) returns an action sequence
 *   inputs: current, the agent's current position
 *           goals, a set of squares; try to plan a route to one of them
 *           allowed, a set of squares that can form part of the route
 *
 *   problem <- ROUTE-PROBLEM(current, goals, allowed)
 *   return A*-GRAPH-SEARCH(problem)
 * </code>
 * </pre>
 *
 * Figure 7.20 A hybrid agent program for the wumpus world. It uses a
 * propositional knowledge base to infer the state of the world, and a
 * combination of problem-solving search and domain-specific code to decide what
 * actions to take.<br><br>
 *
 * This is a tuned version of the {@link HybridWumpusAgent}. It uses a model cave
 * not only for routing but also for position and visited location tracking.
 * The knowledge base grows significant slower than in the original version
 * and response times are much faster.
 *
 */
public class EfficientMassimAgent extends MassimAgent {

    private final MassimGrid massimGrid;
    private final Set<MassimCell> knownCell = new HashSet<>(); // Zellen, die vom Agenten bereits erkundet sind
    private final Set<MassimCell> okToMoveCell = new HashSet<>(); // Zellen, die ein Agent betreten kann (kein Obstacle)
    private final Set<MassimCell> scoutable = new HashSet<>(); // Zellen, die erforscht werden können


    public EfficientMassimAgent()
    {
    	super();
    	getKB().disableNavSentences(); // Optimization: Verbosity of produced sentences is reduced.
    	knownCell.add(currentPosition.getFieldElement());
    	okToMoveCell.add(currentPosition.getFieldElement());
    	massimGrid = new MassimGrid(20, 20); // Ich habe zum Testen so getan, also wäre das Grid immer 20 x 20 groß. Die fest reingehackten WErte sind schlecht, aber ich wollte ursprünglich ja nur das System austesten.
    	scoutable.add(new MassimCell(currentPosition.getX() - 1, currentPosition.getY()));
    	scoutable.add(new MassimCell(currentPosition.getX() + 1, currentPosition.getY()));
    	scoutable.add(new MassimCell(currentPosition.getX(), currentPosition.getY() - 1));
    	scoutable.add(new MassimCell(currentPosition.getX(), currentPosition.getY() + 1));
    }


    /**
     * function HYBRID-WUMPUS-AGENT(percept) returns an action<br>
     *
     * @param percept
     *            a list, [stench, breeze, glitter, bump, scream]
     *
     * @return an action the agent should take.
     */
    // Im Gegensatz zum Wumpus-Beispiel, gibt es nur die nächste Aktion zum ereichen des Ziels zurück, nicht den ganzen Satrz.
    @Override
    public MassimAction planScoutingAction(List<MassimPercept> p) {

        // TELL(KB, MAKE-PERCEPT-SENTENCE(percept, t))
    	for (MassimPercept percept : p)
    	{
    		getKB().makePerceptSentence(percept, currentPosition);
    		if (!percept.isObstacle())
    			okToMoveCell.add(new MassimCell(percept.getXValue() + currentPosition.getX(), percept.getYValue() + currentPosition.getY()));
    		knownCell.add(new MassimCell(percept.getXValue() + currentPosition.getX(), percept.getYValue() + currentPosition.getY()));
    	}
        // TELL the KB the temporal "physics" sentences for time t
        // Optimization: The agent is aware of it's position - the KB can profit from that!
        getKB().tellTemporalPhysicsSentences(t, currentPosition);

        Set<MassimCell> okToMove = null;
        //Set<MassimCell> unknown = null;




        // if plan is empty then
        if (plan.isEmpty()) {
            // unvisited <- {[x, y] : ASK(KB, L<sup>t'</sup><sub>x,y</sub>) = false for all t' &le; t}
            // Optimization: Agent remembers visited locations, no need to ask.
            //unknown = SetOps.difference(field.getAllRooms(), knownField);
            okToMove = SetOps.intersection(okToMoveCell, knownCell);
           
    		

    		
    		
            // plan <- PLAN-ROUTE(current, unvisited &cap; safe, safe)
            plan.addAll(planRouteToFieldElements(scoutable, okToMove));
        }

      
        // action <- POP(plan)
        t = t + 1;
        MassimAction action;
        if (!plan.isEmpty()) {
        	action = plan.remove();
        	// TELL(KB, MAKE-ACTION-SENTENCE(action, t))
        	getKB().makeActionSentence(action, t);

        	updateAgentPosition(action);
        	knownCell.add(currentPosition.getFieldElement());
        }
        else
        	//MassimAction action = new MassimAction(NOOP);
        	return MassimAction.NOOP;
        // return action
        
        return action;
    }

    /**
     * Returns a sequence of actions using A* Search.
     *
     * @param goals
     *            a set of agent positions; try to plan a route to one of them
     * @param allowed
     *            a set of squares that can form part of the route
     *
     * @return the best sequence of actions that the agent have to do to reach a
     *         goal from the current position.
     */
    public List<MassimAction> planRoute(Set<AgentPosition> goals, Set<MassimCell> allowed) {
        massimGrid.setAllowed(allowed);
        Problem<AgentPosition, MassimAction> problem = new GeneralProblem<>(currentPosition,
                MassimFunctions.createActionsFunction(massimGrid),
                MassimFunctions.createResultFunction(massimGrid), goals::contains);
        SearchForActions<AgentPosition, MassimAction> search =
                new AStarSearch<>(new GraphSearch<>(), MassimFunctions.createManhattanDistanceFunction(goals));
        Optional<List<MassimAction>> actions = search.findActions(problem);

        return actions.orElse(Collections.emptyList());
    }

    /**
     * Uses the model cave to update the current agent position.
     */
    private void updateAgentPosition(MassimAction action) {
    	massimGrid.setAllowed(massimGrid.getAllCells());
        switch (action) {
            case NORTH:
                currentPosition = massimGrid.moveNorth(currentPosition);
                break;
            case WEST:
                currentPosition = massimGrid.moveWest(currentPosition);
                break;
            case EAST:
                currentPosition = massimGrid.moveEast(currentPosition);
                break;
            case SOUTH:
                currentPosition = massimGrid.moveSouth(currentPosition);
                break;
            default :
                	break;
        }
        
        int x = currentPosition.getX();
        int y = currentPosition.getY();
        MassimCell toBeRemoved = null;
        
        // Ich glaube hier ist ein Bug. Es sollte nicht die aktuell betretene Zelle aus scoutable
        // entffernt werden, sonder die durch Percepts neu erkundeten.
        for (MassimCell mc : scoutable)
        {
        	if (mc.getX() == x && mc.getY() == y)
        		toBeRemoved = mc;
        }
        
        if (toBeRemoved != null)
        	scoutable.remove(toBeRemoved);
        

         
        
        MassimCell mc = new MassimCell(x, y-1);
        
        if (!scoutable.contains(mc) && !knownCell.contains(mc))
        	scoutable.add(mc);
        
        mc = new MassimCell(x, y+1);
        
        if (!scoutable.contains(mc) && !knownCell.contains(mc))
        	scoutable.add(mc);
        
        mc = new MassimCell(x-1, y);
        
        if (!scoutable.contains(mc) && !knownCell.contains(mc))
        	scoutable.add(mc);
        
        mc = new MassimCell(x+1, y);
        
        if (!scoutable.contains(mc) && !knownCell.contains(mc))
        	scoutable.add(mc);
    }
}