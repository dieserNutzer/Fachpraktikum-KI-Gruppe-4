package massim.javaagents.massimworld.percepts.game;

import eis.iilang.*;
import massim.javaagents.massimworld.Coordinates;
import massim.javaagents.massimworld.game.Game;
import massim.javaagents.massimworld.game.task.MassimTask;
import massim.javaagents.massimworld.game.task.TaskRequirement;

import java.util.ArrayList;
import java.util.List;

public class TaskPercept extends GamePercept {

    private final String name;
    private final int deadline;
    private final int reward;
    private final List<TaskRequirement> requirements;

    public TaskPercept(Percept percept) {
        this.name = readString(percept, 0);
        this.deadline = readInt(percept, 1);
        this.reward = readInt(percept, 2);
        this.requirements = readRequirements(readParameterList(percept, 3));
    }

    @Override
    public void updateGame(Game game) {
        game.addTask(new MassimTask(name, deadline, reward, requirements));
    }

    private List<TaskRequirement> readRequirements(ParameterList requirementParams) {

        List<TaskRequirement> requirements = new ArrayList<>();
        for (int i = 0; i < requirementParams.size(); i++) {
            Function f = (Function) requirementParams.get(i);
            List<Parameter> params = f.getParameters();

            int x = readInt(params, 0);
            int y = readInt(params, 1);
            String blockType = readString(params, 2);

            requirements.add(new TaskRequirement(Coordinates.of(x, y), blockType));
        }
        return requirements;

    }


    public String getName() {
        return name;
    }

    public int getDeadline() {
        return deadline;
    }

    public int getReward() {
        return reward;
    }

    public List<TaskRequirement> getRequirements() {
        return requirements;
    }
}
