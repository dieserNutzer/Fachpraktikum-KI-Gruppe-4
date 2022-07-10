package massim.javaagents.aimamassimworld;

import java.util.*;

// Beschreibt das Spielfeld
// Die move Funktionen beschreiben die Auswirkung auf die entsprechenden Agentenposition
// TODO Wenn die SpielWelt mal rund ist, müssen hier die Randbeschränkungen der Welt entfernt werden.  
public class MassimGrid {

	private int gridXDimension;
	private int gridYDimension;

	private Set<MassimCell> allowedCells;
	

	public MassimGrid(int xDimension, int yDimension) {
		if (xDimension < 1)
			throw new IllegalArgumentException("Cave must have x dimension >= 1");
		if (yDimension < 1)
			throw new IllegalArgumentException("Case must have y dimension >= 1");
		this.gridXDimension = xDimension;
		this.gridYDimension = yDimension;
		allowedCells = getAllCells();
	}


	public MassimGrid setAllowed(Set<MassimCell> allowedCells) {
		this.allowedCells.clear();
		this.allowedCells.addAll(allowedCells);
		return this;
	}


	
	public AgentPosition moveNorth(AgentPosition position) {
		int x = position.getX();
		int y = position.getY();
		y--; // korrekt. y negativ bedeutet oben!
		MassimCell mc = new MassimCell(x, y);
		return allowedCells.contains(mc) ? new AgentPosition(x, y) : position;
	}
	
	public AgentPosition moveWest(AgentPosition position) {
		int x = position.getX();
		int y = position.getY();
		x--;
		MassimCell mc = new MassimCell(x, y);
		return allowedCells.contains(mc) ? new AgentPosition(x, y) : position;
	}
	
	public AgentPosition moveEast(AgentPosition position) {
		int x = position.getX();
		int y = position.getY();
		x++;
		MassimCell mc = new MassimCell(x, y);
		return allowedCells.contains(mc) ? new AgentPosition(x, y) : position;
	}
	
	public AgentPosition moveSouth(AgentPosition position) {
		int x = position.getX();
		int y = position.getY();
		y++;  // korrekt. y positiv bedeutet unten!
		MassimCell mc = new MassimCell(x, y);
		return allowedCells.contains(mc) ? new AgentPosition(x, y) : position;
	}
	
	public Set<MassimCell> getAllCells() {
		Set<MassimCell> allowedCells = new HashSet<>();
		for (int x = 1; x <= gridXDimension; x++)
			for (int y = 1; y <= gridYDimension; y++)
				allowedCells.add(new MassimCell(x, y));
		return allowedCells;
	}
}
