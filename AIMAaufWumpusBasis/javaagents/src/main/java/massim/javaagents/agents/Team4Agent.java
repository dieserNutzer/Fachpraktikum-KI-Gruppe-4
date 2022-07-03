package massim.javaagents.agents;

import eis.iilang.*;
import massim.javaagents.MailService;
import massim.javaagents.aimamassimworld.MassimAction;
import massim.javaagents.aimamassimworld.EfficientMassimAgent;
import massim.javaagents.aimamassimworld.MassimPercept;
import java.util.List;
import java.util.ArrayList;



public class Team4Agent extends Agent {

    private int lastID = -1;
    private EfficientMassimAgent massimAgent;
    
    boolean deactivated, onGoalZone, onRoleZone;
    int vision;
    Task task;
    Dispenser neighbouringDispenser;
    List<Block> knownBlock;
    List<Thing> attachedThing;
    int step = 0;
    boolean requestAction;
    String role;
    Block attachedBlock; // Das ist der zu der Aufgabe gehörende Block. TODO Muss überarbeitet werden, wenn Aufgaben mit mehreren Block angegangen werden.
    

    public Team4Agent(String name, MailService mailbox) {
        super(name, mailbox);
        massimAgent = new EfficientMassimAgent();
    	vision = 5;
    	role = "default";
    	knownBlock = new ArrayList<>();
    	attachedThing = new ArrayList<>();
    }
    
    
    protected class Task{
    	String name;
    	int deadline;
    	String type;
    	int x;
    	int y;
    	
    	protected Task(String n, int d, String t, int x1, int y1)
    	{
    		name = n;
    		deadline = d;
    		type = t;
    		x = x1;
    		y = y1;
    	}
    }
    
    protected class Thing{
    	int x;
    	int y;
    	
    	protected Thing(int x1, int y1)
    	{
    		x = x1;
    		y = y1;
    	}
    }
    
    protected class Dispenser{
    	String type;
    	int x;
    	int y;
    	
    	protected Dispenser(String t, int x1, int y1)
    	{
    		type = t;
    		x = x1;
    		y = y1;
    	}
    }
    
    protected class Block{
    	String type;
    	int x;
    	int y;
    	
    	protected Block(String t, int x1, int y1)
    	{
    		type = t;
    		x = x1;
    		y = y1;
    	}
    }
    
    public boolean hasTask()
    {
    	return (task != null);
    }
    
    protected void setTask(Task t)
    {
    	task = t;
    }
    
    protected void setDeactivated(boolean b)
    {
    	deactivated = b;
    }
    
    public boolean isDeactivated()
    {
    	return deactivated;
    }
    
    protected Action getMoveAction(MassimAction ma)
    {
    	Action retVal = null;
    	
    	
    	if (ma.getSymbol().equals("West"))
    	{
    		retVal = new Action("move", new Identifier("w"));
    	}
    	if (ma.getSymbol().equals("North"))
    	{
    		retVal = new Action("move", new Identifier("n"));
    	}
    	if (ma.getSymbol().equals("South"))
    	{
    		retVal = new Action("move", new Identifier("s"));
    	}
    	if (ma.getSymbol().equals("East"))
    	{
    		retVal = new Action("move", new Identifier("e"));
    	}
    	return retVal;
    }
    
    protected void resetValues()
    {
    	requestAction = false;
    	onGoalZone = false;
    	onRoleZone = false;
        neighbouringDispenser = null;
        knownBlock.clear();
        attachedThing.clear();
    }
    
