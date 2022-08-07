package massim.javaagents.agents;

import eis.iilang.*;
import massim.javaagents.MailService;
import massim.javaagents.aimamassimworld.MassimAction;
import massim.javaagents.aimamassimworld.EfficientMassimAgent;
import massim.javaagents.aimamassimworld.MassimPercept;
import java.util.List;
import java.util.ArrayList;

/* Unsere Agentenklasse von Massim. Über die step()-Methode werden vom Massim-Server alle Percepts mitgeteilt,
 * der Server erwartet als Antwort eine Action (z. B. move) und ggf. Parameter zu der Action (z.B. eine Richtung,
 * (n wie North). Ein bisschen ungewoöhnlich ist, dass dieser Agent dann einen weiteren Agenten vom Typ
 * EfficientMassimAgent kennt. Der EfficientMassimAgent basiert auf dem Wumpus-Beispiel, ist aber nur noch für
 * Fragen bzgl. der Karte und  Wegfindung zuständig. Während EfficientMassimAgent mit absoluten Koordinaten
 * arbeitet, sind in dieser Klasse (Team4Agent) alle Infomationen auf relativen Koordinaten basierend. Dadurch
 * werden ein paar Informationen doppelt gehalten (einmal in Team4Agent und einmal in EfficientMassimAgent).
 * Wir können die beiden auch verschmelzen, da spricht nix dagegen.
 */

public class Team4Agent extends Agent {

    private int lastID = -1;
    private EfficientMassimAgent massimAgent;
    
    boolean deactivated, onGoalZone, onRoleZone; // ob der Agent deaktiviert ist, bzw. sich in der Goal- oder Rolezone befindet.
    // Markiert, ob der Server ein Action erfragt, sollte fast immer true sein, außer bei sim-Start und Ende.
    boolean requestAction; 
    // Der aktuelle Step der Simulation, sollte sich bei jedem Aufruf von step() um 1 erhöhen.
    int step;
    // Die Sichweite des Agenten, in der er Informationen bzgl. seiner Umwelt mitgeteilt bekommt.
    // TODO Das Auslesen der Sichtweite funktioniert noch nicht. Ist aktuell fest auf 5 gesetzt (entspechend Szenario zu Turnier3)
    int vision;
    // Hält Informationen über die aktuell vom Agenten verfolgte Task
    Task task; 
    // Wenn direkt neben dem Agenten en Dispenser mit gesuchtem Block existiert, so wird dies hier eingetragen, ansosten ist die Var. null 
    Dispenser neighbouringDispenser; 
    // Eine Liste von Blocks im aktuellen Sichtfeld des Agenten
    List<Block> knownBlock;
    // Eine Liste von Dingen, die aktuell am Agenten attached sind.
    List<Thing> attachedThing;
    // Die aktuelle Rolle des Agenten, aktuell noch als String. Wird bestimm nochmal geändert, wenn mehr mit Rollen gearbeitet wird.
    String role;
    // Das ist der zu der Aufgabe gehörende Block, wenn er am Agenten attached ist. TODO Muss überarbeitet werden, wenn Aufgaben mit mehreren Block angegangen werden.
    Block attachedBlock;
    // Anzahl der Schritte, für die ein Agent die Aktion attach nicht ausführt, um zu vermeiden, dass mehrere Agenten den gleichen Block greifen
    int dontTouch;
    // taskValid ist true, wenn der Server die aktuell bearbeitete Task als Percept zurück gibt. Sonst ist sie abgelaufen.
    boolean taskValid;
    

    public Team4Agent(String name, MailService mailbox) {
        super(name, mailbox);
        massimAgent = new EfficientMassimAgent();
    	vision = 5; // TODO aus aktueller Rolle auslesen
    	role = "default"; // TODO aus Percepts auslesen
    	knownBlock = new ArrayList<>();
    	attachedThing = new ArrayList<>();
    	step = 0;
    }
    
