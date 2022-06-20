package massim.javaagents.agents;
import java.text.FieldPosition;

enum FieldValues{
	free, entity, block, obstacle, dispencer
}
enum ZoneValues{
	free, goalZone, roleZone
}

public class State {
	public int IndexLastAction;
	float Heuristik;	// wird genutzt um die Heuristiken der anzahlAktions vielen Nachbarn des aktuellen States zu speichern.
	/**
	 * Spielfeld wird als Ausgangszustand im Zustandsraum genutzt zusammen mit Zones und connected
	 * free, entity, block, obstacle, dispencer
	 */
	FieldValues[][] Spielfeld;
	/**
	 * ZoneValues und FieldValues können sich überlagern, daher werden sie in unterschiedlichen Listen geführt.
	 * free, goalZone, roleZone
	 */
	ZoneValues[][] Zones;
	/**
	 * gibt an ob das Objekt auf Feld x,y connected ist. 
	 * False, True
	 */
	boolean[][] connected;
	/**
	 * gibt alle Felder an die durch die Aktionen attach und connect in den status connected wechseln können.
	 */
	boolean[][] connectable;
	/**
	 * Eine Liste der zur Verfügung stehenden Aktionen
	 * ich gehe davon aus, dass wir bisher immer noch nur ein Aktionsset haben daher 
	 */
	int XDim, YDim;
	/**
	 * Indices: 
	 * 0 = X,
	 * 1 = Y
	 */
	int[] Position;
	
	void setSize(int x, int y) {
		this.Spielfeld = new FieldValues[x][y];
		this.Zones = new ZoneValues[x][y];
		this.connected = new boolean[x][y];
	}
	
	void setPosition(int x, int y) {
		this.Position[0] = x;
		this.Position[1] = y;
	}
	
	// Ich gehe davon aus, dass Funktionen, die Daten manipulieren so nah wie möglich an diesen definiert sein sollten.
}
