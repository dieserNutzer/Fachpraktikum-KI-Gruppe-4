package massim.javaagents.agents;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import eis.iilang.Action;
import eis.iilang.Identifier;
import eis.iilang.Parameter;
import eis.iilang.Percept;
import massim.javaagents.MailService;


/*Da wir keinerlei FOL nutzen, müssen wir alle Preconditions und Effekte der Aktionsschematas in JAVA implementieren.
Dabei hatte ich die Idee die Aktionen als Iteratoren anzulegen, weil die Aktionsschematas oftmals Quantifizierungen für Parameter aufweisen. Die Iteratoren bekommen dazu alle Daten übergeben, welche die Kombinationen der Parameter definieren (vorerst nur Felder auf dem Spielfeld). 
Weil die Anwendung jeder Aktion auch einen neuen State erstellen werden die Aktionen auch als Generatoren ausgelegt, sodass sie für alle möglichen Parameter Kombinationen einen neuen State erstellen. 
Jeder dieser States ist ein Nachbarstate des aktuellen Beliefe-State. Jeder Nachbarstate bekommt einen heuristischen Wert zugewiesen. */

public class HybridAgent extends Agent{
	static int anzahlAktions = 12; // gerne nochmal checken.
	Random randomGenerator = new Random();	// generiert zufallszahlen
	State aktuellerBeliefeState;
	State aktuellerPercept;
	/**
	 * Eine Liste für die benachbarten Spielfelder die sich Anwendung der Aktionen aus dem aktuellen Spielfeld ergeben.
	 */
	State[] Nachbarn;
	/**
	 * Erster Index gibt den index der Aktion an.
	 * Zweiter Index gibt 
	 */
	State Goal;
	int viewDist;
	List<Iterator>  Aktions;
	
	HybridAgent(String name, MailService mailbox, int XDim, int YDim, int viewDist) {
		super(name, mailbox);
		aktuellerPercept.setSize(XDim, YDim);
		aktuellerPercept.setPosition(XDim/2, YDim/2);
		this.aktuellerBeliefeState.XDim = XDim; 
		this.aktuellerBeliefeState.YDim = YDim;
		this.viewDist = viewDist;
		this.aktuellerBeliefeState.Position = new int[2];
		this.aktuellerBeliefeState.Position[0] = XDim / 2;
		this.aktuellerBeliefeState.Position[1] = YDim / 2;
		int randomInt;
		for (int i = 0; i < XDim; i++) {	// wir initialisieren alle Werte zufällig, hier steckt also Optimierungspotential.
			this.aktuellerBeliefeState.Spielfeld[i] = new FieldValues[YDim];
			for (int j = 0; j < YDim; j++) {
				// hier würde es sich lohnen vorteilhaftere Randomzahlen zu erstellen.
				randomInt = randomGenerator.nextInt(0, FieldValues.values().length);
				this.aktuellerBeliefeState.Spielfeld[i][j] = FieldValues.values()[randomInt];
				randomInt = randomGenerator.nextInt(0, ZoneValues.values().length);
				this.aktuellerBeliefeState.Zones[i][j] = ZoneValues.values()[randomInt];
				this.aktuellerBeliefeState.connected[i][j] = false;
			}
		}
		// TODO Auto-generated constructor stub
	}

