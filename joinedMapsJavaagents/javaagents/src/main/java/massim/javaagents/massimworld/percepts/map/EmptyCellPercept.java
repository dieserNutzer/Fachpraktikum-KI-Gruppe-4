package massim.javaagents.massimworld.percepts.map;

import massim.javaagents.massimworld.Coordinates;
import massim.javaagents.massimworld.map.MassimCell;

public class EmptyCellPercept extends MapPercept {

    public EmptyCellPercept(Coordinates coordinates) {
        super(coordinates);
    }

    public void updateMassimCell(MassimCell massimCell) {
        massimCell.clear();
    }
}
