package massim.javaagents.massimworld.percepts.agent;

import eis.iilang.Percept;
import massim.javaagents.massimworld.agent.AgentState;

public class LastActionResultPercept extends AgentPercept {

    private final String result;

    public LastActionResultPercept(Percept percept) {
//        this.success = readString(percept, 0).equals("success") ? true : false;
        result = readString(percept, 0);
    }

    @Override
    public void updateAgentState(AgentState agentState) {
        agentState.setLastActionResult(result);
    }
}
