package massim.javaagents.agents;

import java.util.Iterator;

public class Edge {
	/**
	 * die Variablen "von" und "nach" geben die Richtung, also welcher Stat aus welchem generiert wurde.
	 */
	Vertex von, nach; 
	/**
	 *  durch die Anwendung von "aktion" und dessen Effekt wurde der State in "nach.state" berechnet aus dem State in "von.state".
	 */
	Aktion aktion; 
	void changeVerrices(Vertex von, Vertex nach) {
		this.von = von;
		this.nach = nach;
	}
	void setAction(Aktion chosenAktion) {
		this.aktion = chosenAktion;	
	}
}

class Aktion{		
	
}

/**
 * @author Nutzer
 *
 */
class skip extends Aktion implements Iterator<FieldValues[][]>{				
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

class attach extends Aktion implements Iterator<boolean[][]>{
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
