package massim.javaagents.massimworld.actions;

import eis.iilang.Action;
import massim.javaagents.massimworld.Coordinates;

public abstract class MassimAction {

//    boolean wasSuccesful;

    public Coordinates getMoveOffset() {
        return Coordinates.ZERO;
    }
    public abstract Action createEisAction();
}