    // Die lokalen Klassen, die Informationen zu aktuellem Task, becnahbartem Dispenser etc. halten können auch in regälre Klassen ausgelagert werden. Ist aber kein Muss.
    protected class Task{
    	String name;
    	int deadline;
    	String type;
    	int x;
    	int y;
    	
    	protected Task(String n, int d, String t, int x1, int y1)
    	{
    		name = n;
    		deadline = d; // Wann ist Task abgelaufen ?
    		type = t; // Welchen Block benötigt task (zurzeit betrachten wir nur Taks mit einem Block)
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
    		type = t; // Typ des Blocks den der Dispenser ausgibt.
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
    
    // Diese getter- und setter-Klassen sind nicht konsequent durchgezogen worden. Aufgrund von Zeitmangel...
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
    
    // Da der EfficientMassimAgent seinen Vorschlag als MassimAction zurück gibt,
    // muss dies in eine Action (erwartet der Massim-Server) übersetzt werden.
    protected Action getMoveAction(MassimAction ma)
    {
    	Action retVal = null;
    	
    	if (ma == null)
    	{
    		return null;
    	}
    	
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
    
    // zurücksetzen von Informationen, die jeden Takt neu ausgewertet werden müssen.
    protected void resetValues()
    {
    	requestAction = false;
    	onGoalZone = false;
    	onRoleZone = false;
        taskValid = false;
        neighbouringDispenser = null;
        knownBlock.clear();
        attachedThing.clear();
        
        // dontTouch muss in jedem Step um eins reduziert werden. (Das ist die Anzahl der Steps, in denen ein Agent kein attach
        // durchführen soll)
        if (dontTouch >= 0)
        	dontTouch -= 1;
    }
    
    // Wird aufgerufen, wenn der Agent an einer Task arbeitet.
    protected Action workOnTask()
    {
    	boolean dispenserReached = false; // true, wenn Agent neben dispenser steht, der den benötigten Block enthält.
    	// true, wenn Agent einen Block attached hat
    	boolean blockThereAndAttached = attachedBlock != null;
    	// Es existiert ein Block mit korrekter Ortientierung, wenn die Koordinaten des
    	// attachten Block den geforderten Koordinaten der Task entsprechen.
    	boolean orientationCorrect = (attachedBlock != null && task != null) ? (attachedBlock.x == task.x && attachedBlock.y == task.y) : false;
    	// true, wenn es einen attachten Block gibt, der nicht dem Typ der aktuell verfolgten Task entspricht
    	boolean wrongTypeConnected = blockThereAndAttached && (!attachedBlock.type.equals(task.type));
    	// Task kann submitted werden, wenn Block in korrekter Orientierung vorliegt, un der Agent in einer Goalzone ist
    	boolean submittable = blockThereAndAttached && onGoalZone && orientationCorrect;
    	
    	// Gibt es einen direkt angrenzenden Dispenser richtigem Typs?
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
    	
    	// falls alle Vorraussetzungen vorliegen, einen Task zu submitten, wird diese Action ausgewählt.
    	if (submittable)
    	{
    		return new Action("submit", new Identifier(task.name));
    	}
    	
    	// Agent hat Block attached
    	if (blockThereAndAttached)
    	{
    		// Nicht in der goal zone, aber block attached, also laufe zur goal zone
    		if (!onGoalZone)
    		{
    			// Erfrage vom EfficientMassimAgent Weg zur goalZone. Der EfficientMassimAgent hat goalZone-Felder
    			// selbst gespeichert. Der Methode werden die relativen Koordinaten des angehängten Blocks
    			// mitgegeben, damit dass bei der Wegfindung berücksichtigt wird ( der Agent nicht mit dem Block
    			// an einem Hindernis hängen bleibt).
    			MassimAction massimAction = massimAgent.planRouteToGoalAction(attachedBlock.x, attachedBlock.y);
    			
    			// Kein Weg gefunden (ist der Fall, wenn EfficientMassimAgent No_op zurückgibt), dann scoute
    			if (massimAction.getSymbol().equals("No_op"))
    			{
    				// Erfrage vom EfficientMassimAgent Weg zur nächsten nicht bekanntem Feld. Der EfficientMassimAgent
        			// selbst gespeichert, welche Felder erkundet sind und welche nicht.
    				// Der Methode werden die relativen Koordinaten des angehängten Blocks
        			// mitgegeben, damit dass bei der Wegfindung berücksichtigt wird ( der Agent nicht mit dem Block
        			// an einem Hindernis hängen bleibt).
    				massimAction = massimAgent.planScoutingAction(attachedBlock.x, attachedBlock.y);
    			}
    			
    			
    			// MassimAction von EfficientMassimAgent in Action für MassimServer übersetzen.
    			Action a = getMoveAction(massimAction);
    			
    			if (a!=null)
    				return a;
    			else
    				return new Action("skip");
    		}
    		// In der Goalzone, muss aber noch drehen
    		else
    		{
    			// Berechnet, ob Drehung counterwise oder clockcounterwise effizienter ist.
    			// TODO Es wird nicht betrachtet, ob Drehung möglich ist (Hindernis)
    			// Agent könnte dadurch feststecken
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
    	// Stehe am Dispenser, der den Block richtigen Typs erzeugt
    	else if (dispenserReached)
    	{
    		
    		Block blockToGrab = null;
    		
    		// Gehe durch die Liste aller in diesem Schritt gesehenen Blöcke und siehe nach,
    		// ob dieser auf dem Platz des Dispensers liegt. Dann kann er ihn attachen
    		// (aktuell nimmt ein Agent nur Blööcke vom Dispenser)
    		for (Block b : knownBlock)
    		{
    			if (b.x == neighbouringDispenser.x && b.y == neighbouringDispenser.y)
    				blockToGrab = b;
    		}
    		
    		// Block liegt bereit
    		if (blockToGrab != null)
    		{
    			// Wenn dontTouch > 0, hat ein anderer Agent einen Block gegriffen. Um zu vermeide, dass
    			// zwei Agenten den gleich Block greifen, wird ein paar Steps gewartet, um einen Agenten mit Block
    			// weggehen zu lassen.
    			if (dontTouch > 0)
    			{
    				return new Action("skip");
    			}
    			
    			// Agent greift sich einen Block und sagt den anderen Agenten, keinen Block zu attachen
    			mailbox.broadcast(new Percept("DontTouch"), getName());
    			
    			// Block nehmen, dass heißt: an den Server attach mit richtigem Parameter
    			// senden.
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
    			// Block beim benachbartem Dispenser erfragen
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
    	// Der Agent hat noch keine Block und steht nocht nicht beim Dispenser
    	// ---> Agent muss zum Dispenser
    	else
    	{
    		// Erfrage vom EfficientMassimAgent Weg zur zum Dispnser des angegeben Typen.
    		// Der EfficientMassimAgent hat Dispenser-Felder
			// selbst gespeichert. Der Methode wird der Typ des angefragten Blocks
			// mitgegeben, damit nicht zum falschen Typ dispenser gelaufen wird.
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
    
    // Einlesen und verarbeiten der Percepts vom Massim Server
    protected void readPercepts()
    {

    	// in dieser Liste werden die Percepts abgelegt, die dem EfficientMassimAgent mitgeteilt werden,
    	// da er diese für Erstellung der Karte und anschließende Wegfindung benötigt.
        List<MassimPercept> perceptListToAIMA = new ArrayList<>();
        int k, x, y;
        // Hilfsvariable...
        boolean perceptFound;
        // Percepts enthalten Informationen, ob die letzte Aktion erfolgreich war. Dafür werden Infos in den
        // nächsten drei Vars. gespeichert, damit das - wenn nötig - dem EfficientMassimAgent mitgeteilt wird.
        String lastActionResult = "";
        String lastActionPara = "";
        String lastAction = "";
        // Hilfsvariable... Wird meistens beim Schleifendurchlauf genutzt
        MassimPercept mp;

    	// Alle Felder in Sichtweite werden zunächst als begehbar gesetzt.
        // Erhalten wir ein Percept, das anderes sagt, wir dies später geändert.
        
        // Dies erscheint notwendig, da uns der Server nicht über freie Felder informiert,
        // sondern nur über Dinge auf den Feldern.
        
        // Die verschachtelte for-Schleife sorgt dafür, dass (i,j) nacheinander alle
        // relativen Koordinaten bezogen auf den Agenten in seiner Sichtweite annehmen.
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
    	
    	
    	
    	// Abfrage aller Percepts
    	List<Percept> percepts = getPercepts();
    	
    	// Gehe nacheinander alle Percepts durch...
        for (Percept percept : percepts) {
        	
        	perceptFound = false;
        	
        	// Abfrage des Namen des Percepts (beschreibt die Art des Percepts, vgl. Massim-Doku)
        	switch (percept.getName())
        	{
        	// Ein Thing wurde gefunden
        		case "thing":
        		{
        			// (x,y) sind die relativen Koordinaten des gefundenen Things
        			// Bei einem Thing sollten Parameter 0 und 1 immer die Koordinaten sein,
        			// und vom Typ Numeral sein.
            		x = ((Numeral) percept.getParameters().get(0)).getValue().intValue();
            		y = ((Numeral) percept.getParameters().get(1)).getValue().intValue();
            		
            		// Es gibt verschiedene Arten eines Things, dies is in Parameter 2 definiert (Typ Identifier)
            		switch (((Identifier) percept.getParameters().get(2)).getValue())
            		{
            			// Percept beschreibt dispenser...
            			case "dispenser":
            			{
            				// Falls der Dispenser direkt benachbart ist...
            				if ((x == 0 && (y == 1 || y == -1)) || (y == 0 && (x == 1 || x == -1)))
                			{
            					// und falls der Typ des Dispensers dem Typen des benötigten Blocks der Task entspricht...
                				if (task != null && ((Identifier) percept.getParameters().get(3)).getValue().equals(task.type))
                				{
                					// ... dann setze diese Variable. 
                					// Damit weiß der Agent, dass er neben einem gesuchten Dispenser steht. (Sonst ist die Variable null.=
                					neighbouringDispenser = new Dispenser(((Identifier) percept.getParameters().get(3)).getValue(), x, y);
                				}
                			}
            				
            				// Die oben angelegten MassimPercepts, die an den EfficientMassimAgent gesendet werden sollen,
            				// werden durchsucht, bis der Percept, mit denselben Koordinaten wie der Dispenser gefunden wurde.
            				// Dieser Percept wird dann als dispenser markiert und mit Koordinaten und Typ des Dispensers versehen
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
            			
            			// Thing ist ein Block
            			case "block":
            			{
            				// Falls der Block als direkt an den Agenten angrenzend erkannt wird, wird er in die Liste
            				// der bekannten Blöcke aufgenommen
                			if (x == 0 && (y == 1 || y == -1) || y == 0 && (x == 1 || x == -1))
                			{
                				knownBlock.add(new Block(((Identifier) percept.getParameters().get(3)).getValue(), x, y));
                			}
                			
                			// Die oben angelegten MassimPercepts, die an den EfficientMassimAgent gesendet werden sollen,
            				// werden durchsucht, bis der Percept, mit denselben Koordinaten wie dieser Block gefunden wurde.
            				// Dieser Percept wird dann als Block markiert und mit Koordinaten versehen
                			for (int i=0; (i < perceptListToAIMA.size()) && !perceptFound; i++)
                			{
                				mp = perceptListToAIMA.get(i);
                				if (mp.getXValue() == x && mp.getYValue() == y)
                				{
                					mp.setEntity(x, y); // TODO setEntity umbenennen (ist ein Sammler für vorrübergehende Hindernisse)
                					perceptFound = true;
                				}
                			}
                			
                			// Sollte für den Block kein bereits angelegter Percept gefunden worden sein, dann lege ihn an
                			// (Sollte aber eigentlich nicht (mehr) vorkommen.
                			if (!perceptFound)
                			{
                				mp = new MassimPercept();
                				mp.setEntity(x, y);
                			}
                			break;
            			}
            			
            			// Thing ist obstacle
            			case "obstacle":
            			{
            				// Die oben angelegten MassimPercepts, die an den EfficientMassimAgent gesendet werden sollen,
            				// werden durchsucht, bis der Percept, mit denselben Koordinaten wie dieses obstacle gefunden wurde.
            				// Dieser Percept wird dann als obstacle markiert und mit Koordinaten versehen
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
            			
            			// Thing ist entity (ein Agent)
            			case "entity":
            			{ 
            				// Die oben angelegten MassimPercepts, die an den EfficientMassimAgent gesendet werden sollen,
            				// werden durchsucht, bis der Percept, mit denselben Koordinaten wie diese entity gefunden wurde.
            				// Dieser Percept wird dann als entity markiert und mit Koordinaten versehen
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
        		
        		// Percept teilt mit, ob Agent deactivated ist. Wird in entsprechendem Attribut gespeichert.
        		case "deactivated":
        		{
            		
            		if(((Identifier) percept.getParameters().get(0)).getValue().equals("true"))
            			setDeactivated(true);
            		else
            			setDeactivated(false);
            		break;
        		}
        		
        		// Resultat der im letzten step an den Server übergebenen Action.
        		// Ist im besten Fall "success", kann aber auch einer der vielen Fails sein.
        		case "lastActionResult":
        		{
            		
            		lastActionResult = ((Identifier) percept.getParameters().get(0)).getValue();
            		break;
        		}
        		
        		// Welches war die zuletzt übergebene Aktion (z. B. move)
        		case "lastAction":
        		{
            		
        			lastAction = ((Identifier) percept.getParameters().get(0)).getValue();
            		break;
        		}
        		
        		// Wenn die Variable mehr als 0 Parameter hat, wird der erste (nach Java-Zählart nullte)
        		// Parameter gespeichert.
        		case "lastActionParams":
        		{
            		
            		ParameterList pl = ((ParameterList) percept.getParameters().get(0));
            		if (pl.size() > 0)
            		{
            			lastActionPara = ((Identifier) pl.get(0)).getValue();
            		}
            		break;
        		}
        		
        		// Percepts sagt etwas über zur Verfügung stehende Task aus.
        		case "task":
        		{
        			// Dieser Percept ist recht verschachtelt und meine gewählten Parameter sind nicht ideal
        			// (da war wohl die Motivatrion gerade im Keller)
            		
        			// Liste aller Blöcke, die für diese Task benötigt werden.
            		ParameterList pl = ((ParameterList) percept.getParameters().get(3));
            		Function f = (Function) pl.get(0);
            		List<Parameter> lp = f.getParameters();
            		// Koordinaten des Blocks
            		int xx = ((Numeral) lp.get(0)).getValue().intValue();
            		int yy = ((Numeral) lp.get(1)).getValue().intValue();
            		// Typ des Blocks
            		String s = ((Identifier) lp.get(2)).getValue();
            		
            		
            		// Falls der Name der im Percept mitgeteilten Task der aktuell bearbeiteten Task entspricht,
            		// dann ist diese Valid und kann weiter bearbeitet werden.
            		if (task != null && task.name.equals(((Identifier) percept.getParameters().get(0)).getValue()))
            			taskValid = true;
            		
            		
            		// Falls Task genau einen Block benötigt (andere werden aktuell noch nicht betrachtet)
            		if (pl.size() == 1) {
            			// Falls der AGent aktuell keie Task verfolgt oder die Task abgelaufen ist
            			if (task == null || step > task.deadline)
            			{
            				// kreiere neue Task, die vom Agenten verfolgt werden soll.
            				setTask(new Task(
            						((Identifier) percept.getParameters().get(0)).getValue(), // Name der Task
            						((Numeral) percept.getParameters().get(1)).getValue().intValue(), // deadline der Task
            						s, xx, yy)); // Typ des benötigten Blocks, und die Koordinaten, an denen sich der Block bei submit befinden muss
            				// Task ist valid und kann bearbeitet werden, da diese gerade aus den Percepts entnommen worden ist.
            				taskValid = true;
            			}
            		}
            		
            		break;
        		}
        		
        		// Percept sagt etwas über Rollen...
        		case "role":
        		{
            		
        			// Wenn ich es richtig herausgelesen habe, beschreibt ein role-Percept mit genau einem Parameter
        			// die aktuelle Rolle des Agenten
        			// Es gibt Role-Percepts mit mehr Parametern, diese beschreiben die Eigenschaften der zur
        			// Verfügung stehenden Rollen. // TODO Diese werden aber zurzeit nicht verarbeitet.
            		if (percept.getParameters().size() == 1)
            		{
            			// setze die aktuelle Rolle des Agenten
            			role = ((Identifier)percept.getParameters().get(0)).getValue();
            		}
            		break;
        		}
        		
        		// Percept beschreibt ein Feld mit einer Role-Zone 
        		case "roleZone":
        		{
            		
        			// Koordinaten des Felds.
            		x = ((Numeral) percept.getParameters().get(0)).getValue().intValue();
            		y = ((Numeral) percept.getParameters().get(1)).getValue().intValue();
            		
    				// Die oben angelegten MassimPercepts, die an den EfficientMassimAgent gesendet werden sollen,
    				// werden durchsucht, bis der Percept, mit denselben Koordinaten wie dieses Feld gefunden wurde.
    				// Dieser Percept wird dann als role-Zone markiert und mit Koordinaten versehen
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
            		
            		// Agent steht auf RoleZoneFeld
            		if (x == 0 && y == 0)
            		{
            			onRoleZone = true;
            		}
            		break;
        		}
        		
        		// Percept beschreibt ein Feld mit einer Goal-Zone 
        		case "goalZone":
        		{
        			// Koordinaten des Felds.
            		x = ((Numeral) percept.getParameters().get(0)).getValue().intValue();
            		y = ((Numeral) percept.getParameters().get(1)).getValue().intValue();        		
            		
    				// Die oben angelegten MassimPercepts, die an den EfficientMassimAgent gesendet werden sollen,
    				// werden durchsucht, bis der Percept, mit denselben Koordinaten wie dieses Feld gefunden wurde.
    				// Dieser Percept wird dann als goal-Zone markiert und mit Koordinaten versehen
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
            		
            		// Agent steht auf GoalZoneFeld
            		if (x == 0 && y == 0)
            		{
            			onGoalZone = true;
            		}
            		break;
        		}
        		
        		// Percept besagt, dass eine Action im step erfragt wird (gibt es nicht bei sim-Start)
        		case "requestAction":
        		{
            		
        			requestAction = true;
            		break;
        		}
        		
        		// Der Server teilt die aktuelle Step-Nummer mit
        		case "step":
        		{
            		
        			step = ((Numeral) percept.getParameters().get(0)).getValue().intValue();
            		break;
        		}
        		
        		// Percepts beschreibt ein Ding, dass attached ist
        		case "attached":
        		{
            		
        			// relative Koordinaten des Dings
            		int attachedX = ((Numeral) percept.getParameters().get(0)).getValue().intValue();
            		int attachedY = ((Numeral) percept.getParameters().get(1)).getValue().intValue();
            		
            		// Füge Ding zur Liste der attachten Dinge hinzu
            		Thing t = new Thing(attachedX, attachedY);
            		attachedThing.add(t);
            		break;
        		}
        		
        		default:
        			break;
        			
        	} // switch (percept.getName())
        		
        		 // TODO Es gibt noch weitere Percepts
     
        } // for (Percept percept : percepts)
        
        
        
        
        
        
        // Sollte beim Durchgehen aller Percepts festgestellt worden sein,
        // dass ein erfolgreiches move durchgeführt worden ist,
        // dann teile dass dem EfficientMassimAgent mit, damit dieser die
        // aktuelle Position aktualisiert.
        
        
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
        
        // Da letzte Runde ein Block erfolgreich attached worden ist, wird anderen
        // Agenten mitgeteilt, nicht zu attachen.
        if (lastAction.equals("attach") && lastActionResult.equals("success"))
        {
        	mailbox.broadcast(new Percept("DontTouch"), getName());
        }

        
        // Sende die von PErcepts erfassten Daten nur an den EfficientMassimAgent, wenn
        // ein request-Action-Percept vorlag. In den anderen STeps kommen auch keine
        // Infos über Felder, und es würden zu viele Felder zu unrecht als begehbar
        // eingestuft.
        if (requestAction)
        	massimAgent.tellStuff(perceptListToAIMA);
    }
    
    // In dieser Methode wird entschieden, welche Action ausgeführt wird.
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
    	
    	// Falls der Agent noch eine Task besitzt, diese aber nicht valid ist, dann setze
    	// aktuelle Task auf null und skippe die Aktion.
    	if (!taskValid && task != null)
    	{
    		task = null;
    		return new Action("skip");
    	}
    	
    	// Wenn keine aktuelle Task verfolgt wird, dann alle Blöcke wegwerfen
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
        
        // Wenn Agent deaktiviert, dann keine Aktion (spart Rechnerei)
        if (isDeactivated())
        {
        	return new Action("skip");
        }
        
        // Falls keine Aktion angefragt wird, wird auch keine berechnet
        else if (!requestAction)
        {
        	return new Action("skip");
        }
        
        // Für Turnier 3 soll der Agent so schnell wie möglich die Rolle worker annehmen
        // TODO Das soll später nicht mehr unbedingt so sein
        // Falls AGent kein worker, dann mache folgendes:
        else if (!role.equals("worker"))
        {
        	// Falls Agent in der Rollen Zone: dann zum worker wechseln
        	if (onRoleZone)
        	{
        		return new Action("adapt", new Identifier("worker"));
        	}
        	
        	// plane ansonsten Route zur nächsten rolezone
        	// Erfrage dazu Aktion beim EfficientMassimAgent
			MassimAction massimAction = massimAgent.planRouteToRoleZone();
			
			// Falls kein Weg gefunden (No_op), dann erkunde, bis du einen Weg findest
			if (massimAction.getSymbol().equals("No_op"))
			{
				massimAction = massimAgent.planScoutingAction();
			}
			
			// Wandle massimAction in eine Action für den Server
			Action action = getMoveAction(massimAction);
			
			if (action == null)
				return new Action("skip");
			
			return action;     	
        }
        // falls gerade an einer Task gearbeitet wird, bestimme die Aktion über folgende Methode
        // (wurde in MEthode ausgelagert, um diese if-else-Schleife nicht noch mehr auszublähen (Übersicht) 
        else if (hasTask())
        {
        	return workOnTask();
        }
        // scout, wenn sonst nichts geht.
        else
        {
        	// Erfrage dazu Aktion beim EfficientMassimAgent
        	MassimAction massimAction = massimAgent.planScoutingAction();
        	
        	// Wandle massimAction in eine Action für den Server
        	Action action = getMoveAction(massimAction);
        	
        	// Wenn keine Aktion gefunden (z.B. eingeschlossen in Höhle) dann skip
        	// TODO Der Agent könnte sich auch freibohren...
			if (action == null)
				return new Action("skip");
			
			return action;   
        }
    	
    }

    @Override
    public void handlePercept(Percept percept) {}

    @Override
    public void handleMessage(Percept message, String sender) {
    	
    	// Wird einem Agenten DontTouch mitgeteilt, führt er für einige Steps kein eigenes attach durch,
    	// um zu verhindern, dass mehrere Agenten nach demselben Block greifen.
    	if (message.getName().equals("DontTouch"))
    		dontTouch = 3;
    }

    @Override
    public Action step() {
        

        // zurücksetzen  der Werte, die jeden step neu gesetzt werden müssen.
        resetValues();
        // auslesen der Percepts vom Server
        readPercepts();
        


        return decideAction();
    }
}