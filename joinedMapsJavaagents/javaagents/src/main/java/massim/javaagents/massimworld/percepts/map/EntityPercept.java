package massim.javaagents.massimworld.percepts.map;

import eis.iilang.Percept;
import massim.javaagents.massimworld.map.MassimCell;
import massim.javaagents.massimworld.map.things.AgentThing;

public class EntityPercept extends MapPercept {
    String teamName;

    public EntityPercept(Percept percept) {
        super(percept);
        this.teamName = readString(percept, 3);
    }

    @Override
    public void updateMassimCell(MassimCell massimCell) {
        massimCell.setThing(new AgentThing(teamName));
    }

    public boolean isOfTeam(String teamName) {
        return this.teamName.equals(teamName);
    }
}
