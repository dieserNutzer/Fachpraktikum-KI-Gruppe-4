package massim.javaagents.agents;

import eis.iilang.*;
import massim.javaagents.MailService;

import java.util.List;

/**
 * A very basic agent.
 */
public class BasicAgent extends Team4Agent {

    private int lastID = -1;

    /**
     * Constructor.
     * @param name    the agent's name
     * @param mailbox the mail facility
     */
    public BasicAgent(String name, MailService mailbox) {
        super(name, mailbox);
        System.out.println("Basic Constr.");
    }

    @Override
    public void handlePercept(Percept percept) {}

    @Override
    public void handleMessage(Percept message, String sender) {}

    //@Override
    /*public Action step() {
    	System.out.println("BasicStep");
        List<Percept> percepts = getPercepts();
        for (Percept percept : percepts) {
            if (percept.getName().equals("actionID")) {
                Parameter param = percept.getParameters().get(0);
                if (param instanceof Numeral) {
                    int id = ((Numeral) param).getValue().intValue();
                    if (id > lastID) {
                        lastID = id;
                        return new Action("move", new Identifier("n"));
                    }
                }
            }
        }
        return null;
    }*/
}
