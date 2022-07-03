package massim.javaagents.aimamassimworld;

import aima.core.agent.Percept;

public class MassimPercept implements Percept {
	private boolean obstacle;
	private boolean dispenser;
	private boolean temporaryBlocked;
	private boolean roleZoneCell;
	private boolean goalZoneCell;
	private int x;
	private int y;
	String type;
	
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