    protected Action workOnTask()
    {
    	boolean dispenserReached = false;
    	boolean blockThereAndAttached = attachedBlock != null;
    	boolean orientationCorrect = attachedBlock != null && task != null ? (attachedBlock.x == task.x && attachedBlock.y == task.y) : false;
    	boolean wrongTypeConnected = blockThereAndAttached && (!attachedBlock.type.equals(task.type));
    	boolean submittable = blockThereAndAttached && onGoalZone && orientationCorrect;
    	
    	if (neighbouringDispenser != null)
    	{
    		if (neighbouringDispenser.type.equals(task.type))
    			dispenserReached = true;
    	}

    	// falls ein falscher Block attached ist, dann wird dieser weggeworfen
    	if (wrongTypeConnected)
    	{
    		String dir = "";
    		
    		if (attachedBlock.x == 0 && attachedBlock.y == -1)
    			dir = "n";
    		else if (attachedBlock.x == 0 && attachedBlock.y == 1)
    			dir = "s";
    		else if (attachedBlock.x == -1 && attachedBlock.y == 0)
    			dir = "w";
    		else if (attachedBlock.x == 1 && attachedBlock.y == 0)
    			dir = "e";
    		
        	return new Action("detach", new Identifier(dir));
    	}
    	if (submittable)
    	{
    		return new Action("submit", new Identifier(task.name));
    	}
    	// Agent hat Block attached
    	if (blockThereAndAttached)
    	{
    		// Alles gesammelt, laufe zur goal zone
    		if (!onGoalZone)
    		{
    			MassimAction massimAction = massimAgent.planRouteToGoalAction(attachedBlock.x, attachedBlock.y);
    			
    			// Kein Weg gefunden, dann scoute
    			if (massimAction.getSymbol().equals("No_op"))
    			{
    				massimAction = massimAgent.planScoutingAction(attachedBlock.x, attachedBlock.y);
    			}
    			
    			Action a = getMoveAction(massimAction);
    			
    			if (a!=null)
    				return a;
    			else
    				return new Action("skip");
    		}
    		// In der Goalzone, muss aber noch drehen
    		else
    		{
    			String dir;
    			int a;
    			if (attachedBlock.x == 0)
    			{
    				a = attachedBlock.y * task.x;
    				dir = a > 0 ? "ccw" : "cw";
    			}
    			else
    			{
    				a = attachedBlock.x * task.y;
    				dir = a > 0 ? "cw" : "ccw";
    			}
        		return new Action("rotate", new Identifier(dir));
    		}
    	}
    	// Stehe am Dispenser
    	else if (dispenserReached)
    	{
    		
    		Block blockToGrab = null;
    		
    		for (Block b : knownBlock)
    		{
    			if (b.x == neighbouringDispenser.x && b.y == neighbouringDispenser.y)
    				blockToGrab = b;
    		}
    		
    		// Block liegt schon dort
    		if (blockToGrab != null)
    		{
    			if (blockToGrab.x == 0 && blockToGrab.y == 1)
    			{
            		return new Action("attach", new Identifier("s"));
    			}
    			else if (blockToGrab.x == 0 && blockToGrab.y == -1)
    			{
            		return new Action("attach", new Identifier("n"));
    			}
    			else if (blockToGrab.x == 1 && blockToGrab.y == 0)
    			{
            		return new Action("attach", new Identifier("e"));
    			}
    			else if (blockToGrab.x == -1 && blockToGrab.y == 0)
    			{
            		return new Action("attach", new Identifier("w"));
    			}
    		}
    		// Block muss requested werden
    		else
    		{
    			if (neighbouringDispenser != null)
    			{
        			if (neighbouringDispenser.x == 0 && neighbouringDispenser.y == 1)
        			{
                		return new Action("request", new Identifier("s"));
        			}
        			else if (neighbouringDispenser.x == 0 && neighbouringDispenser.y == -1)
        			{
                		return new Action("request", new Identifier("n"));
        			}
        			else if (neighbouringDispenser.x == 1 && neighbouringDispenser.y == 0)
        			{
                		return new Action("request", new Identifier("e"));
        			}
        			else if (neighbouringDispenser.x == -1 && neighbouringDispenser.y == 0)
        			{
                		return new Action("request", new Identifier("w"));
        			}
    			}
    		}
    	}
    	// Agent muss zum Dispenser
    	else
    	{
			MassimAction massimAction = massimAgent.planRouteToDispenserAction(task.type);
			
			// Kein Weg zum Dispenser gefunden, also scout
			if (massimAction.getSymbol().equals("No_op"))
			{
				massimAction = massimAgent.planScoutingAction();
			}
			
			Action a = getMoveAction(massimAction);
			
			if (a!=null)
				return a;
			else
				return new Action("skip");
    	}
    	// dieser Code sollte eigentlich nicht erreicht werden
    	return new Action("skip");
    }
    
