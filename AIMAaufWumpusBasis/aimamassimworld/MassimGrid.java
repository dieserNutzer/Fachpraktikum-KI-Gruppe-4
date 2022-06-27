package massim.javaagents.aimamassimworld;

import java.util.*;


/**
 * Artificial Intelligence A Modern Approach (3rd Edition): page 236.<br>
 * <br>
 * The <b>wumpus world</b> is a cave consisting of rooms connected by
 * passageways. The rooms are always organized into a grid. See Figure 7.2 for
 * an example.
 *
 */
public class MassimGrid {

	private int gridXDimension;
	private int gridYDimension;

	private Set<MassimCell> allowedCells;
	

	/**
	 * Create a grid of rooms of dimensions x and y, representing the wumpus's cave.
	 * 
	 * @param caveXDimension
	 *            the cave's x dimension.
	 * @param caveYDimension
	 *            the cave's y dimension.
	 */
	public MassimGrid(int xDimension, int yDimension) {
		if (xDimension < 1)
			throw new IllegalArgumentException("Cave must have x dimension >= 1");
		if (yDimension < 1)
			throw new IllegalArgumentException("Case must have y dimension >= 1");
		this.gridXDimension = xDimension;
		this.gridYDimension = yDimension;
		allowedCells = getAllCells();
	}



	/**
	 * Limits possible movement within the cave (for search).
	 * @param allowedCells
	 *            the set of legal rooms that can be reached within the cave.
	 */
	public MassimGrid setAllowed(Set<MassimCell> allowedCells) {
		this.allowedCells.clear();
		this.allowedCells.addAll(allowedCells);
		return this;
	}


	
	public AgentPosition moveNorth(AgentPosition position) {
		int x = position.getX();
		int y = position.getY();
		y++;
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
		y--;
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
/*
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (int y = caveYDimension; y >= 1; y--) {
			for (int x = 1; x <= caveXDimension; x++) {
				Room r = new Room(x, y);
				String txt = "";
				if (r.equals(start.getRoom()))
					txt += "S";
				if (r.equals(gold))
					txt += "G";
				if (r.equals(wumpus))
					txt += "W";
				if (isPit(r))
					txt += "P";

				if (txt.isEmpty())
					txt = ". ";
				else if (txt.length() == 1)
					txt += " ";
				else if ( txt.length() > 2) // cannot represent...
					txt = txt.substring(0, 2);
				builder.append(txt);
			}
			builder.append("\n");
		}
		return builder.toString();
	}*/
}
