package massim.javaagents.massimworld.actions;

import eis.iilang.Action;
import massim.javaagents.massimworld.map.Coordinates;

public abstract class MassimAction {

    public Coordinates getMoveOffset() {
        return Coordinates.ZERO;
    }

    public abstract Action createEisAction();

}