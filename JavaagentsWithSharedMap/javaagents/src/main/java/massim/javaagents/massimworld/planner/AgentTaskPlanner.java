package massim.javaagents.massimworld.planner;

import massim.javaagents.massimworld.actions.MassimAction;
import massim.javaagents.massimworld.agent.MassimTeam4Agent;
import massim.javaagents.massimworld.game.task.MassimTask;

public class AgentTaskPlanner {

    MassimTeam4Agent agent;

    public AgentTaskPlanner(MassimTeam4Agent agent) {
        this.agent = agent;
    }


    public void update(MassimTask massimTask) {
        massimTask.update(agent);
    }

    private MassimAction getNextAction(MassimTask massimTask) {
        MassimAction massimAction = massimTask.getNextAction(agent);


        return massimAction;

    }

}
