package massim.javaagents.massimworld.game;

import massim.javaagents.massimworld.game.role.GameRoles;
import massim.javaagents.massimworld.game.task.gametask.GameTask;
import massim.javaagents.massimworld.game.task.gametask.OneBlockTask;
import massim.javaagents.massimworld.game.task.gametask.TwoBlockTask;
import massim.javaagents.massimworld.percepts.MassimPercept;
import massim.javaagents.massimworld.percepts.game.GamePercept;

import java.util.List;

/**
 * Singleton class contains the game configuration and global game state,
 * e.g. the current simulation step and the list of currently active {@link GameTask}.
 */
public class Game {
    private static final Game game = new Game();

    private boolean  simStarted;

    private String teamName;

    private int gameSteps = 750;

    private int currentStep = 0;

    private int score = 0;

    private int teamSize = 10;

    private GameTasks tasks = new GameTasks();

    private GameRoles gameRoles = new GameRoles();

    private Game() {
    }

    public static Game game() {
        return game;
    }

    public static int getCurrentStep() {
        return game().getStep();
    }

    /**
     * Updates the game state with the massimPercepts of an agent, that applies to the game state
     * i.e. the ones of type {@link GamePercept}.
     * @param massimPercepts the percepts received by an agent
     */
    public void updateGame(List<MassimPercept> massimPercepts) {
        massimPercepts.stream()
                .filter(mp -> mp instanceof GamePercept)
                .forEach(mp -> mp.updateGame(this));
    }

    public void addTask(GameTask task) {
        if (!tasks.getTasks().contains(task)) {
            tasks.addTask(task);
        }
    }

    public int getGameSteps() {
        return gameSteps;
    }

    public void setGameSteps(int gameSteps) {
        this.gameSteps = gameSteps;
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

    public GameRoles getGameRoles() {
        return gameRoles;
    }

    public List<OneBlockTask> getOneBlockTasks() {
        return tasks.getActiveTasks(currentStep).stream().filter(t -> t.getRequirements().size()==1).map(OneBlockTask::new).toList();
    }

    public List<TwoBlockTask> getTwoBlockTasks() {
        return tasks.getActiveTasks(currentStep).stream().filter(t -> t.getRequirements().size()==2).map(TwoBlockTask::new).toList();
    }

    public void setTeamSize(int teamSize) {
    }

    public void setSimStarted() {
        simStarted = true;
    }

    public void setTeam(String team) {
    }
}
