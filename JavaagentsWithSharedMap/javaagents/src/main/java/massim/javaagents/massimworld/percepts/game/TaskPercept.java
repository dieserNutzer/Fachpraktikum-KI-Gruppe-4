package massim.javaagents.massimworld.percepts.game;

import eis.iilang.Function;
import eis.iilang.Parameter;
import eis.iilang.ParameterList;
import eis.iilang.Percept;
import massim.javaagents.massimworld.game.Game;
import massim.javaagents.massimworld.game.task.TaskRequirement;
import massim.javaagents.massimworld.game.task.gametask.GameTask;
import massim.javaagents.massimworld.game.task.gametask.OneBlockTask;
import massim.javaagents.massimworld.game.task.gametask.ThreeBlockTask;
import massim.javaagents.massimworld.game.task.gametask.TwoBlockTask;
import massim.javaagents.massimworld.map.Coordinates;
import massim.javaagents.massimworld.map.things.BlockType;

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
        GameTask newGameTask;
        switch (requirements.size()) {
            case 1 -> {
                newGameTask = new OneBlockTask(name, deadline, reward, requirements);
            }
            case 2 -> {
                newGameTask = new TwoBlockTask(name, deadline, reward, requirements);
            }
            case 3 -> {
                newGameTask = new ThreeBlockTask(name, deadline, reward, requirements);
            }
            default -> {
                throw new IllegalArgumentException("task with requirements size other than 1,2,3 fond: " + requirements.size());
            }
        }
        game.addTask(newGameTask);
    }

    private List<TaskRequirement> readRequirements(ParameterList requirementParams) {

        List<TaskRequirement> requirements = new ArrayList<>();
        for (int i = 0; i < requirementParams.size(); i++) {
            Function f = (Function) requirementParams.get(i);
            List<Parameter> params = f.getParameters();

            int x = readInt(params, 0);
            int y = readInt(params, 1);
            BlockType blockType = BlockType.getByTypeName(readString(params, 2));

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
