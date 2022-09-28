package massim.javaagents.massimworld.percepts.agent;

import eis.iilang.Percept;
import massim.javaagents.massimworld.actions.ActionResult;
import massim.javaagents.massimworld.agent.AgentState;
import massim.javaagents.massimworld.agent.MassimTeam4Agent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LastActionResultPercept extends AgentPercept {

    private static final Logger LOG = LoggerFactory.getLogger(LastActionResultPercept.class);

    private ActionResult result;

    public LastActionResultPercept(Percept percept) {
        String resultString = readString(percept, 0);
        if (resultString == null || resultString == "") {
            result = null;
            LOG.info("empty or null result string received in LastActionResultPercept ");
        } else {
            try {
                result = ActionResult.byName(resultString);
            } catch (IllegalArgumentException e) {
                result = null;
                LOG.error("result {} string not found in ActionResult ", resultString);
            }
        }
    }

    @Override
    public void updateAgentState(AgentState agentState) {
        agentState.setLastActionResult(result);
    }
}
