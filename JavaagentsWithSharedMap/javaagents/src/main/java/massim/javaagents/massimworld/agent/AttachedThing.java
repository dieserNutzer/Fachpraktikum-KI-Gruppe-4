package massim.javaagents.massimworld.agent;

import massim.javaagents.massimworld.actions.Rotation;
import massim.javaagents.massimworld.map.Coordinates;
import massim.javaagents.massimworld.map.things.Thing;

public class AttachedThing {

    Thing thing;
    Coordinates relativePosition;

    public AttachedThing(Thing thing, Coordinates relativePosition) {
        this.thing = thing;
        this.relativePosition = relativePosition;
    }

    public AttachedThing(Coordinates relativePosition) {
        this.relativePosition = relativePosition;
    }

    public void setThing(Thing thing) {
        this.thing = thing;
    }

    public AttachedThing rotate(Rotation rotation) {
        switch (rotation) {
            case CW ->  {
                return new AttachedThing(thing, Coordinates.of(-relativePosition.getY(), relativePosition.getX()));
            }
            case CCW -> {
                return new AttachedThing(thing, Coordinates.of(-relativePosition.getX(), relativePosition.getY()));
            }
            default -> {
                throw new IllegalArgumentException("Coordinates:rotate with unknown direction " + rotation);
            }
        }
    }
}
