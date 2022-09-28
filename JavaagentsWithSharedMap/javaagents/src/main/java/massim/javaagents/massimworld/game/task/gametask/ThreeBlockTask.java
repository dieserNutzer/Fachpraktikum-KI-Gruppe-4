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
import java.util.List;

public class ThreeBlockTask extends GameTask {

    protected List<AgentTask> agentTasks = new ArrayList<>();

    public ThreeBlockTask(String name, int deadline, int reward, List<TaskRequirement> requirements) {
        super(name, deadline, reward, requirements, 3, 0);
    }

    public ThreeBlockTask(GameTask gameTask) {
        super(gameTask.getName(), gameTask.deadline, gameTask.reward, gameTask.getRequirements(), 3, 0);
    }

    public ThreeBlockTask(String name, int deadline, int reward, List<TaskRequirement> requirements, MoveToDispenserTask moveToDispenserTask, MoveToGoalZoneTask moveToGoalZoneSubtask) {
        super(name, deadline, reward, requirements, 3, 0);
//        AcquireBlockTask acquireBlockTask =
        agentTasks.add(moveToDispenserTask);
       // agentTasks.add(new RequestSubtask());
        //agentTasks.add(new AttachSubtask());
        agentTasks.add(moveToGoalZoneSubtask);
        agentTasks.add(new RotateSubtask());
        agentTasks.add(new SubmitSubtask(name));
    }

    public ThreeBlockTask copy() {
        return new ThreeBlockTask(name, deadline, reward, requirements);
    }



    public BlockType getBlockType() {
        return getRequirements().get(0).getBlockType();
    }

    public Coordinates getRelativeBlockPosition() {
        return requirements.get(0).getRelPosition();
    }


    public void setAgentTasks(List<AgentTask> agentTasks) {
        this.agentTasks = agentTasks;
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
