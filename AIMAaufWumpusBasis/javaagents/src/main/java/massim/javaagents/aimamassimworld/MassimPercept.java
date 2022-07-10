package massim.javaagents.aimamassimworld;

import aima.core.agent.Percept;
// Percepts, die vom EfficientMassimAgent für die Karte verarbeitet werden
// Hier wird noch zwischen obstacle und temporaryBlocked untgerschieden,
// was wahrscheinlich überflüssig ist, das auch obstacles entfernt / bewegt werden können
// TODO Es ist eigentlich Quatsch, dass bei den set-Methoden die Koordinate übergeben wird, die sollte beim anlegen des Objekts einmalig gesetzt werden
public class MassimPercept implements Percept {
	private boolean obstacle; // dienst der markierung von Feldern mit obstacles
	private boolean dispenser; // markiert ein Feld mit einem Dispenser und setzt dessen Typen
	private boolean temporaryBlocked; // Feld ist aktuell blockiert, z.B. durch entity (Agent) oder Block
	private boolean roleZoneCell; // Feld gehört zur role zone
	private boolean goalZoneCell; // Feld gehört zur goal zone
	private int x; // x-Koordinate
	private int y; // y-Koordinate
	String type; // wenn dispenser, dann markiert dies den Typen
	
	public void setObstacle(int x, int y)
	{
		obstacle = true;
		dispenser = false;
		temporaryBlocked = false;
		this.x = x;
		this.y = y;
	}
	
	public void walkable(int x, int y)
	{
		obstacle = false;
		dispenser = false;
		temporaryBlocked = false;
		this.x = x;
		this.y = y;
	}
	
	public void setDispenser(int x, int y, String type)
	{
		obstacle = true;
		dispenser = true;
		temporaryBlocked = false;
		this.x = x;
		this.y = y;
		this.type = type;
	}
	
	public void setEntity(int x, int y)
	{
		obstacle = false;
		dispenser = false;
		temporaryBlocked = true;
		this.x = x;
		this.y = y;
	}
	
	public void setRoleZone(int x, int y)
	{
		roleZoneCell = true;
		this.x = x;
		this.y = y;
	}
	
	public void setGoalZone(int x, int y)
	{
		goalZoneCell = true;
		this.x = x;
		this.y = y;
	}
	
	public boolean isObstacle()
	{
		return obstacle;
	}
	
	public boolean isDispenser()
	{
		return dispenser;
	}
	
	public boolean isTemporaryBlocked()
	{
		return temporaryBlocked;
	}
	
	public boolean isRoleZone()
	{
		return roleZoneCell;
	}
	
	public boolean isGoalZone()
	{
		return goalZoneCell;
	}
	
	public int getXValue()
	{
		return x;
	}
	
	public int getYValue()
	{
		return y;
	}
	
	public String getType()
	{
		return type;
	}
}
