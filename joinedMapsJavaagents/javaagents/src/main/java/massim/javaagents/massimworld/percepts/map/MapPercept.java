package massim.javaagents.massimworld.percepts.map;

import eis.iilang.Numeral;
import eis.iilang.Percept;
import massim.javaagents.massimworld.Coordinates;
import massim.javaagents.massimworld.map.MassimCell;
import massim.javaagents.massimworld.percepts.MassimPercept;

public abstract class MapPercept extends MassimPercept {

	protected final Coordinates coordinates;

	protected MapPercept(Percept percept) {
		this.coordinates = readCoordinates(percept);
	}

	protected MapPercept(Coordinates coordinates) {
		this.coordinates = coordinates;
	}

	protected Coordinates readCoordinates(Percept percept) {
		int x = ((Numeral) percept.getParameters().get(0)).getValue().intValue();
		int y = ((Numeral) percept.getParameters().get(1)).getValue().intValue();
		return Coordinates.of(x, y);
	}

	public Coordinates getCoordinates() {
		return coordinates;
	}

	public abstract void updateMassimCell(MassimCell massimCell);

}

