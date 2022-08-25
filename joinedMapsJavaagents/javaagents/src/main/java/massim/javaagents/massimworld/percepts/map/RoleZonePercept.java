package massim.javaagents.massimworld.percepts.map;

import eis.iilang.Percept;
import massim.javaagents.massimworld.map.MassimCell;

public class RoleZonePercept extends MapPercept {
    public RoleZonePercept(Percept percept) {
        super(percept);
    }

    @Override
    public void updateMassimCell(MassimCell massimCell) {
        massimCell.setRoleZone(true);
    }
}
