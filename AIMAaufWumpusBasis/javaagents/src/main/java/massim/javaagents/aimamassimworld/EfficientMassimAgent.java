package massim.javaagents.aimamassimworld;

import aima.core.agent.impl.SimpleAgent;
import aima.core.search.framework.SearchForActions;
import aima.core.search.framework.problem.GeneralProblem;
import aima.core.search.framework.problem.Problem;
import aima.core.search.framework.qsearch.GraphSearch;
import aima.core.search.informed.AStarSearch;
import aima.core.util.SetOps;

import java.util.*;

public class EfficientMassimAgent extends SimpleAgent<MassimPercept, MassimAction> {

    private final MassimGrid massimGrid;
	protected AgentPosition start;
	protected AgentPosition currentPosition;
	protected Queue<MassimAction> plan = new LinkedList<>(); // FIFOQueue
	
    private final Set<MassimCell> knownCell = new HashSet<>(); // Zellen, die vom Agenten bereits erkundet sind
    private final Set<MassimCell> walkableCell = new HashSet<>(); // Zellen, die ein Agent betreten kann (kein Obstacle)
    private final Set<MassimCell> scoutable = new HashSet<>(); // Zellen, die erforscht werden können
    private final Set<MassimCell> goalField = new HashSet<>(); // Zellen, die die goalzone darstellen
    private final Set<MassimCell> roleZone = new HashSet<>(); // Zellen, die die rolezone darstellen
    private final Set<MassimDispenserCell> dispenserField = new HashSet<>(); // Zellen, die zu einem Dispenser BENACHBART sind
    private final Set<MassimCell> temporaryBlocked = new HashSet<>(); // Zellen, die nur vorrübergehend blockiert sind, z.B. durch andere Agenten
    
    
   


    public EfficientMassimAgent()
    {
    	massimGrid = new MassimGrid(128, 184);
		start = new AgentPosition(64,92); // setze Agenten in die "Mitte" seines Grids
		currentPosition = new AgentPosition(64, 92);
		//TODO
		// Alle Werte in diesem Block sind für Turnier 3 auf feste groesse gesetzt.
		// Höhe und Breite jeweils doppelt so groß wie das original gewählt, Agenten
		// in die Mitte gesetzt. So sollte er nicht über den Rand des MassimGrid hinaus können.); 

    	knownCell.add(currentPosition.getFieldElement());
    	walkableCell.add(currentPosition.getFieldElement());
    }
    
    
    