	void updateSpielfeld() {
		// wir gehen davon aus, dass im percept alle Dinge auf den Feldern im Sichtfeld 
		// angegeben sind.
		// diese angaben überschreiben den bisherigen Beliefe.
		State zonePercept = new State();	// laut stackoverflow ist der default Wert 0 (https://stackoverflow.com/questions/2154251/any-shortcut-to-initialize-all-array-elements-to-zero)
		zonePercept.setSize(viewDist,viewDist);
		List<Percept> percepts = getPercepts();
		Percept percept; 
    	List<Percept> nurDinge = new LinkedList<Percept>();
    	List<Percept> rest = new LinkedList<Percept>();
    	// wir teilen erstmal die Percepts in solche die Felder betreffen und den Rest.
        for (int i = 0; i < percepts.size();i++) {
        	percept = percepts.get(i);
        	String name = percept.getName();
        	// es gibt einige Aktionen deren Erfolg überprüft werden muss, um die Informationen zu aktualisieren, die nicht im Percept mitgeteilt werden.
        	// das sind Move, Attach, Detatch, Connect, Disconnect, 
        	if (percept.getName() == "") {
        		aktuellerBeliefeState.Position[0]+=1;
        	}
        	if (percept.getName() == "things" || percept.getName() == "goalZones" || 
        			percept.getName() == "roleZones") {
        		nurDinge.add(percept);
        	}
        	else {
        		rest.add(percept);
        	}
        }
        for (Percept p : nurDinge){
        	if (p.getName() == "thing") {
        		// wenn der Name des Percept thing ist, hat dieses 3 Parameter: x-,y-Koordinate und den Namen des Dinges in dieser Reihenfolge
        		// dieser Ansatz ist nahezu geraten, ich habe keine Ahnung wie genau die Informationen in Percepts addressiert werden.
        		List<Parameter> parameters = p.getParameters();
        		// das hier gibt einen Fehler ich hoffe jemand weiß wie parameter in int gecastet werden können
        		int x = parameters.get(0);
        		int y = parameters.get(1);
        		if (parameters.get(2).equals("entity"))
        			aktuellerBeliefeState.Spielfeld[aktuellerBeliefeState.Position[0]+x][aktuellerBeliefeState.Position[1]+y] = FieldValues.values()[1];
        		if (parameters.get(2).equals("block"))
        			aktuellerBeliefeState.Spielfeld[aktuellerBeliefeState.Position[0]+x][aktuellerBeliefeState.Position[1]+y] = FieldValues.values()[2];
        		if (parameters.get(2).equals("marker"))
        			aktuellerBeliefeState.Spielfeld[aktuellerBeliefeState.Position[0]+x][aktuellerBeliefeState.Position[1]+y] = FieldValues.values()[3];
        		if (parameters.get(2).equals("dispencer"))
        			aktuellerBeliefeState.Spielfeld[aktuellerBeliefeState.Position[0]+x][aktuellerBeliefeState.Position[1]+y] = FieldValues.values()[4];
        	}
        	else if (p.getName() == "goalZones") {
        		List<Parameter> parameters = p.getParameters();
        		int x = parameters.get(0);
        		int y = parameters.get(1);
        		aktuellerBeliefeState.Spielfeld[aktuellerBeliefeState.Position[0]+x][aktuellerBeliefeState.Position[1]+y] = FieldValues.values()[0];
        	}
        	else if (p.getName() == "roleZones") {
        		int x = parameters.get(0);
        		int y = parameters.get(1);
        		aktuellerBeliefeState.Spielfeld[aktuellerBeliefeState.Position[0]+x][aktuellerBeliefeState.Position[1]+y] = FieldValues.values()[1];
        	}
        }
	}
	
	void calculateNeigbours() {
		for (int i = 0; i < anzahlAktions; i++) {
			chooseAction(i);
			calculateHeuristics();
		}
	}
	
	/**
	 * @author Nutzer
	 *
	 */
	class skip implements Iterator<FieldValues[][]>{				
		@Override
		public boolean hasNext() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public FieldValues[][] next() {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	private class attach implements Iterator<boolean[][]>{
		// Ich habe hiervon einiges https://stackoverflow.com/questions/36065361/how-a-java-iterator-works-internally
		int cursorX;
		int cursorY;
		int lastRet = -1; // index of last element returned; -1 if no such
		State neighbourState;
		
		/**
		 * attach kann alle Felder adressieren auch solche die nicht im Sichtfeld sind.
		 * Daher muss jedes Feld in connectable[][] durchlaufen werden.
		 */
		attach(){
			neighbourState = new State();
			reset();
		}

		public void reset() {
			neighbourState.connectable = aktuellerBeliefeState.connectable.clone();
			neighbourState.connected = aktuellerBeliefeState.connected.clone();
			neighbourState.Position = aktuellerBeliefeState.Position.clone();
			neighbourState.Spielfeld = aktuellerBeliefeState.Spielfeld.clone();
			neighbourState.XDim = aktuellerBeliefeState.XDim;
			neighbourState.YDim = aktuellerBeliefeState.YDim;
			neighbourState.Zones = aktuellerBeliefeState.Zones.clone();
			cursorX = 0;
			cursorY = 0;
		}
		
		@Override
		public boolean hasNext() {
			return cursor < lastRet;
		}

		@Override
		public boolean[][] next() {
			return neighbourState.connected;
		}
	}	
	
	private class detach implements Iterator<boolean[][]>{
		
		@Override
		public boolean hasNext() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean[][] next() {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	void connect() {
		
	}
	
	void disconnect() {
		
	}
	
	void request() {
		
	}
	
	void submit() {
		
	}

	void clear() {
		
	}
	
	void adopt() {
		
	}
	
	void survey() {
		
	}
	
	void rotate() {
		
	}
	
	void move() {
		
	}
	
	void calculateHeuristicValues() {
		
	}
	
	@Override
	public void handlePercept(Percept percept) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Action step() {
    	updateSpielfeld();
    	calculateNachbarn();
        return new Action("move", new Identifier("n"));
    }
	
	@Override
	public void handleMessage(Percept message, String sender) {
		// TODO Auto-generated method stub
		
	} 
}
