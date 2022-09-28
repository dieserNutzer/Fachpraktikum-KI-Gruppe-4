package massim.javaagents.massimworld.percepts;

import eis.iilang.Identifier;
import eis.iilang.Percept;
import massim.javaagents.Scheduler;
import massim.javaagents.massimworld.agent.MassimTeam4Agent;
import massim.javaagents.massimworld.percepts.agent.*;
import massim.javaagents.massimworld.percepts.game.*;
import massim.javaagents.massimworld.percepts.map.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MassimPerceptReader {

    private static final Logger LOG = LoggerFactory.getLogger(MassimPerceptReader.class);

    public static List<MassimPercept> readPercepts(List<Percept> eisPercepts) {

        List<MassimPercept> massimPercepts = new ArrayList<>();
        // Gehe nacheinander alle Percepts durch...
        for (Percept percept : eisPercepts) {

            // Abfrage des Namen des Percepts (beschreibt die Art des Percepts, vgl. Massim-Doku)
            switch (percept.getName()) {
                case "thing" -> {

                    // Es gibt verschiedene Arten eines Things, dies ist in Parameter 2 definiert (Typ Identifier)
                    String thingPerceptType = ((Identifier) percept.getParameters().get(2)).getValue();

                    switch (thingPerceptType) {
                        case "dispenser" -> {
                            massimPercepts.add(new DispenserPercept(percept));
                        }
                        case "block" -> {
                            massimPercepts.add(new BlockPercept(percept));
                        }
                        case "obstacle" -> {
                            massimPercepts.add(new ObstaclePercept(percept));
                        }
                        case "entity" -> {
                            String teamName = ((Identifier) percept.getParameters().get(3)).getValue();
                            massimPercepts.add(new EntityPercept(percept));
                        }
                        case "marker" -> {
                            massimPercepts.add(new MarkerPercept(percept));
                        }
                        default -> LOG.error("unknown thing percept with name " + thingPerceptType);
                    }
                }


                // Percept teilt mit, ob Agent deactivated ist. Wird in entsprechendem Attribut gespeichert.
                case "deactivated" -> {
                    massimPercepts.add(new DeactivatedPercept(percept));
                }

                // Percepts sagt etwas über zur Verfügung stehende Task aus.
                case "task" -> {
                    massimPercepts.add(new TaskPercept(percept));
                }

                // Percept sagt etwas über Rollen...
                case "role" -> {
                    if (percept.getParameters().size() == 1) {
                        massimPercepts.add(new AgentRolePercept(percept));
                    }
//                    } else {
//                        massimPercepts.add(new RolePercept(percept));
//                    }
                }

                // Percept beschreibt ein Feld mit einer Role-Zone
                case "roleZone" -> {
                    massimPercepts.add(new RoleZonePercept(percept));
                }

                // Percept beschreibt ein Feld mit einer Goal-Zone
                case "goalZone" -> {
                    massimPercepts.add(new GoalZonePercept(percept));
                }

                // Percept besagt, dass eine Action im step erfragt wird (gibt es nicht bei sim-Start)
                case "requestAction" -> {
                    massimPercepts.add(new RequestActionPercept());
                }

                // Percepts beschreibt ein Ding, dass attached ist
                case "attached" -> {
                    massimPercepts.add(new AttachedPercept(percept));
                }
                case "step" -> {
                    massimPercepts.add(new StepPercept(percept));
                }

                // Welches war die zuletzt übergebene Aktion (z. B. move)
                case "lastAction" -> {
                    massimPercepts.add(new LastActionPercept(percept));
                }
                case "lastActionParams" -> {
                    massimPercepts.add(new LastActionParamsPercept(percept));
                }
                case "lastActionResult" -> {
                    massimPercepts.add(new LastActionResultPercept(percept));
                }
                case "simStart" -> {
                    massimPercepts.add(new SimStartPercept(percept));
                }
                case "deadline" -> {
                    massimPercepts.add(new DeadlinePercept(percept));
                }
                case "actionID"-> {
                    massimPercepts.add(new ActionIdPercept(percept));
                }
                case "timestamp"-> {
                    massimPercepts.add(new TimestampPercept(percept));
                }
                case "team"-> {
                    massimPercepts.add(new TeamPercept(percept));
                }
                case "steps"-> {
                    massimPercepts.add(new StepsPercept(percept));
                }
                case "score"-> {
                    massimPercepts.add(new ScorePercept(percept));
                }
                case "energy"-> {
                    massimPercepts.add(new EnergyPercept(percept));
                }
                case "teamSize"-> {
                    massimPercepts.add(new TeamSizePercept(percept));
                }
                case "name"-> {
                    massimPercepts.add(new AgentNamePercept(percept));
                }
                default -> {
                    LOG.warn("unknown percept with name {}", percept.getName());
                }
//                    say("agent:" + name + " readPercepts: found unhandled percept " + percept);
            } // switch (percept.getName())

            // TODO Es gibt noch weitere Percepts

        } // for (Percept percept : percepts)
        return massimPercepts;
    }
}
