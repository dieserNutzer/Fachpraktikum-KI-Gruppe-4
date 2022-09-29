package massim.javaagents.massimworld.agent;

import eis.iilang.Action;
import eis.iilang.Percept;
import massim.javaagents.MailService;
import massim.javaagents.agents.Agent;
import massim.javaagents.massimworld.actions.*;
import massim.javaagents.massimworld.agentcoordinator.AgentGroupCoordinator;
import massim.javaagents.massimworld.game.Game;
import massim.javaagents.massimworld.game.role.RoleType;
import massim.javaagents.massimworld.game.task.MassimTask;
import massim.javaagents.massimworld.map.Coordinates;
import massim.javaagents.massimworld.map.Direction;
import massim.javaagents.massimworld.map.MassimCell;
import massim.javaagents.massimworld.map.MassimMap;
import massim.javaagents.massimworld.map.things.Block;
import massim.javaagents.massimworld.map.things.BlockType;
import massim.javaagents.massimworld.map.things.Thing;
import massim.javaagents.massimworld.map.view.MassimMapView;
import massim.javaagents.massimworld.percepts.MassimPercept;
import massim.javaagents.massimworld.percepts.MassimPerceptReader;
import massim.javaagents.massimworld.percepts.map.EmptyCellPercept;
import massim.javaagents.massimworld.percepts.map.MapPercept;
import massim.javaagents.massimworld.planner.AgentTaskPlanner;
import massim.javaagents.massimworld.planner.TaskPlanner;
import massim.javaagents.massimworld.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static massim.javaagents.massimworld.actions.ActionResult.SUCCESS;
import static massim.javaagents.massimworld.actions.ActionType.MOVE;
import static massim.javaagents.massimworld.actions.Rotation.CCW;
import static massim.javaagents.massimworld.actions.Rotation.CW;
import static massim.javaagents.massimworld.map.Direction.*;

/**
 * The agent implementation for this javaagent package.
 * Contains many helper methods to retrieve agent state and map states directly.
 */
public class MassimTeam4Agent extends Agent {

    private static final Logger LOG = LoggerFactory.getLogger(MassimTeam4Agent.class);

    private AgentState agentState = new AgentState();

    private AgentTaskPlanner agentTaskPlanner = new AgentTaskPlanner(this);

    private MassimMap massimMap = new MassimMap(this);

    private TaskPlanner taskPlanner = new TaskPlanner(this);

    boolean planTasks = false;

    boolean replanSubtask = false;

    MassimTask massimTask;

    public boolean isPlanTasks() {
        return planTasks;
    }

    private AgentGroupCoordinator agentGroupCoordinator = AgentGroupCoordinator.getAgentGroupCoordinator();


    public MassimTeam4Agent(String name, MailService mailbox) {
        super(name, mailbox);
    }


    public void updateStatesByPerception() {
        List< MassimPercept > massimPercepts = MassimPerceptReader.readPercepts(getPercepts());

        LOG.info("agent {} read {} perceptions", name, massimPercepts.size());

        // complete perception by adding empty percepts for every unpercepted cell within view
        massimPercepts = fillViewWithEmptyMapPercepts(massimPercepts);

        Game.game().updateGame(massimPercepts);

        createCurrentAgentState(massimPercepts);

        if (agentState.getLastActionResult() != null && ActionResult.errorResults().contains(agentState.getLastActionResult())) {
            LOG.error("agent {} got last action {} with parameter {} error {}", name, agentState.getLastActionType(), String.join(",", agentState.getLastActionParams()), agentState.getLastActionResult());
            MassimMapView view = getMap().getCurrentViewWithRelativeCoordinatesAndOffset(this, Coordinates.ZERO);
            view.printView(this);
        }

        agentTaskPlanner.update(getTaskPlanner().getTaskByAgent(this));

        boolean mapContainsGoalZone = false;
        if (massimMap.containsGoalZone()) {
            mapContainsGoalZone = true;
        }
//        if (agentState.isRequestAction())
        agentGroupCoordinator.tellPerception(this, massimPercepts.stream().filter(mp -> mp instanceof MapPercept).map(mp -> (MapPercept) mp).collect(Collectors.toList()));

        if (!mapContainsGoalZone && massimMap.containsGoalZone()) {
            planTasks = true;
        }
        //
//         if (taskPlanner.hasNewTask()) {
             agentState.setCurrentTask(taskPlanner.getTaskByAgent(this));
//        }
    }

    private void createCurrentAgentState(List<MassimPercept> massimPercepts) {
        agentState = new AgentState();
        agentState.updateAgentState(massimPercepts);
    }

