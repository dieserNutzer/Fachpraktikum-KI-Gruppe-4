package massim.javaagents.massimworld.percepts.game;

import eis.iilang.Percept;
import massim.javaagents.massimworld.game.Game;

public class TeamPercept extends GamePercept {

    private final String team;

    public TeamPercept(Percept percept) {
        team = readString(percept, 0);
    }

    @Override
    public void updateGame(Game game) {
        game.setTeam(team);
    }

}
