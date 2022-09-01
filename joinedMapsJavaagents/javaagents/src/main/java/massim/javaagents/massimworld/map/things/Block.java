package massim.javaagents.massimworld.map.things;

import java.util.Objects;

public class Block extends Thing {
    private final String blockType;

    public Block(String blockType) {
        this.blockType = blockType;
    }

    public String getBlockType() {
        return blockType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Block)) return false;
        Block block = (Block) o;
        return Objects.equals(blockType, block.blockType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(blockType);
    }
}
