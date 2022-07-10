package massim.javaagents.aimamassimworld;

import java.util.List;
import java.util.ArrayList;

// Wurde eingeführt, um die Nachbarfelder eines Dispensers zu bestimmen.
// Für die Wegfindung sollen die Nachbarfelder eines Dispensers als mögliches  Ziel gelten
// und nicht der Dispenser selbst (request und attach wird von Nachbarfeldern ausgeführt.)
public class MassimDispenserCell extends MassimCell {
	private String type;


	public MassimDispenserCell(int x, int y, String type) {
		super(x,y);
		this.type = type;
	}
	
	public List<MassimDispenserCell> getNeighbours() {
		List<MassimDispenserCell> retVal = new ArrayList<>();
		
		retVal.add(new MassimDispenserCell(x+1, y, type));
		retVal.add(new MassimDispenserCell(x-1, y, type));
		retVal.add(new MassimDispenserCell(x, y+1, type));
		retVal.add(new MassimDispenserCell(x, y-1, type));
		
		return retVal;
	}
	
	public String getType() {
		return type;
	}

	@Override
	public boolean equals(Object o) {
		if (o != null && o instanceof MassimDispenserCell) {
			MassimDispenserCell r = (MassimDispenserCell) o;
			return x == r.x && y == r.y && type.equals(r.type);
		}
		return false;
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 37 * result + getX();
		result = 43 * result + getY();
		return result;
	}
}
