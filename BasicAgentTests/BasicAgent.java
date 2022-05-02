package massim.javaagents.agents;

import eis.iilang.*;
import massim.eismassim.Log;
import massim.javaagents.MailService;

import java.util.List;

/**
 * A very basic agent.
 */
public class BasicAgent extends Agent {

    private int lastID = -1;

    /**
     * Constructor.
     * @param name    the agent's name
     * @param mailbox the mail facility
     */
    public BasicAgent(String name, MailService mailbox) {
        super(name, mailbox);
    }

    @Override
    public void handlePercept(Percept percept) {}

    @Override
    public void handleMessage(Percept message, String sender) {}

    @Override
    public Action step() {
    	List<Percept> percepts = getPercepts();
        String name = this.getName();
        String tabs = new String();
        System.out.println("------NEUER AGENT-------");
        System.out.println(name);
        /*
        for (Percept percept : percepts) {
        	tabs += " ";
        	System.out.println("Percept-Name: " + percept.getName());
        	for(Parameter param : percept.getParameters()) {
        		System.out.println(name + ": " + "Parameter: " + tabs + param.toString());
        	}
        }*/
        System.out.println("Der gesamte Percept als String: ");
        System.out.println(percepts.toString());
        return new Action("move", new Identifier("n"));
    }
}