    public void tellStuff(List<MassimPercept> percepts)
    {
    	temporaryBlocked.clear();

    	MassimCell tempCell = null;
    	
    	for (MassimPercept p : percepts)
    	{
    		tempCell = null;
    		
    		// Auslesen, der Koordinaten des Dings im Percept
    		// Koordinaten sind die relativen Koordinaten bezogen auf den Agenten und
    		// werden zum absolluten Positionswert hinzu gefügt.
			int x = p.getXValue() + currentPosition.getX();
			int y = p.getYValue() + currentPosition.getY();
			
    		if (p.isObstacle())
    		{
    			// Sollte für die Koordinaten schon markiert sein, dass der Weg hier frei ist
    			// wird das hier korrigiert (z.B. durch Verschieben von Obstacles)
    			for (MassimCell c : walkableCell)
    			{
    				if (c.getX() == x && c.getY() == y)
    				{
    					tempCell = c;
    				}
    			}
    			
    			if (tempCell != null)
    			{
    				walkableCell.remove(tempCell);
    			}
    		}
    		// Wenn kein Hindernis, werden die Koordinaten in die Liste der freien Wege aufgenommen
    		else
    		{
    			for (MassimCell c : walkableCell)
    			{
    				if (c.getX() == x && c.getY() == y)
    				{
    					tempCell = c;
    				}
    			}		
    			
    			if (tempCell == null)
    			walkableCell.add(new MassimCell(x, y));
    		}
    		
    		// Die Nachbarn eines Dispensers, inklusive des Typs des Blocks werden aufgelistet
    		if (p.isDispenser())
    		{
    			MassimDispenserCell mdc = new MassimDispenserCell(x, y, p.getType());
    			for (MassimDispenserCell mc : mdc.getNeighbours())
    				dispenserField.add(mc);
    		}
    		
    		// Liste der vorrübergehnd blockierten Zellen
    		if (p.isTemporaryBlocked())
    		{
    			temporaryBlocked.add(new MassimCell(x, y));
    		}
    		
    		if (p.isRoleZone())
    		{
    			roleZone.add(new MassimCell(x, y));
    		}
    		
    		if (p.isGoalZone())
    		{
    			goalField.add(new MassimCell(x, y));
    		}
    		// goal Zones können sich verschieben.
    		else
    		{
    			goalField.remove(new MassimCell(x, y));
    		}
    		
    		
    		
    		// Werden Informationen über ein Feld mitgeteilt, so ist es 1. von nun an bekannt.
    		// und 2. nicht mehr scoutable
    		
    		knownCell.add(new MassimCell(x, y));
    		
            MassimCell toBeRemoved = null;
            
            for (MassimCell mc : scoutable)
            {
            	if (mc.getX() == x && mc.getY() == y)
            		toBeRemoved = mc;
            }
            
            if (toBeRemoved != null)
            	scoutable.remove(toBeRemoved);
    	} // for (MassimPercept p : percepts)
    	
    	
    	
    	// Die angrenzenden Felder zum Feld des Percepts können nun scoutable sein. 
    	
        MassimCell mc;
        
        // TODO bedenke den Rand, es soll nicht über die Grenzen hinaus gehen...
        for (MassimCell m : knownCell)
        {
        	mc = new MassimCell(m.getX() - 1, m.getY());
        	if (!scoutable.contains(m) && !knownCell.contains(mc))
            	scoutable.add(m);
        	
        	mc = new MassimCell(m.getX() + 1, m.getY());
        	if (!scoutable.contains(m) && !knownCell.contains(mc))
            	scoutable.add(m);
        	
        	mc = new MassimCell(m.getX(), m.getY() - 1);
        	if (!scoutable.contains(m) && !knownCell.contains(mc))
            	scoutable.add(m);
        	
        	mc = new MassimCell(m.getX(), m.getY() + 1);
        	if (!scoutable.contains(m) && !knownCell.contains(mc))
            	scoutable.add(m);
        }
        
        plan.clear();
    }
    
    // Gibt die nächste auszuführende Aktion zurück, um möglichst schnell eine RoleZone zu erreichen.
    public MassimAction planRouteToRoleZone() {
    	return prepareRoutePlanning(roleZone);
    }
    
    // Gibt die nächste auszuführende Aktion zurück, um möglichst schnell eine Zelle zu erreichen, die benachbart zu einem
    // dispenser mit angegebenem Typ ist.
    public MassimAction planRouteToDispenserAction(String type) {
        Set<MassimCell> dispenserCorrectType = new HashSet<>();
        
        
        for (MassimDispenserCell df : dispenserField)
        {
        	if (df.getType().equals(type))
        		dispenserCorrectType.add(new MassimCell(df.getX(), df.getY()));
        }
        return prepareRoutePlanning(dispenserCorrectType);
    }
    
