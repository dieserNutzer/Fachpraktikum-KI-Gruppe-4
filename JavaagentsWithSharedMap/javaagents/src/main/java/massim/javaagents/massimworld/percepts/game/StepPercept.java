package massim.javaagents.massimworld.percepts.game;

import eis.iilang.Percept;
import massim.javaagents.massimworld.game.Game;

public class StepPercept extends GamePercept {

    int step;

    public StepPercept(Percept percept) {
        step = readInt(percept, 0);
    }

    @Override
    public void updateGame(Game game) {
        game.setCurrentStep(step);
    }

}
