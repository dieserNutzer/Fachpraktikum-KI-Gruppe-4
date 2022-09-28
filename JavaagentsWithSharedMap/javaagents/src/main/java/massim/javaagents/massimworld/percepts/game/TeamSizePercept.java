package massim.javaagents.massimworld.percepts.game;

import eis.iilang.Percept;
import massim.javaagents.massimworld.game.Game;

public class TeamSizePercept extends GamePercept {

    int teamSize;

    public TeamSizePercept(Percept percept) {
        teamSize = readInt(percept, 0);
    }

    @Override
    public void updateGame(Game game) {
        game.setTeamSize(teamSize);
    }

}
