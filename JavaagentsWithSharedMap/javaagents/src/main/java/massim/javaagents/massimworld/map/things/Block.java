package massim.javaagents.massimworld.map.things;

import java.util.Objects;

public class Block extends Thing {
    private final BlockType blockType;

    public Block(BlockType blockType) {
        this.blockType = blockType;
    }

    public BlockType getBlockType() {
        return blockType;
    }

    @Override
    public boolean isBlockOfType(BlockType blockType) {
        return this.blockType == blockType;
    }

    @Override
    public boolean isBlock() {
        return true;
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
