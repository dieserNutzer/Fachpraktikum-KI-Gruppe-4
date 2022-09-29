package massim.javaagents.massimworld.game.task.agenttask.composite;

import massim.javaagents.massimworld.agent.MassimTeam4Agent;
import massim.javaagents.massimworld.game.task.agenttask.AgentTask;
import massim.javaagents.massimworld.map.things.BlockType;

import java.util.List;

/**
 * Task to acquire a block of the given {@link BlockType}.
 */
public class AcquireBlockTask extends CompositeTask {

    private final BlockType blockType;

    public AcquireBlockTask(String name, BlockType blockType, List<AgentTask> agentTasks) {
        super(name, agentTasks);
        this.blockType = blockType;
        super.subtasks = agentTasks;
    }


    public BlockType getBlockType() {
        return blockType;
    }

    @Override
    public void replan(MassimTeam4Agent agent) {

    }

}
