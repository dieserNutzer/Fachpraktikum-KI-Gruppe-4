package massim.javaagents.massimworld.percepts.map;

import eis.iilang.Percept;
import massim.javaagents.massimworld.game.Game;
import massim.javaagents.massimworld.map.MassimCell;
import massim.javaagents.massimworld.map.things.Obstacle;

public class ObstaclePercept extends MapPercept {

    public ObstaclePercept(Percept percept) {
        super(percept);
    }

    @Override
    public void updateMassimCell(MassimCell cell) {
        if (cell.getLastUpdatedStep() < Game.getCurrentStep()) {
            cell.clear();
        }
        cell.setThing(Obstacle.obstacle());
    }

}