    protected void readPercepts()
    {

    	
        List<MassimPercept> perceptListToAIMA = new ArrayList<>();
        int k, x, y;
        boolean perceptFound;
        String lastActionResult = "";
        String lastActionPara = "";
        String lastAction = "";
        MassimPercept mp;

    	// Alle Felder in Sichtweite werden zunächst als begehbar gesetzt.
        // Erhalten wir ein Percept, das anderes sagt, wir dies später geändert.
    	for (int i = -vision; i <= vision; i++)
        {
        	if (i < 0)
        	{
        		k = vision + i;
        	}
        	else
        	{
        		k = vision - i;
        	}
        	
        	for (int j = -k; j<=k; j++)
        	{
        		mp = new MassimPercept();
                mp.walkable(i, j);
                perceptListToAIMA.add(mp);
        	}
        }
    	
    	List<Percept> percepts = getPercepts();
    	
    	
    	
        for (Percept percept : percepts) {
        	
        	perceptFound = false;
        	
        	switch (percept.getName())
        	{
        		case "thing":
        		{
            		x = ((Numeral) percept.getParameters().get(0)).getValue().intValue();
            		y = ((Numeral) percept.getParameters().get(1)).getValue().intValue();
            		
            		switch (((Identifier) percept.getParameters().get(2)).getValue())
            		{
            			case "dispenser":
            			{
            				if ((x == 0 && (y == 1 || y == -1)) || (y == 0 && (x == 1 || x == -1)))
                			{
                				if (task != null && ((Identifier) percept.getParameters().get(3)).getValue().equals(task.type))
                				{
                					neighbouringDispenser = new Dispenser(((Identifier) percept.getParameters().get(3)).getValue(), x, y);
                				}
                			}
            				
            				// angelegte Percepts duchsuchen, und denjenigen, mit Koordinaten wie dispenser, als dispenser markieren
                			for (int i=0; (i < perceptListToAIMA.size()) && !perceptFound; i++)
                			{
                				mp = perceptListToAIMA.get(i);
                				if (x == mp.getXValue() && y == mp.getYValue())
                				{
                					mp.setDispenser(x, y, ((Identifier) percept.getParameters().get(3)).getValue());
                					perceptFound = true;
                				}
                			}
                			break;
            			}
            			
            			case "block":
            			{
            				// direkt an Agenten angrenzenden Block in Liste aufnehmen
                			if (x == 0 && (y == 1 || y == -1) || y == 0 && (x == 1 || x == -1))
                			{
                				knownBlock.add(new Block(((Identifier) percept.getParameters().get(3)).getValue(), x, y));
                			}
                			
                			for (int i=0; (i < perceptListToAIMA.size()) && !perceptFound; i++)
                			{
                				mp = perceptListToAIMA.get(i);
                				if (mp.getXValue() == x && mp.getYValue() == y)
                				{
                					mp.setEntity(x, y); // TODO setEntity umbenennen (ist ein Sammler für vorrübergehende Hindernisse)
                					perceptFound = true;
                				}
                			}
                			if (!perceptFound)
                			{
                				mp = new MassimPercept();
                				mp.setEntity(x, y);
                			}
                			break;
            			}
            			
            			case "obstacle":
            			{
                			for (int i=0; (i < perceptListToAIMA.size()) && !perceptFound; i++)
                			{
                				mp = perceptListToAIMA.get(i);
                				if (mp.getXValue() == x && mp.getYValue() == y)
                				{
                					mp.setObstacle(x, y);
                					perceptFound = true;
                				}
                			}
                			if (!perceptFound)
                			{
                				mp = new MassimPercept();
                				mp.setObstacle(x, y);
                			}
                			break;
            			}
            			
            			case "entity":
            			{ 
                			for (int i=0; (i < perceptListToAIMA.size()) && !perceptFound; i++)
                			{
                				mp = perceptListToAIMA.get(i);
                				if (mp.getXValue() == x && mp.getYValue() == y)
                				{
                					mp.setEntity(x, y);
                					perceptFound = true;
                				}
                			}
                			if (!perceptFound)
                			{
                				mp = new MassimPercept();
                				mp.setEntity(x, y);
                			}
                			break;
            			}
            			
            			// Ende von Switch zur Unterscheidung der Things
            			default:
            				break;
            		}
            		break; // break zum case thing
        		}
        		
        		case "deactivated":
        		{
            		
            		if(((Identifier) percept.getParameters().get(0)).getValue().equals("true"))
            			setDeactivated(true);
            		else
            			setDeactivated(false);
            		break;
        		}
        		
        		case "lastActionResult":
        		{
            		
            		lastActionResult = ((Identifier) percept.getParameters().get(0)).getValue();
            		break;
        		}
        		
        		case "lastAction":
        		{
            		
        			lastAction = ((Identifier) percept.getParameters().get(0)).getValue();
            		break;
        		}
        		
        		case "lastActionParams":
        		{
            		
            		ParameterList pl = ((ParameterList) percept.getParameters().get(0));
            		if (pl.size() > 0)
            		{
            			lastActionPara = ((Identifier) pl.get(0)).getValue();
            		}
            		break;
        		}
        		
        		case "task":
        		{
            		
            		ParameterList pl = ((ParameterList) percept.getParameters().get(3));
            		Function f = (Function) pl.get(0);
            		List<Parameter> lp = f.getParameters();
            		int xx = ((Numeral) lp.get(0)).getValue().intValue();
            		int yy = ((Numeral) lp.get(1)).getValue().intValue();
            		String s = ((Identifier) lp.get(2)).getValue();
            		
            		
            		if (pl.size() == 1) {
            			if (task == null || step > task.deadline)
            				setTask(new Task(((Identifier) percept.getParameters().get(0)).getValue(), ((Numeral) percept.getParameters().get(1)).getValue().intValue(), s, xx, yy));
            		}
            		
            		break;
        		}
        		
        		case "role":
        		{
            		
            		if (percept.getParameters().size() == 1)
            		{
            			role = ((Identifier)percept.getParameters().get(0)).getValue();
            		}
            		break;
        		}
        		
        		case "roleZone":
        		{
            		
            		x = ((Numeral) percept.getParameters().get(0)).getValue().intValue();
            		y = ((Numeral) percept.getParameters().get(1)).getValue().intValue();
            		
            		
            		for (int i=0; (i < perceptListToAIMA.size()) && !perceptFound; i++)
        			{
        				mp = perceptListToAIMA.get(i);
        				
        				if (mp.getXValue() == x && mp.getYValue() == y)
        				{
        				
        				mp.setRoleZone(x, y);
        				perceptFound = true;
        				}
        			}
            		
            		if (!perceptFound)
            		{
            			mp = new MassimPercept();
            			mp.setRoleZone(x, y);
            		}
            		
            		if (x == 0 && y == 0)
            		{
            			onRoleZone = true;
            		}
            		break;
        		}
        		
        		case "goalZone":
        		{
            		
            		x = ((Numeral) percept.getParameters().get(0)).getValue().intValue();
            		y = ((Numeral) percept.getParameters().get(1)).getValue().intValue();        		
            		
            		for (int i=0; (i < perceptListToAIMA.size()) && !perceptFound; i++)
        			{
        				mp = perceptListToAIMA.get(i);
        				
        				if (mp.getXValue() == x && mp.getYValue() == y)
        				{
        				
        				mp.setGoalZone(x, y);
        				perceptFound = true;
        				}
        			}
            		
            		if (!perceptFound)
            		{
            			mp = new MassimPercept();
            			mp.setGoalZone(x, y);
            		}
            		
            		
            		if (x == 0 && y == 0)
            		{
            			onGoalZone = true;
            		}
            		break;
        		}
        		
        		case "requestAction":
        		{
            		
        			requestAction = true;
            		break;
        		}
        		
        		/*case "step":
        		{
            		
        			step = ((Numeral) percept.getParameters().get(0)).getValue().intValue();
            		break;
        		}*/
        		
        		case "attached":
        		{
            		
            		int attachedX = ((Numeral) percept.getParameters().get(0)).getValue().intValue();
            		int attachedY = ((Numeral) percept.getParameters().get(1)).getValue().intValue();
            		
            		Thing t = new Thing(attachedX, attachedY);
            		attachedThing.add(t);
            		break;
        		}
        		
        		default:
        			break;
        			
        	} // switch (percept.getName())
        		
        		 // TODO Es gibt noch weitere Percepts
     
        } // for (Percept percept : percepts)
        
        
        
        if (lastAction.equals("move") && lastActionResult.equals("success"))
        {
        	if (lastActionPara.equals("n"))
        	{
        		massimAgent.updateAgentPosition(MassimAction.NORTH);
        	}
        	else if (lastActionPara.equals("w"))
        	{
        		massimAgent.updateAgentPosition(MassimAction.WEST);
        	}
        	else if (lastActionPara.equals("e"))
        	{
        		massimAgent.updateAgentPosition(MassimAction.EAST);
        	}
        	else if (lastActionPara.equals("s"))
        	{
        		massimAgent.updateAgentPosition(MassimAction.SOUTH);
        	}
        }

        
        if (requestAction)
        	massimAgent.tellStuff(perceptListToAIMA);
        
        
    }
    
