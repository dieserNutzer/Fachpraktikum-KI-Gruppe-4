package massim.javaagents.massimworld.percepts.agent;

import eis.iilang.Percept;
import massim.javaagents.massimworld.actions.ActionResult;
import massim.javaagents.massimworld.actions.ActionType;
import massim.javaagents.massimworld.agent.AgentState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LastActionPercept extends AgentPercept {

    private static final Logger LOG = LoggerFactory.getLogger(LastActionPercept.class);

    private ActionType actionType;

    public LastActionPercept(Percept percept) {
        String resultString = readString(percept, 0);
        if (resultString == null || resultString == "") {
            actionType = null;
            LOG.info("empty or null result string received in LastActionPercept ");
        } else {
            try {
                actionType = ActionType.getByName(readString(percept, 0));
            } catch (IllegalArgumentException e) {
                actionType = null;
                LOG.error("result {} string not found in ActionType", resultString);
            }
        }
    }

    @Override
    public void updateAgentState(AgentState agentState) {
        agentState.setLastActionType(actionType);
    }
}
