package massim.javaagents.massimworld.game.task;

import massim.javaagents.massimworld.map.Coordinates;
import massim.javaagents.massimworld.map.things.BlockType;

/**
 * Class dscribing a task requiremetn of a {@link MassimTask}.
 */
public class TaskRequirement {
    private final Coordinates relPosition;
    private final BlockType blockType;

    public TaskRequirement(Coordinates relPosition, BlockType blockType) {
        this.relPosition = relPosition;
        this.blockType = blockType;
    }

    public Coordinates getRelPosition() {
        return relPosition;
    }

    public BlockType getBlockType() {
        return blockType;
    }

}
