package massim.javaagents.massimworld.actions;

import eis.iilang.Action;
import massim.javaagents.massimworld.map.Coordinates;

/**
 * The supertype of all actions generated by this javaagents implementation,
 * provides an abstract method for creating an eis.iilang.Action for the scheduler.
 */
public abstract class MassimAction {

    /**
     * Returns the move offset of an action in relative coordinates if the action was succesfull.
     * Differs only for the MoveAction from Coordinates.ZERO.
     * @return the relative shift of an agent by successful execution.
     */
    public Coordinates getMoveOffset() {
        return Coordinates.ZERO;
    }

    public abstract Action createEisAction();

}