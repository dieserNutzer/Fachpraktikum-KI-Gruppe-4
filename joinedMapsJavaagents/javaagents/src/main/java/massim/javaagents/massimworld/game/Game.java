package massim.javaagents.massimworld.game;

import massim.javaagents.massimworld.game.task.gametask.GameTask;
import massim.javaagents.massimworld.game.task.gametask.OneBlockTask;
import massim.javaagents.massimworld.percepts.MassimPercept;
import massim.javaagents.massimworld.percepts.game.GamePercept;

import java.util.List;

public class Game {
    private static final Game game = new Game();

    private int steps = 750;

    private int currentStep = 0;

    private int score = 0;

    private GameTasks tasks = new GameTasks();

    private Game() {
    }

    public static Game game() {
        return game;
    }

    public static int getCurrentStep() {
        return game().getStep();
    }

    public void updateGame(List<MassimPercept> massimPercepts) {
        massimPercepts.stream()
                .filter(mp -> mp instanceof GamePercept)
                .forEach(mp -> ((GamePercept) mp).updateGame(this));
    }


    public void addTask(GameTask task) {
        if (!tasks.getTasks().contains(task)) {
            tasks.addTask(task);
        }
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public void setCurrentStep(int currentStep) {
        this.currentStep = currentStep;
    }

    public int getStep() {
        return currentStep;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public GameTasks getTasks() {
        return tasks;
    }

    public List<OneBlockTask> getOneBlockTasks() {
        return tasks.getActiveTasks(currentStep).stream().filter(t -> t.getRequirements().size()==1).map(OneBlockTask::new).toList();
    }
}
