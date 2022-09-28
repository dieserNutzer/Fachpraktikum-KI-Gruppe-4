package massim.javaagents.massimworld.game.task.gametask;

import massim.javaagents.massimworld.actions.MassimAction;
import massim.javaagents.massimworld.agent.MassimTeam4Agent;
import massim.javaagents.massimworld.game.task.MassimTask;
import massim.javaagents.massimworld.game.task.TaskRequirement;
import massim.javaagents.massimworld.game.task.agenttask.AgentTask;
import massim.javaagents.massimworld.game.task.agenttask.MoveToDispenserTask;
import massim.javaagents.massimworld.game.task.agenttask.MoveToGoalZoneTask;
import massim.javaagents.massimworld.game.task.agenttask.subtask.RotateSubtask;
import massim.javaagents.massimworld.game.task.agenttask.subtask.SubmitSubtask;
import massim.javaagents.massimworld.map.Coordinates;
import massim.javaagents.massimworld.map.things.BlockType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TwoBlockTask extends GameTask {

    protected Map<MassimTeam4Agent, List<AgentTask>> agentTasksByAgent = new HashMap<>();

    public TwoBlockTask(String name, int deadline, int reward, List<TaskRequirement> requirements) {
        super(name, deadline, reward, requirements,2, 200);
    }

    public TwoBlockTask(GameTask gameTask) {
        super(gameTask.getName(), gameTask.deadline, gameTask.reward, gameTask.getRequirements(),2, 200 );
    }

    public TwoBlockTask copy() {
        return new TwoBlockTask(name, deadline, reward, requirements);
    }

    public BlockType getBlockType() {
        return getRequirements().get(0).getBlockType();
    }

    public Coordinates getRelativeBlockPosition() {
        return requirements.get(0).getRelPosition();
    }


    public void setAgentTasks(Map<MassimTeam4Agent, List<AgentTask>> agentTasksByAgent) {
        this.agentTasksByAgent = agentTasksByAgent;
    }


    @Override
    public void update(MassimTeam4Agent agent) {
        agentTasks.get(0).update(agent);
        if (agentTasks.get(0).isFinished(agent)) {
            agentTasks.remove(0);
            if (agentTasks.isEmpty()) {
                setFinished();
            }
        }
    }

    @Override
    public boolean hasSubtask() {
        return !agentTasks.isEmpty();
    }

    @Override
    public MassimTask getCurrentSubtask() {
        return agentTasks.get(0);
    }

    @Override
    public void replan(MassimTeam4Agent agent) {

    }

    @Override
    public MassimAction getNextAction(MassimTeam4Agent agent) {
        return agentTasks.get(0).getNextAction(agent);
    }

    @Override
    public int getStepEstimation() {
        return agentTasks.stream().mapToInt(AgentTask::getStepEstimation).sum();
    }
}