    // Versucht die angrenzenden Felder zu erforschen
    // in dieser Version, mit dem Versuch der Berücksichtigung eines angehängten Blocks mit Koordinaten x, y
    public MassimAction planScoutingAction(int x, int y) {

    	Set<MassimCell> okToMove = null;
    	Set<MassimCell> okToMove2 = new HashSet<>();
    	MassimCell temp;

        if (plan.isEmpty()) {

            okToMove = SetOps.intersection(walkableCell, knownCell);
            okToMove = SetOps.difference(okToMove, temporaryBlocked);
            okToMove.add(new MassimCell(currentPosition.getX(), currentPosition.getY()));
            okToMove.add(new MassimCell(currentPosition.getX() + x, currentPosition.getY() + y));
            
            for (MassimCell mc : okToMove)
            {
            	temp = new MassimCell(mc.getX() + x, mc.getY() + y);
            	if (okToMove.contains(temp) || scoutable.contains(temp))
            		okToMove2.add(mc);
            }
        }
        
        

        plan.addAll(planRouteToFieldElements(scoutable, okToMove2));

        MassimAction action;
        if (!plan.isEmpty()) {
        	action = plan.remove();
        }
        else
        	return MassimAction.NOOP;
        
        return action;
    }
    
    
    public MassimAction planRouteToGoalAction(int x, int y) {



    	Set<MassimCell> okToMove = null;
    	Set<MassimCell> okToMove2 = new HashSet<>();
    	Set<MassimCell> goalField2 = new HashSet<>();
    	MassimCell temp;

        if (plan.isEmpty()) {

            okToMove = SetOps.intersection(walkableCell, knownCell);
            okToMove = SetOps.difference(okToMove, temporaryBlocked);
            okToMove.add(new MassimCell(currentPosition.getX(), currentPosition.getY()));
            okToMove.add(new MassimCell(currentPosition.getX() + x, currentPosition.getY() + y));
            
            for (MassimCell mc : okToMove)
            {
            	temp = new MassimCell(mc.getX() + x, mc.getY() + y);
            	if (okToMove.contains(temp))
            		okToMove2.add(mc);
            }
            
            for (MassimCell mc : goalField)
            {
            	temp = new MassimCell(mc.getX() + x, mc.getY() + y);
            	// Weg zur Zielzone wird gesucht, soll aber kein Hindernis enthalten.
            	if (/* goalField.contains(temp) &&*/ okToMove.contains(new MassimCell(mc.getX(), mc.getY())))
            		goalField2.add(mc);
            }
            

    		
            plan.addAll(planRouteToFieldElements(goalField2, okToMove2));
        }
        
        if (plan.isEmpty()) {
        	plan.addAll(planRouteToFieldElements(goalField2, okToMove));
        }
        


        Set<MassimCell> chance = new HashSet<>();

        MassimAction action;
        AgentPosition pos = null;
        
        if (!plan.isEmpty())
        {
        	action = plan.remove();
        	if (action == MassimAction.NORTH)
        		pos = massimGrid.moveNorth(currentPosition);
        	else if (action == MassimAction.WEST)
        		pos = massimGrid.moveWest(currentPosition);
        	else if (action == MassimAction.SOUTH)
        		pos = massimGrid.moveSouth(currentPosition);
        	else if (action == MassimAction.EAST)
        		pos = massimGrid.moveEast(currentPosition);
        	
        	
        	
            if (!plan.isEmpty() && pos != null)
            {
            	action = plan.remove();
            	if (action == MassimAction.NORTH)
            		pos = massimGrid.moveNorth(pos);
            	else if (action == MassimAction.WEST)
            		pos = massimGrid.moveWest(pos);
            	else if (action == MassimAction.SOUTH)
            		pos = massimGrid.moveSouth(pos);
            	else if (action == MassimAction.EAST)
            		pos = massimGrid.moveEast(pos);
        	
        }
            if (pos != null)
            chance.add(new MassimCell(pos.getX(), pos.getY()));
        }
            
            
        plan.clear();
            
        
        
        
        plan.addAll(planRouteToFieldElements(chance, okToMove2));
        
        
        if (!plan.isEmpty()) {
        	action = plan.remove();

        	//updateAgentPosition(action);
        	//knownCell.add(currentPosition.getFieldElement());
        }
        else
        	//MassimAction action = new MassimAction(NOOP);
        	return MassimAction.NOOP;
        // return action
        
        return action;
    }
    
    // Plant den Weg zu einer goalZone, gibt die nächste auszuführende Aktion zurück.
    public MassimAction planRouteToGoalAction() {
        return prepareRoutePlanning(goalField);
    }
    
    protected MassimAction prepareRoutePlanning(Set<MassimCell> goal)
    {
    	if (plan.isEmpty()) {
    		Set<MassimCell> okToMove = null;
    		
    		okToMove = SetOps.intersection(walkableCell, knownCell);
    		okToMove = SetOps.difference(okToMove, temporaryBlocked);
    	
    		plan.addAll(planRouteToFieldElements(goal, okToMove));
    	}
    	
    	
        MassimAction action;
        if (!plan.isEmpty()) {
        	
        	action = plan.remove();
        }
        else
        	// keinen Weg gefunden
        	return MassimAction.NOOP;
        return action;
    }



    public MassimAction planScoutingAction() {

    	return prepareRoutePlanning(scoutable);
    }
    
    // Aus dem Wumpus Beispiel
	public List<MassimAction> planRouteToFieldElements(Set<MassimCell> goals, Set<MassimCell> allowed) {
		final Set<AgentPosition> goalPositions = new LinkedHashSet<>();
		for (MassimCell goalCell : goals) {
			int x = goalCell.getX();
			int y = goalCell.getY();
			goalPositions.add(new AgentPosition(x, y));
		}
		
		return planRoute(goalPositions, allowed);
	}

	// Aus dem Wumpus Beispiel
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


    
    public void updateAgentPosition(MassimAction action) {
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
    }
}