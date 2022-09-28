package massim.javaagents.massimworld.percepts.game;

import eis.iilang.Percept;
import massim.javaagents.massimworld.game.Game;

public class StepsPercept extends GamePercept {

    int steps;

    public StepsPercept(Percept percept) {
        steps = readInt(percept, 0);
    }

    @Override
    public void updateGame(Game game) {
        game.setGameSteps(steps);
    }

}
