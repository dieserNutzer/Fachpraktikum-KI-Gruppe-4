package massim.javaagents.aimamassimworld;

import aima.core.agent.Percept;

public class MassimPercept implements Percept {
	private boolean obstacle;
	private int x;
	private int y;
	
	public void setObstacle(int x, int y)
	{
		obstacle = true;
		this.x = x;
		this.y = y;
	}
	
	public void setOKToMove(int x, int y)
	{
		obstacle = false;
		this.x = x;
		this.y = y;
	}
	
	public boolean isObstacle()
	{
		return obstacle;
	}
	
	public int getXValue()
	{
		return x;
	}
	
	public int getYValue()
	{
		return y;
	}
}