    protected Action decideAction() {
    	
    	attachedBlock = null;
    	
    	// Bestimmen, ob ein Block am Agenten attached ist
    	for (Block b : knownBlock)
    	{
    		for (Thing t : attachedThing)
    		{
    			if (b.x == t.x && b.y == t.y) {
    				attachedBlock = b;}
    		}
    	}
    	
    	// Wenn kein aktuellen Task verfolgt wird, dann alle Blöcke wegwerfen
    	// TODO soll später nicht mehr so sein
        if(!hasTask())
        {
        	String dir = "";
        	
        	if (attachedBlock != null)
        	{
        		if (attachedBlock.x == 0 && attachedBlock.y == -1)
        			dir = "n";
        		else if (attachedBlock.x == 0 && attachedBlock.y == 1)
        			dir = "s";
        		else if (attachedBlock.x == -1 && attachedBlock.y == 0)
        			dir = "w";
        		else if (attachedBlock.x == 1 && attachedBlock.y == 0)
        			dir = "e";
        		
            	return new Action("detach", new Identifier(dir));
        	}
        }
        
        // Wenn deaktiviert, dann keine Aktion
        if (isDeactivated())
        {
        	return new Action("skip");
        }
        // Falls keine Aktion angefragt wird, wird auch keine berechnet
        else if (!requestAction)
        {
        	return new Action("skip");
        }
        // TODO Für Turnier 3: nur worker 
        else if (!role.equals("worker"))
        {
        	// Falls in der Rollen Zone: dann zum worker wechseln
        	if (onRoleZone)
        	{
        		return new Action("adapt", new Identifier("worker"));
        	}
        	
        	// plane ansonsten Route zur nächsten rolezone
			MassimAction massimAction = massimAgent.planRouteToRoleZone();
			
			// Falls kein Weg gefunden, dann erkunde, bis du einen Weg findest
			if (massimAction.getSymbol().equals("No_op"))
			{
				massimAction = massimAgent.planScoutingAction();
			}
			
			Action action = getMoveAction(massimAction);
			
			if (action == null)
				return new Action("skip");
			
			return action;     	
        }
        else if (hasTask())
        {;
        	return workOnTask();
        }
        // scout
        else
        {
        	MassimAction massimAction = massimAgent.planScoutingAction();
        	
        	Action action = getMoveAction(massimAction);
        	
			if (action == null)
				return new Action("skip");
			
			return action;   
        }
    	
    }

    @Override
    public void handlePercept(Percept percept) {}

    @Override
    public void handleMessage(Percept message, String sender) {}

    @Override
    public Action step() {
        step++;
        

        resetValues();
        readPercepts();
        


        return decideAction();
    }
}
