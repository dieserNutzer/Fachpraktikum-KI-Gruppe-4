package massim.javaagents.aimamassimworld;

/**
 * Representation of an Agent's [x,y] position within a Massim World.
 * 
 */
public class AgentPosition {


	private MassimCell mc;

	
	public AgentPosition(int x, int y) {
		this(new MassimCell(x, y));
	}
	
	public AgentPosition(MassimCell massimCell) {
		this.mc = massimCell;
	}
	
	public MassimCell getFieldElement() {
		return mc;
	}
	
	public int getX() {
		return mc.getX();
	}
	
	public int getY() {
		return mc.getY();
	}

	@Override
	public String toString() {
		return mc.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && getClass() == obj.getClass()) {
			AgentPosition other = (AgentPosition) obj;
			return (getX() == other.getX()) && (getY() == other.getY());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int result = 17;
		result = 37 * result + mc.hashCode();
		return result;
	}	
}
