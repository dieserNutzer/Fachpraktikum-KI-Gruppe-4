package massim.javaagents.massimworld.map.things;

import java.util.Objects;

public class Dispenser extends Thing {
    private final String blockType;

    public Dispenser(String blockType) {
        this.blockType = blockType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Dispenser)) return false;
        Dispenser dispenser = (Dispenser) o;
        return Objects.equals(blockType, dispenser.blockType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(blockType);
    }
}