    @Override
    public Action step() {

        MassimTask task = taskPlanner.getTaskByAgent(this);

        // TODO find right place
        //agentTaskPlanner.update(task);

//        if (task.isFinished(this)) {
//            taskPlanner.planTaskforAgents();
//        }

        MassimAction massimAction = task.getNextAction(this);
        LOG.info("agent {} {} got next action {} from Task {}",name, agentState.getRole().getRoleType(), massimAction.toString(), task.toString());
//        decideAction()
        if (massimAction instanceof MoveAction) {
            MoveAction moveAction = (MoveAction) massimAction;
            Coordinates targetCoordinates = getCurrentCoordinates().withOffset(moveAction.getMoveOffset());
            if (!massimMap.getMassimCell(targetCoordinates).isEmpty()) {
                Direction possibleDirection = massimMap.getDirectionFromCoordinatesTowardsDirection(getCurrentCoordinates(), moveAction.getDirection());
                if (possibleDirection != null) {
                    massimAction = new MoveAction(possibleDirection);
                    replanSubtask = true;
                }
            } else if (massimMap.getMassimCell(targetCoordinates).isEmpty() &&
                    agentState.hasAttachedBlock() &&
                    MOVE.equals(agentState.getLastActionType()) &&
                    !SUCCESS.equals(agentState.getLastActionResult())) {
                massimAction = computeRotateActionToGetBlockBehind(moveAction);
            } else {
                massimMap.applyMoveActionForAgent(moveAction, this);
            }
        }

        broadcast(new Percept("step " + Game.getCurrentStep() + "at agent " + name), name);

        Action action = massimAction.createEisAction();

        return action;

    }

    private RotateAction computeRotateActionToGetBlockBehind(MoveAction moveAction) {
        Direction moveDirection = moveAction.getDirection();
        Direction relativeBlockDirection = Direction.getByOffset(getAttachedBlockRelativePosition());
        RotateAction rotateAction = null;
        switch (moveDirection) {
            case EAST -> {
                if (relativeBlockDirection == NORTH) {
                    rotateAction = new RotateAction(CCW);
                }
            }
            case SOUTH -> {
                if (relativeBlockDirection == EAST) {
                    rotateAction =  new RotateAction(CCW);
                }
            }
            case WEST -> {
                if (relativeBlockDirection == SOUTH) {
                    rotateAction =  new RotateAction(CCW);
                }
            }
            case NORTH -> {
                if (relativeBlockDirection == WEST) {
                    rotateAction =  new RotateAction(CCW);
                }
            }
        };
        return rotateAction != null ? rotateAction : new RotateAction(CW);
    }

//    private Action decideAction(AgentTask agentTask) {
//        if (agentTask instanceof RequestSubtask) {
//            BlockType blockType = ((RequestSubtask) agentTask).getBlockType();
//            Direction direction =  massimMap.getAdjacentDispenserDirection(blockType);
//            return (new RequestAction(direction)).createEisAction();
//        }
//        if (agentTask instanceof RequestSubtask) {
//            BlockType blockType = ((RequestSubtask) agentTask).getBlockType();
//            Direction direction =  massimMap.getAdjacentDispenserDirection(blockType);
//            return (new AttachAction(direction)).createEisAction();
//        }
//        if (agentTask instanceof S) {
//            BlockType blockType = ((RequestSubtask) agentTask).getBlockType();
//            Direction direction =  massimMap.getAdjacentDispenserDirection(blockType);
//            return (new RequestAction(direction)).createEisAction();
//        }
//    }

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

    public MassimTask getCurrentTask() {
        return taskPlanner.getTaskByAgent(this);
    }

    public void setTaskPlanner(TaskPlanner taskPlanner) {
        this.taskPlanner = taskPlanner;
    }

    public AgentState getAgentState() {
        return agentState;
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
        //LOG.info("agent {} received message {} from {}", name, message.getName(), sender);
    }


    public MassimCell getAdjacentCell(Direction direction) {
        Coordinates current = getCurrentCoordinates();
        MassimCell cell = massimMap.getAdjacentCell(current, direction);
        return cell;
    }

    public Coordinates getCurrentCoordinates() {
        return massimMap.getAgentPositionByAgent(this);
    }


    public Block getAttachedBlock() {
        Pair<Coordinates, Block> attachedThing = agentState.getFirstAttachedBlock();
        if (attachedThing != null) {
            return attachedThing.getSecond();
        } else {
            LOG.error("agent {} getAttachedBlock: returned null", getName());
            return null;
        }
    }

    public Coordinates getAttachedBlockRelativePosition() {
        return agentState.getFirstAttachedBlock().getFirst();
    }

    public boolean hasAttachedBlock(BlockType blockType) {
        return agentState.hasAttachedBlock(blockType);
    }

    public boolean hasAttachedBlock() {
        return agentState.hasAttachedBlock();
    }

    public Pair<Coordinates, Block> getFirstAttachedBlock() {
        return agentState.getFirstAttachedBlock();
    }

    public Pair<Coordinates, Thing> getFirstAttachedBlock(BlockType blockType) {
        return agentState.getFirstAttachedBlock(blockType);
    }

    public RoleType getCurrentRoleType() {
        return getAgentState().getRole().getRoleType();
    }

    public ActionResult getLastActionResult() {
        return getAgentState().getLastActionResult();
    }

    public ActionType getLastActionType() {
        return getAgentState().getLastActionType();
    }

    public boolean isInGoalZone() {
        return massimMap.getMassimCell(massimMap.getAgentPositionByAgent(this)).isGoalZone();
    }

    @Override
    public String toString() {
        return "MassimTeam4Agent{" +
                "name='" + name + '\'' +
                '}';
    }

    public boolean isAdjacentToDispenser(BlockType blockType) {
        return massimMap.isAdjacentToDispenser(getCurrentCoordinates(), blockType);
    }
}
