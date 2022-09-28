package massim.javaagents.massimworld.percepts.map;

import eis.iilang.Percept;
import massim.javaagents.massimworld.map.MassimCell;

public class GoalZonePercept extends MapPercept {
    public GoalZonePercept(Percept percept) {
        super(percept);
    }

    @Override
    public void updateMassimCell(MassimCell massimCell) {
        massimCell.setGoalZone(true);
    }
}
