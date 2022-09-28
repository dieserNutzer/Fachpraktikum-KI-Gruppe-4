package massim.javaagents.massimworld.percepts.game;

import eis.iilang.Percept;
import massim.javaagents.massimworld.game.Game;

public class SimStartPercept extends GamePercept {

    public SimStartPercept(Percept percept) {}

    @Override
    public void updateGame(Game game) {
        game.setSimStarted();
    }

}
