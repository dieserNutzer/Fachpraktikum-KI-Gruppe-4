package massim.javaagents.massimworld.percepts.agent;

import eis.iilang.Identifier;
import eis.iilang.ParameterList;
import eis.iilang.Percept;
import massim.javaagents.massimworld.agent.AgentState;

import java.util.ArrayList;
import java.util.List;

public class LastActionParamsPercept extends AgentPercept {

    private final List<String> parameters;

    public LastActionParamsPercept(Percept percept) {
        parameters = readParameters(percept);
    }

    @Override
    public void updateAgentState(AgentState agentState) {
        agentState.setLastActionParams(parameters);
    }

    List<String> readParameters(Percept percept) {
        List<String> params = new ArrayList<>();
//        if ()
        ParameterList pl = ((ParameterList) percept.getParameters().get(0));
        if (pl.size() > 0)
        {
            String lastActionPara = ((Identifier) pl.get(0)).getValue();
            params.add(lastActionPara);
        }
        return params;
    }
}
