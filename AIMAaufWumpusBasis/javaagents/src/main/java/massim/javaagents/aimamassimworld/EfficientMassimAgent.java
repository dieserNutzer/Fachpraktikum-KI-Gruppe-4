package massim.javaagents.aimamassimworld;

import aima.core.agent.impl.SimpleAgent;
import aima.core.search.framework.SearchForActions;
import aima.core.search.framework.problem.GeneralProblem;
import aima.core.search.framework.problem.Problem;
import aima.core.search.framework.qsearch.GraphSearch;
import aima.core.search.informed.AStarSearch;
import aima.core.util.SetOps;

import java.util.*;

// Dieser Agent ist hauptsächlich für das Kartenmanagement und die Wegfindung zuständig
// Er liest dafür auch die Percepts vom Team4Agent aus und verwertet die Informationen
// Dabei wandelt er die relativen Koordinaten vom Team4Agent in globale Kartendaten, die hier verwendet werden.
// Informationen werden in Sets eingefügt und gehalten
public class EfficientMassimAgent extends SimpleAgent<MassimPercept, MassimAction> {

    private final MassimGrid massimGrid;
	protected AgentPosition start;
	protected AgentPosition currentPosition;
	protected Queue<MassimAction> plan = new LinkedList<>(); // FIFOQueue, enthält eine Queue von Aktionen, die hintereinander ausgeführt werden sollen, um das letzte angefragte Ziel zu erreichen
	
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

		
		// Beim Anlegen des Agenten wird die Startposition als bekannte und betretbare Zelle markiert (bzw. in die
		// entsprechenden Listen eingefügt)
    	knownCell.add(currentPosition.getFieldElement());
    	walkableCell.add(currentPosition.getFieldElement());
    }
    
    
    // Wird vom Team4Agent aufgerufen, um die von ihm verfassten Percepts auszulesen und zu verwerten.
    public void tellStuff(List<MassimPercept> percepts)
    {
    	// temporary blocked Zellen ändern sich häufig, daher lösche die bisher bekannten Informationen
    	temporaryBlocked.clear();

    	MassimCell tempCell = null;
    	
    	for (MassimPercept p : percepts)
    	{
    		tempCell = null;
    		
    		// Auslesen, der Koordinaten des Dings, das im Percept beschrieben wird
    		// Koordinaten sind die relativen Koordinaten bezogen auf den Agenten und
    		// werden zum absoluten Positionswert hinzu gefügt.
			int x = p.getXValue() + currentPosition.getX();
			int y = p.getYValue() + currentPosition.getY();
			
			// Ist es ein Obstacle ?
    		if (p.isObstacle())
    		{
    			// Sollte für die Koordinaten schon markiert sein, dass der Weg hier frei ist
    			// wird das hier korrigiert (z.B. nach Verschieben von Obstacles)
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
    		
    		// Ist es ein Dispenser ?
    		// Dann werden die Nachbarfelder des Dispensers, inklusive des Typs des Blocks in die entsprechende Liste aufgenommen
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
    		
    		// RoleZone-Felder werden hinzugefügt
    		if (p.isRoleZone())
    		{
    			roleZone.add(new MassimCell(x, y));
    		}
    		
    		// Ist es goalZone-Feld ?
    		// Dann füge es in entsprechende Liste
    		if (p.isGoalZone())
    		{
    			goalField.add(new MassimCell(x, y));
    		}
    		// goal Zones können sich verschieben.
    		// daher lösche es ggf. wieder aus der Auflistung
    		else
    		{
    			goalField.remove(new MassimCell(x, y));
    		}
    		
    		
    		
    		// Werden Informationen über ein Feld mitgeteilt, so ist es 1. von nun an bekannt.
    		// und 2. nicht mehr scoutable
    		
    		knownCell.add(new MassimCell(x, y));
    		
            MassimCell toBeRemoved = null;
            
            // Falls vorhanden, entferne Feld aus den scoutable-Set
            
            for (MassimCell mc : scoutable)
            {
            	if (mc.getX() == x && mc.getY() == y)
            		toBeRemoved = mc;
            }
            
            if (toBeRemoved != null)
            	scoutable.remove(toBeRemoved);
    	} // for (MassimPercept p : percepts)
    	
    	
    	
    	// Die angrenzenden Felder zum Feld des Percepts können nun scoutable sein. 
    	// Füge ein Feld als scoutable hinzu, falls es nicht schon bekannt ist.
    	
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
        
        // lösche bisher angelegten Plan, da sich Ziel oder auch belegte Felder geändert haben können.
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
        
        // Gehe alle dispenser-Felder durch und füge Felder, mit gesuchtem Typ Dispenser zu einem Set hinzu.
        // Alle Felder des Sets gelten als mögliche Ziele für den Agenten
        for (MassimDispenserCell df : dispenserField)
        {
        	if (df.getType().equals(type))
        		dispenserCorrectType.add(new MassimCell(df.getX(), df.getY()));
        }
        return prepareRoutePlanning(dispenserCorrectType);
    }
    
    // Versucht die unbekannten Felder zu erforschen
    // in dieser Version, mit dem Versuch der Berücksichtigung eines angehängten Blocks mit Koordinaten x, y
    // (damit Agent nicht mit dem angehängten Block irgendwo festhängt) 
    public MassimAction planScoutingAction(int x, int y) {

    	Set<MassimCell> okToMove = null;
    	Set<MassimCell> okToMove2 = new HashSet<>();
    	MassimCell temp;

        if (plan.isEmpty()) {

        	// Felder, die walkable und bekannt sind, sind erlaubte Felder innerhalb der möglichen Route zum Ziel
            okToMove = SetOps.intersection(walkableCell, knownCell);
            // Felder, die temporär blockiert sind, sollen kein Teil der Route sein.
            okToMove = SetOps.difference(okToMove, temporaryBlocked);
            // Füge aktuelle Agentenposition zu erlaubten Feldern hinzu, sonst steht der Agent sich selbst im Weg
            // (Feld ist als temporär blockiert gekennzeichnet, da der eigene Agent auch entity ist)
            okToMove.add(new MassimCell(currentPosition.getX(), currentPosition.getY()));
            // Füge das Feld mit dem attachten Block zu den erlaubten Feldern hinzu. Sonst geht der Agent da nicht drüber.
            okToMove.add(new MassimCell(currentPosition.getX() + x, currentPosition.getY() + y));
            
            
            // In eine zweite Liste werden nur Felder als erlaubte Felder übernommen, die auch neben sich (in Richtung des angehängten
            // Blocks) ein begehbares oder scoutable Feld haben, damit genug Platz für Agent und Block ist.
            for (MassimCell mc : okToMove)
            {
            	temp = new MassimCell(mc.getX() + x, mc.getY() + y);
            	if (okToMove.contains(temp) || scoutable.contains(temp))
            		okToMove2.add(mc);
            }
        }
        
        

        // Füge dem Plan alle Aktionen hinzu, die benötigt werden, um ein scoutable Feld zu erreichen, wobei dabei nur
        // okToMove2-Felder betreten werden dürfen.
        plan.addAll(planRouteToFieldElements(scoutable, okToMove2));

        // Gebe nur die erste Aktion zurück. Im nächsten Step wird nach Aktualisierung der Karte neu berechnet.
        // TODO je nach Rolle sind durchaus auch mehrere move-Aktionen pro step möglich
        MassimAction action;
        if (!plan.isEmpty()) {
        	action = plan.remove();
        }
        else
        	// Wenn kein Weg gefunden wurde (plan.isEmpty()==true), dann gebe NOOP zurück.
        	return MassimAction.NOOP;
        
        return action;
    }
    
    
    public MassimAction planRouteToGoalAction(int x, int y) {



    	Set<MassimCell> okToMove = null;
    	Set<MassimCell> okToMove2 = new HashSet<>();
    	Set<MassimCell> goalField2 = new HashSet<>();
    	MassimCell temp;

        if (plan.isEmpty()) {
        	
        	
        	
            // Felder, die walkable und bekannt sind, sind erlaubte Felder innerhalb der möglichen Route zum Ziel
            okToMove = SetOps.intersection(walkableCell, knownCell);
            // Felder, die temporär blockiert sind, sollen kein Teil der Route sein.
            okToMove = SetOps.difference(okToMove, temporaryBlocked);
            // Füge aktuelle Agentenposition zu erlaubten Feldern hinzu, sonst steht der Agent sich selbst im Weg
            // (Feld ist als temporär blockiert gekennzeichnet, da der eigene Agent auch entity ist)
            okToMove.add(new MassimCell(currentPosition.getX(), currentPosition.getY()));
            // Füge das Feld mit dem attachten Block zu den erlaubten Feldern hinzu. Sonst geht der Agent da nicht drüber.
            okToMove.add(new MassimCell(currentPosition.getX() + x, currentPosition.getY() + y));
            
            
            // In eine zweite Liste werden nur Felder als erlaubte Felder übernommen, die auch neben sich (in Richtung des angehängten
            // Blocks) ein begehbares Feld haben, damit genug Platz für Agent und Block ist.
            for (MassimCell mc : okToMove)
            {
            	temp = new MassimCell(mc.getX() + x, mc.getY() + y);
            	if (okToMove.contains(temp))
            		okToMove2.add(mc);
            }
            
            // Ein Zielzonen-Feld sollte nur dann betreten werden, wenn in Richtung des angehängten Blocks auch ein freies Feld ist.
            for (MassimCell mc : goalField)
            {
            	temp = new MassimCell(mc.getX() + x, mc.getY() + y);
            	// Weg zur Zielzone wird gesucht, soll aber kein Hindernis enthalten.
            	if (/* goalField.contains(temp) &&*/ okToMove.contains(new MassimCell(mc.getX(), mc.getY())))
            		goalField2.add(mc);
            }
            

            // Füge dem Plan alle Aktionen hinzu, die benötigt werden, um ein goalField2 Feld zu erreichen, wobei dabei nur
            // okToMove2-Felder betreten werden dürfen.
            plan.addAll(planRouteToFieldElements(goalField2, okToMove2));
        }
        
        // Oje, hier habe ich zum Schluss ein wenig rumprobiert. Falls kein Weg gefu nden wurde, versuche es nochmal
        // indem die betretbaren Felder weniger limitiert sind...
        if (plan.isEmpty()) {
        	plan.addAll(planRouteToFieldElements(goalField2, okToMove));
        }
        

        // Es folgt ein schlimmes Experiment, dass so wahrscheinlich nicht in den Code sollte
        // (war für den Fall, dass auf herkömmlichen WEg kein Ziel gefunden werden kann.
        // Soll wahrschienlich wieder //TODO raus

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
        }
        else
        	return MassimAction.NOOP;
        
        return action;
    }
    
    // Plant den Weg zu einer goalZone, gibt die nächste auszuführende Aktion zurück.
    public MassimAction planRouteToGoalAction() {
        return prepareRoutePlanning(goalField);
    }
    
    // Sucht eine Folge von Aktionen, mit denen ein Ziel erreicht werden kann.
    // Begehbare Felder sind dabei alle bekannten, nicht blockeirten Felder
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
    // "Übersetzt" das Set aus MassimCells, also die Felder, die bei der Wegfindung als Ziel gelten sollen,
    // in ein Set aus Agentpositions mit gleichen Koordinaten, da die weiteren Algorithmen der WEgfindung dies so benötigen
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
	// Schließelich die eigentliche Formulierung des Problems für die Suche
	// und Lösung mittels AStarSearch
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


    // Aktualisierung der AgentPosition,  w enn durch TEam4Agent ein erfolgreiches move mitgeteilt wird.
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