package massim.javaagents.massimworld.game.task;

import massim.javaagents.massimworld.Coordinates;

public class TaskRequirement {
    private final Coordinates relPosition;
    private final String blockType;

    public TaskRequirement(Coordinates relPosition, String blockType) {
        this.relPosition = relPosition;
        this.blockType = blockType;
    }

    public Coordinates getRelPosition() {
        return relPosition;
    }

    public String getBlockType() {
        return blockType;
    }

}
