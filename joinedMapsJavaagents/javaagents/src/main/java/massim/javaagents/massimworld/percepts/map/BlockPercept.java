package massim.javaagents.massimworld.percepts.map;

import eis.iilang.Identifier;
import eis.iilang.Percept;
import massim.javaagents.massimworld.map.MassimCell;

public class BlockPercept extends MapPercept {
    String blockType;

    public BlockPercept(Percept percept) {
        super(percept);
        this.blockType = ((Identifier) percept.getParameters().get(3)).getValue();
    }

    public String getBlockType() {
        return blockType;
    }

    @Override
    public void updateMassimCell(MassimCell massimCell) {

    }
}
