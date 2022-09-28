package massim.javaagents.massimworld.game.task.agenttask.subtask;

import massim.javaagents.massimworld.actions.MassimAction;
import massim.javaagents.massimworld.agent.MassimTeam4Agent;
import massim.javaagents.massimworld.game.task.TaskRequirement;

public class SupportCombineSubtask extends AgentSubtask {

    MassimTeam4Agent otherAgent;

    TaskRequirement leadRequirement;

    TaskRequirement supportRequirement;

    public SupportCombineSubtask(MassimTeam4Agent otherAgent, TaskRequirement leadRequirement, TaskRequirement supportRequirement) {
        super("supportCombineSubtask");
        this.otherAgent = otherAgent;
        this.leadRequirement = leadRequirement;
        this.supportRequirement = supportRequirement;
    }

    @Override
    public void update(MassimTeam4Agent agent) {

    }

    @Override
    public MassimAction getNextAction(MassimTeam4Agent agent) {
        return null;
    }
}
