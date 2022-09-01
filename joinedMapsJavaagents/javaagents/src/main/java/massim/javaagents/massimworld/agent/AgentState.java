package massim.javaagents.massimworld.agent;


import massim.javaagents.massimworld.actions.MassimAction;
import massim.javaagents.massimworld.actions.MoveAction;
import massim.javaagents.massimworld.game.role.Role;
import massim.javaagents.massimworld.game.task.MassimTask;
import massim.javaagents.massimworld.map.Coordinates;
import massim.javaagents.massimworld.map.Direction;
import massim.javaagents.massimworld.percepts.MassimPercept;
import massim.javaagents.massimworld.percepts.agent.AgentPercept;

import java.util.ArrayList;
import java.util.List;

public class AgentState {

    private List<MassimAction> actionHistory;

    private MassimTask currentTask;

    private String lastAction;

    private String lastActionResult = "";

    private List<String> lastActionParams = new ArrayList<>();

    private boolean deactivated;

    Role role;

    int vision = 5;

    public void updateAgentState(List<MassimPercept> massimPercepts) {
        massimPercepts.stream()
                .filter(mp -> mp instanceof AgentPercept)
                .forEach(mp -> ((AgentPercept) mp).updateAgentState(this));
    }

    public List<MassimAction> getActionHistory() {
        return actionHistory;
    }

    public void setLastAction(String lastAction) {
        this.lastAction = lastAction;
    }

    public void setLastActionResult(String lastActionResult) {
        this.lastActionResult = lastActionResult;
    }

    public void setLastActionParams(List<String> lastActionParams) {
        this.lastActionParams = lastActionParams;
    }

    public Coordinates getLastActionOffset() {
        if (lastAction == null || !lastAction.equals("move") || !lastActionResult.equals("success")) {
            return Coordinates.ZERO;
        } else {
            MoveAction moveAction = new MoveAction(Direction.getBySymbol(lastActionParams.get(0)));
            return moveAction.getMoveOffset();
        }
    }

    public MassimTask getCurrentTask() {
        return currentTask;
    }

    public void setCurrentTask(MassimTask currentTask) {
        this.currentTask = currentTask;
    }

    public int getVision() {
        return vision;
    }

    public void setDeactivated(boolean deactivated) {
        this.deactivated = deactivated;
    }
}

