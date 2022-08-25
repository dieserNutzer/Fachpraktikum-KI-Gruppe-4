package massim.javaagents.massimworld.percepts;

import eis.iilang.Identifier;
import eis.iilang.Percept;
import massim.javaagents.massimworld.percepts.agent.*;
import massim.javaagents.massimworld.percepts.game.StepPercept;
import massim.javaagents.massimworld.percepts.game.TaskPercept;
import massim.javaagents.massimworld.percepts.map.*;

import java.util.ArrayList;
import java.util.List;

public class MassimPerceptReader {

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
                        default ->
                                throw new IllegalArgumentException("unkknown thing percept with name " + thingPerceptType);
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
                    // Wenn ich es richtig herausgelesen habe, beschreibt ein role-Percept mit genau einem Parameter
                    // die aktuelle Rolle des Agenten
                    // Es gibt Role-Percepts mit mehr Parametern, diese beschreiben die Eigenschaften der zur
                    // Verfügung stehenden Rollen. // TODO Diese werden aber zurzeit nicht verarbeitet.
                    massimPercepts.add(new RolePercept(percept));
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
                default -> {
                }
//                    say("agent:" + name + " readPercepts: found unhandled percept " + percept);
            } // switch (percept.getName())

            // TODO Es gibt noch weitere Percepts

        } // for (Percept percept : percepts)
        return massimPercepts;
    }
}
