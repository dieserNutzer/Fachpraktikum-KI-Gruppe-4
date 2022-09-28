package massim.javaagents.massimworld.percepts.game;

import eis.iilang.Percept;
import massim.javaagents.massimworld.game.Game;

public class ScorePercept extends GamePercept {

    int score;

    public ScorePercept(Percept percept) {
        score = readInt(percept, 0);
    }

    @Override
    public void updateGame(Game game) {
        game.setScore(score);
    }

}
