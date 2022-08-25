package massim.javaagents.massimworld.agent;

import eis.iilang.Action;
import eis.iilang.Identifier;
import eis.iilang.Percept;
import massim.javaagents.MailService;
import massim.javaagents.agents.Agent;
import massim.javaagents.aimamassimworld.MassimAction;
import massim.javaagents.massimworld.AgentGroupCoordinator;
import massim.javaagents.massimworld.Coordinates;
import massim.javaagents.massimworld.actions.MoveAction;
import massim.javaagents.massimworld.game.Game;
import massim.javaagents.massimworld.game.task.TaskPlanner;
import massim.javaagents.massimworld.game.task.subtask.Subtask;
import massim.javaagents.massimworld.map.Direction;
import massim.javaagents.massimworld.map.MassimMap;
import massim.javaagents.massimworld.percepts.MassimPercept;
import massim.javaagents.massimworld.percepts.MassimPerceptReader;
import massim.javaagents.massimworld.percepts.map.EmptyCellPercept;
import massim.javaagents.massimworld.percepts.map.MapPercept;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MassimTeam4Agent extends Agent {

    private static final Logger LOG = LoggerFactory.getLogger(MassimTeam4Agent.class);

    private AgentState agentState = new AgentState();

    private MassimMap massimMap = new MassimMap(this);

    private TaskPlanner taskPlanner = new TaskPlanner(this);

    private AgentGroupCoordinator agentGroupCoordinator = AgentGroupCoordinator.getAgentGroupCoordinator();


    public MassimTeam4Agent(String name, MailService mailbox) {
        super(name, mailbox);
    }

    private List<MassimPercept> readPerceptsFromPerception() {
        return MassimPerceptReader.readPercepts(getPercepts());
    }


    public void updateStateByPerception() {
        List< MassimPercept > massimPercepts = readPerceptsFromPerception();

        LOG.info("agent {} read {} perceptions", name, massimPercepts.size());

        // complete perception by adding empty percepts for every unpercepted cell withuin view
        massimPercepts = fillViewWithEmptyMapPercepts(massimPercepts);

        // update game by GamePercepts
        Game.game().updateGame(massimPercepts);

        // update agentState by AgentPercepts
        agentState.updateAgentState(massimPercepts);

        // Sende die von PErcepts erfassten Daten nur an den EfficientMassimAgent, wenn
        // ein request-Action-Percept vorlag. In den anderen STeps kommen auch keine
        // Infos über Felder, und es würden zu viele Felder zu unrecht als begehbar
        // eingestuft.
//        if (agentState.isRequestAction())
        agentGroupCoordinator.tellPerception(this, massimPercepts.stream().filter(mp -> mp instanceof MapPercept).map(mp -> (MapPercept) mp).collect(Collectors.toList()));
//
//         if (taskPlanner.hasNewTask()) {
             agentState.setCurrentTask(taskPlanner.getTaskByAgent(this));
//        }
    }

    @Override
    public Action step() {



        //getNextSubtask().getAction();

        MoveAction moveAction = new MoveAction(Direction.getRandomDirection());
        Action action = moveAction.createEisAction();

        return action;
    }

    public int getVision() {
        return 5;
    }


    public boolean hasTask() {
        return (agentState.getCurrentTask() != null);
    }

    public MassimMap getMap() {
        return massimMap;
    }

    public void setMap(MassimMap map) {
        this.massimMap = map;
    }

    public TaskPlanner getTaskPlanner() {
        return taskPlanner;
    }

    public void setTaskPlanner(TaskPlanner taskPlanner) {
        this.taskPlanner = taskPlanner;
    }

    public AgentState getAgentState() {
        return agentState;
    }

    public void setAgentState(AgentState agentState) {
        this.agentState = agentState;
    }

    protected Action getMoveAction(MassimAction ma) {
        Action retVal = null;

        if (ma == null) {
            return null;
        }

        if (ma.getSymbol().equals("West")) {
            retVal = new Action("move", new Identifier("w"));
        }
        if (ma.getSymbol().equals("North")) {
            retVal = new Action("move", new Identifier("n"));
        }
        if (ma.getSymbol().equals("South")) {
            retVal = new Action("move", new Identifier("s"));
        }
        if (ma.getSymbol().equals("East")) {
            retVal = new Action("move", new Identifier("e"));
        }
        return retVal;
    }


    private List<MassimPercept> fillViewWithEmptyMapPercepts(List<MassimPercept> massimPercepts) {
        // add empty map percepts for coordinates without perception
        Set<Coordinates> perceptedCoordinates = massimPercepts.stream()
                        .filter(mp -> mp instanceof MapPercept)
                        .map(mapPercept -> ((MapPercept) mapPercept).getCoordinates())
                        .collect(Collectors.toSet());
        Set<Coordinates> coordinatesInVision = Coordinates.getAllRelativeCoordinatesWithinDistance(agentState.getVision());
        coordinatesInVision.forEach(coordinates -> {
            if (!perceptedCoordinates.contains(coordinates)) {
                massimPercepts.add(new EmptyCellPercept(coordinates));
            }
        });
        return massimPercepts;
    }


    @Override
    public void handlePercept(Percept percept) {
    }

    @Override
    public void handleMessage(Percept message, String sender) {
    }

    private Subtask getNextSubtask() {
        return null;
    }


}