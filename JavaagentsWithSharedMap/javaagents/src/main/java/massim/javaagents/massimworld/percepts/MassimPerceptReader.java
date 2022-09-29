package massim.javaagents.massimworld.percepts;

import eis.iilang.Identifier;
import eis.iilang.Percept;
import massim.javaagents.massimworld.percepts.agent.*;
import massim.javaagents.massimworld.percepts.game.*;
import massim.javaagents.massimworld.percepts.map.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Reads eis.iilang.Percepts as received by the agent and returns MassimPercepts for further processing.
 * Plays the role of an input transformer to the classes of the package massimworld.
 */
public class MassimPerceptReader {

    private static final Logger LOG = LoggerFactory.getLogger(MassimPerceptReader.class);

    public static List<MassimPercept> readPercepts(List<Percept> eisPercepts) {

        List<MassimPercept> massimPercepts = new ArrayList<>();
        for (Percept percept : eisPercepts) {

            switch (percept.getName()) {
                case "thing" -> {

                    String thingPerceptType = ((Identifier) percept.getParameters().get(2)).getValue();

                    switch (thingPerceptType) {
                        case "dispenser" -> massimPercepts.add(new DispenserPercept(percept));
                        case "block" -> massimPercepts.add(new BlockPercept(percept));
                        case "obstacle" -> massimPercepts.add(new ObstaclePercept(percept));
                        case "entity" -> {
                            String teamName = ((Identifier) percept.getParameters().get(3)).getValue();
                            massimPercepts.add(new EntityPercept(percept));
                        }
                        case "marker" -> massimPercepts.add(new MarkerPercept(percept));
                        default -> LOG.error("unknown thing percept with name " + thingPerceptType);
                    }
                }

                case "role" -> {
                    if (percept.getParameters().size() == 1) {
                        massimPercepts.add(new AgentRolePercept(percept));
                    }
                }

                case "deactivated" -> massimPercepts.add(new DeactivatedPercept(percept));
                case "task" -> massimPercepts.add(new TaskPercept(percept));
                case "roleZone" -> massimPercepts.add(new RoleZonePercept(percept));
                case "goalZone" -> massimPercepts.add(new GoalZonePercept(percept));
                case "requestAction" -> massimPercepts.add(new RequestActionPercept());
                case "attached" -> massimPercepts.add(new AttachedPercept(percept));
                case "step" -> massimPercepts.add(new StepPercept(percept));
                case "lastAction" -> massimPercepts.add(new LastActionPercept(percept));
                case "lastActionParams" -> massimPercepts.add(new LastActionParamsPercept(percept));
                case "lastActionResult" -> massimPercepts.add(new LastActionResultPercept(percept));
                case "simStart" -> massimPercepts.add(new SimStartPercept(percept));
                case "deadline" -> massimPercepts.add(new DeadlinePercept(percept));
                case "actionID"-> massimPercepts.add(new ActionIdPercept(percept));
                case "timestamp"-> massimPercepts.add(new TimestampPercept(percept));
                case "team"-> massimPercepts.add(new TeamPercept(percept));
                case "steps"-> massimPercepts.add(new StepsPercept(percept));
                case "score"-> massimPercepts.add(new ScorePercept(percept));
                case "energy"-> massimPercepts.add(new EnergyPercept(percept));
                case "teamSize"-> massimPercepts.add(new TeamSizePercept(percept));
                case "name"-> massimPercepts.add(new AgentNamePercept(percept));
                default -> LOG.warn("unknown percept with name {}", percept.getName());
            }

        }
        return massimPercepts;
    }
}
