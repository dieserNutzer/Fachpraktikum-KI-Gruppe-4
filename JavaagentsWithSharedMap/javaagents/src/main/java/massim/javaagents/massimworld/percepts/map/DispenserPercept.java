package massim.javaagents.massimworld.percepts.map;

import eis.iilang.Percept;
import massim.javaagents.massimworld.game.Game;
import massim.javaagents.massimworld.map.MassimCell;
import massim.javaagents.massimworld.map.things.BlockType;
import massim.javaagents.massimworld.map.things.Dispenser;

public class DispenserPercept extends MapPercept {
    private final BlockType blockType;

    public DispenserPercept(Percept percept) {
        super(percept);
        this.blockType = BlockType.getByTypeName(readString(percept, 3));
    }


    @Override
    public void updateMassimCell(MassimCell cell) {
        if (cell.getLastUpdatedStep() < Game.getCurrentStep()) {
            cell.clear();
        }
        cell.setDispenser(new Dispenser(blockType));
    }

}
