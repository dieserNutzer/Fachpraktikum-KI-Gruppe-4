package massim.javaagents.massimworld.agent;

import massim.javaagents.massimworld.actions.Rotation;
import massim.javaagents.massimworld.map.Coordinates;
import massim.javaagents.massimworld.map.things.Block;

public class AttachedBlock {

    Block block;
    Coordinates relativePosition;

    public AttachedBlock(Block block, Coordinates relativePosition) {
        this.block = block;
        this.relativePosition = relativePosition;
    }

    public AttachedBlock rotate(Rotation rotation) {
        switch (rotation) {
            case CW ->  {
                return new AttachedBlock(block, Coordinates.of(-relativePosition.getY(), relativePosition.getX()));
            }
            case CCW -> {
                return new AttachedBlock(block, Coordinates.of(-relativePosition.getX(), relativePosition.getY()));
            }
            default -> {
                throw new IllegalArgumentException("Coordinates:rotate with unknown direction " + rotation);
            }
        }
    }
}
