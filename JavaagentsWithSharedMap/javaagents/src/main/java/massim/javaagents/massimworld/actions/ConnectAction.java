package massim.javaagents.massimworld.actions;

import eis.iilang.Action;
import eis.iilang.Numeral;
import massim.javaagents.massimworld.agent.MassimTeam4Agent;
import massim.javaagents.massimworld.map.Coordinates;

public class ConnectAction extends MassimAction{

    MassimTeam4Agent otherAgent;

    Coordinates relativeCoordinates;

    public ConnectAction(MassimTeam4Agent otherAgent, Coordinates relativeCoordinates) {
        this.otherAgent = otherAgent;
        this.relativeCoordinates = relativeCoordinates;
    }

    @Override
    public Action createEisAction() {
        return new Action("connect", new Numeral(relativeCoordinates.getX()), new Numeral(relativeCoordinates.getY()));
    }

    @Override
    public String toString() {
        return "ConnectAction{" +
                "otherAgent=" + otherAgent.getName() +
                ", relativeCoordinates=" + relativeCoordinates +
                '}';
    }
}
