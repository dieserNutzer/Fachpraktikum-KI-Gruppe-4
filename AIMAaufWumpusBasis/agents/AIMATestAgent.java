package massim.javaagents.agents;

import eis.iilang.*;
import massim.javaagents.MailService;
import massim.javaagents.aimamassimworld.MassimAction;
import massim.javaagents.aimamassimworld.EfficientMassimAgent;
import massim.javaagents.aimamassimworld.MassimPercept;
import java.util.List;
import java.util.ArrayList;


/**
 * Test.
 */
public class AIMATestAgent extends Agent {

    private int lastID = -1;
    private EfficientMassimAgent massimAgent;

    /**
     * Constructor.
     * @param name    the agent's name
     * @param mailbox the mail facility
     */
    public AIMATestAgent(String name, MailService mailbox) {
        super(name, mailbox);
        massimAgent = new EfficientMassimAgent();
    }

    @Override
    public void handlePercept(Percept percept) {}

    @Override
    public void handleMessage(Percept message, String sender) {}

    @Override
    public Action step() {
        List<Percept> percepts = getPercepts();
        
        List<MassimPercept> list = new ArrayList<>();
        MassimPercept mps = new MassimPercept();
        mps.setOKToMove(0, -1);
        list.add(mps);
        MassimPercept mpn = new MassimPercept();
        mpn.setOKToMove(0, 1);
        list.add(mpn);
        MassimPercept mpe = new MassimPercept();
        mpe.setOKToMove(1, 0);
        list.add(mpe);
        MassimPercept mpw = new MassimPercept();
        mpw.setOKToMove(-1, 0);
        list.add(mpw);
        
        int x;
        int y;
        int i = 0;
        
        for (Percept percept : percepts) {
        	
        	
        	if (percept.getName().equals("thing"))
        	{
        		x = ((Numeral) percept.getParameters().get(0)).getValue().intValue();
        		y = ((Numeral) percept.getParameters().get(1)).getValue().intValue();
        		
        		if (x == 0)
        		{
        			if (y == -1)
        			{
        				mps.setObstacle(x, y);
        			}
        			else if (y == 1)
        			{
        				mpn.setObstacle(x, y);
        			}
        		}
        		else if (y == 0)
        		{
        			if (x == -1)
        			{
        				mpw.setObstacle(x, y);
        			}
        			else if (x == 1)
        			{
        				mpe.setObstacle(x, y);
        			}
        		}
        	}
        	
        	

        }
        MassimAction action = massimAgent.planScoutingAction(list);
        
        if (action.getSymbol().equals("West"))
        {
        	System.out.println(getName() + ": Sende Westen");
        	return new Action("move", new Identifier("w"));
        }
        if (action.getSymbol().equals("North"))
        {
        	System.out.println(getName() + ": Sende Norden");
        	return new Action("move", new Identifier("n"));
        }
        if (action.getSymbol().equals("South"))
        {
        	System.out.println(getName() + ": Sende Sueden");
        	return new Action("move", new Identifier("s"));
        }
        if (action.getSymbol().equals("East"))
        {
        	System.out.println(getName() + ": Sende Osten");
        	return new Action("move", new Identifier("e"));
        }
        System.out.println(getName() + ": Sende Nix");
        return null;
    }
}
