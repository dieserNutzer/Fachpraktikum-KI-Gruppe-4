package massim.javaagents.massimworld.map.things;

public abstract class Thing {

    protected Thing() {
    }

    public boolean isObstacle() {
        return false;
    }

    public boolean isBlockOfType(BlockType blockType) {
        return false;
    }

    public boolean isBlock() {
        return false;
    }
}
