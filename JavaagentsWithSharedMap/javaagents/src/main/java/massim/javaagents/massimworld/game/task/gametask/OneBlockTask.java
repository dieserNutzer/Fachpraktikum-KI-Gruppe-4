package massim.javaagents.massimworld.game.task.gametask;

import massim.javaagents.massimworld.actions.MassimAction;
import massim.javaagents.massimworld.actions.NoActionAction;
import massim.javaagents.massimworld.agent.MassimTeam4Agent;
import massim.javaagents.massimworld.game.Game;
import massim.javaagents.massimworld.game.task.MassimTask;
import massim.javaagents.massimworld.game.task.TaskRequirement;
import massim.javaagents.massimworld.game.task.agenttask.AgentTask;
import massim.javaagents.massimworld.map.Coordinates;
import massim.javaagents.massimworld.map.things.BlockType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class OneBlockTask extends GameTask {

    private static final Logger LOG = LoggerFactory.getLogger(OneBlockTask.class);

    protected List<AgentTask> agentTasks = new ArrayList<>();

    public OneBlockTask(String name, int deadline, int reward, List<TaskRequirement> requirements) {
        super(name, deadline, reward, requirements, 1, 100);
    }

    public OneBlockTask(int deadline, int reward, List<TaskRequirement> requirements, List<AgentTask> agentTasks) {
        super("oneBlockTask", deadline, reward, requirements, 1, 100);
        this.agentTasks = agentTasks;
    }

    public OneBlockTask(GameTask gameTask) {
            super(gameTask.getName(), gameTask.deadline, gameTask.reward, gameTask.getRequirements(), 1, 100);
    }

    public OneBlockTask copy() {
        return new OneBlockTask(name, deadline, reward, requirements);
    }

    public BlockType getBlockType() {
        return getRequirements().get(0).getBlockType();
    }

    public Coordinates getRelativeBlockPosition() {
        return requirements.get(0).getRelPosition();
    }


    public void setAgentTasks(List<AgentTask> agentTasks) {
        this.agentTasks = agentTasks;
        LOG.info("OneBlockTask: setAgentTasks");
        agentTasks.forEach(task ->
                LOG.info("set {} {}", task.getName(), task.toString())
        );
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
        if (agentTasks != null && !agentTasks.isEmpty()) {
            return agentTasks.get(0).getNextAction(agent);
        } else {
            LOG.error("getNextAction for {} is Empty", agent.getName());
            return new NoActionAction();
        }
    }

    @Override
    public int getStepEstimation() {
        return agentTasks.stream().mapToInt(AgentTask::getStepEstimation).sum();
    }

    @Override
    public boolean isFeasible() {
        return Game.getCurrentStep() + getStepEstimation() + 5 < getDeadline();
    }
}
