package massim.javaagents.massimworld;

import massim.javaagents.massimworld.agent.MassimTeam4Agent;
import massim.javaagents.massimworld.map.MassimMap;
import massim.javaagents.massimworld.map.MassimMapView;

import java.util.HashMap;
import java.util.Map;

public class AgentEncounter {

    String agentName;

    MassimTeam4Agent agent;

    Coordinates otherAgentRelativePosition;

    Map<String, Coordinates> coordinatesByAgentName = new HashMap<>();

    public AgentEncounter(MassimTeam4Agent agent) {
        this.agent = agent;
    }

    public AgentEncounter(MassimTeam4Agent agent, Coordinates otherAgentRelativePosition) {
        this.agent = agent;
        this.otherAgentRelativePosition = otherAgentRelativePosition;
    }

    public MassimTeam4Agent getAgent() {
        return agent;
    }


    public Coordinates getOtherAgentRelativePosition() {
        return otherAgentRelativePosition;
    }

    public void setOtherAgentWithCoordinates(String agentName, Coordinates coor) {
        coordinatesByAgentName.put(agentName, coor);
    }

    public boolean isWithUnknownAgent() {
        MassimMap agentMap = agent.getMap();
        Coordinates agentPosition = agentMap.getAgentPositionByAgent(agent);
        return agentMap.isUnknownAgent(agentPosition.withOffset(otherAgentRelativePosition));
    }

    // TODO remove when not needed anymore
//    public boolean isMatching(AgentEncounter otherEncounter){
//        boolean matches = otherAgentRelativePosition.isInverse(otherEncounter.getOtherAgentRelativePosition());
//        if (!matches) {
//            return false;
//        }
//        MassimMapView view = agent.getMap().getCurrentViewWithRelativeCoordinatesAndOffset(agent, Coordinates.ZERO);
//        view.printView();
//        MassimTeam4Agent otherAgent = otherEncounter.getAgent();
//        MassimMapView otherView = otherAgent.getMap().getCurrentViewWithRelativeCoordinatesAndOffset(otherAgent, otherAgentRelativePosition);
//        otherAgent.getMap().getCurrentViewWithRelativeCoordinatesAndOffset(otherAgent, Coordinates.ZERO).printView();
//        if (view.isSame(otherView)) {
//            return true;
//        } else {
//            return false;
//        }
//    }

    public boolean agentsHaveInverseRelativePositions(AgentEncounter otherEncounter)  {
        return otherAgentRelativePosition.isInverse(otherEncounter.getOtherAgentRelativePosition());
    }

    public boolean
    viewIntersectionIsEqual(AgentEncounter otherEncounter) {
        MassimMapView view = agent.getMap().getCurrentViewWithRelativeCoordinatesAndOffset(agent, Coordinates.ZERO);
//        view.printView();
        MassimTeam4Agent otherAgent = otherEncounter.getAgent();
        MassimMapView otherView = otherAgent.getMap().getCurrentViewWithRelativeCoordinatesAndOffset(otherAgent, otherAgentRelativePosition);
//        otherAgent.getMap().getCurrentViewWithRelativeCoordinatesAndOffset(otherAgent, Coordinates.ZERO).printView();
        if (view.isSame(otherView)) {
            return true;
        } else {
            return false;
        }
    }



//
//    public boolean matchesByNecessaryCondition(AgentEncounter other) {
//        if (this.numOfOtherAgents() != other.numOfOtherAgents()) {
//            return false;
//        }
//
//        if ()
//
//        Coordinates otherAgentCoor = null;
//        for (Coordinates otherCoor: otherAgentPositions) {
//            for (Coordinates otherPosOfOther : other.getOtherAgentPositions()) {
//                if (otherCoor.isInvertedCoordiantes(otherPosOfOther)) {
//                    otherAgentCoor = otherCoor;
//                    coordinatesByAgentName.put(other.getAgentName(), otherAgentCoor);
//                    other.setOtherAgentWithCoordinates(agentName, otherPosOfOther);
//                }
//            }
//        }
//        if (otherAgentCoor == null) {
//            System.out.println("Encounter does not match " + agentName + " " + other.getAgentName());
//            return false;
//        }
//
//        for (Coordinates otherCoor: otherAgentPositions) {
//            if (otherCoor.equals(otherAgentCoor)) {
//                continue;
//            }
//            boolean foundCommonPos = false;
//            for (Coordinates otherPosOfOther : other.getOtherAgentPositions()) {
//                if(otherPosOfOther.equals(otherAgentCoor.getInvertedCoordinates())){
//                    continue;
//                }
//                if (Coordinates.haveZeroSum(otherAgentCoor, otherPosOfOther, otherCoor.getInvertedCoordinates())) {
//                    foundCommonPos = true;
//                    break;
//                }
//            }
//            if (foundCommonPos == false) {
//                System.out.println("Encounter does not match " + agentName + " " + other.getAgentName());
//                return false;
//            }
//        }
//        return true;
//    }
//
//    public static Set<AgentEncounter> findMatchingEncounters(Set<AgentEncounter> others) {
//
//    }

//    public boolean hasEncounter() {
//        return !otherAgentPositions.isEmpty();
//    }
//
//    public int numOfOtherAgents() {
//        return otherAgentPositions.size();
//    }
}
