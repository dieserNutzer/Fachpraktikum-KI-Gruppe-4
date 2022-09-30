package massim.javaagents.massimworld.agent;

import massim.javaagents.massimworld.actions.ActionResult;
import massim.javaagents.massimworld.actions.ActionType;
import massim.javaagents.massimworld.actions.MoveAction;
import massim.javaagents.massimworld.game.Game;
import massim.javaagents.massimworld.game.role.Role;
import massim.javaagents.massimworld.game.role.RoleType;
import massim.javaagents.massimworld.game.task.MassimTask;
import massim.javaagents.massimworld.map.Coordinates;
import massim.javaagents.massimworld.map.Direction;
import massim.javaagents.massimworld.map.things.*;
import massim.javaagents.massimworld.percepts.MassimPercept;
import massim.javaagents.massimworld.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * The agent state percepted by the agent as result of his perception processing.
 * Only valid for one simulation step, recreated by the perceptions occuring at next perception time.
 */
public class AgentState {

    private static final Logger LOG = LoggerFactory.getLogger(AgentState.class);

    private String agentName;

    private int energy;

    private MassimTask currentTask;

    private ActionType lastActionType;

    private ActionResult lastActionResult;

    private List<String> lastActionParams = new ArrayList<>();

    private boolean deactivated;

    private final List<Pair<Coordinates, Thing>> attachedThings = new ArrayList<>();

    Role role = Game.game().getGameRoles().getGameRoleByType(RoleType.DEFAULT);

    int vision = 5;

    public void updateAgentState(List<MassimPercept> massimPercepts) {
        massimPercepts.stream()
                .sorted(Comparator.comparing(MassimPercept::getProcessOrder))
                .forEach(mp -> mp.updateAgentState(this));
    }

    public void setLastActionType(ActionType lastActionType) {
        this.lastActionType = lastActionType;
    }

    public void setLastActionResult(ActionResult lastActionResult) {
        this.lastActionResult = lastActionResult;
    }

    public void setLastActionParams(List<String> lastActionParams) {
        this.lastActionParams = lastActionParams;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Coordinates getLastActionMoveOffset() {
        if (lastActionType != ActionType.MOVE || lastActionResult != ActionResult.SUCCESS) {
            return Coordinates.ZERO;
        } else {
            MoveAction moveAction = new MoveAction(Direction.getBySymbol(lastActionParams.get(0)));
            return moveAction.getMoveOffset();
        }
    }

    public MassimTask getCurrentTask() {
        return currentTask;
    }

    public void setCurrentTask(MassimTask currentTask) {
        this.currentTask = currentTask;
    }

    public int getVision() {
        return vision;
    }

    public void setDeactivated(boolean deactivated) {
        this.deactivated = deactivated;
    }

    public ActionType getLastActionType() {
        return lastActionType;
    }

    public ActionResult getLastActionResult() {
        return lastActionResult;
    }

    public List<String> getLastActionParams() {
        return lastActionParams;
    }

    public boolean isDeactivated() {
        return deactivated;
    }

    public Role getRole() {
        return role;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public void setActionId(long actionId) {
    }

    public void setDeadline(long deadline) {
    }

    public void setTimestamp(long timestamp) {
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public void setVision(int vision) {
        this.vision = vision;
    }

    public Pair<Coordinates, Block> getFirstAttachedBlock() {
        if (hasAttachedBlock()) {
            return attachedThings.stream()
                    .filter(pair -> pair.getSecond() instanceof Block)
                    .map(pair -> Pair.of(pair.getFirst(), (Block) pair.getSecond()))
                    .findFirst()
                    .orElse(null);
        } else {
            return null;
        }
    }

    public Pair<Coordinates, Thing> getFirstAttachedBlock(BlockType blockType) {
        if (hasAttachedBlock()) {
            return attachedThings.stream()
                    .filter(pair -> pair.getSecond() instanceof Block && ((Block) pair.getSecond()).getBlockType() == blockType)
                    .findFirst()
                    .orElse(null);
        } else {
            return null;
        }
    }

    public boolean hasAttachedBlock() {
        return !attachedThings.isEmpty() &&
                attachedThings.stream()
                .filter(pair -> pair.getSecond() instanceof Block)
                .findFirst().isPresent();
    }

    public boolean hasAttachedBlock(BlockType blockType) {
        return !attachedThings.isEmpty() &&
                attachedThings.stream()
                        .filter(pair -> pair.getSecond() instanceof Block && ((Block) pair.getSecond()).getBlockType() == blockType)
                        .findFirst().isPresent();
    }

    public void addAttachedCoordinates(Coordinates coordinates) {
        if (!Coordinates.hasNormOne(coordinates)) {
            LOG.error("add attached relative coordinates with norm different from 1: {}", coordinates);
            return;
        }
        attachedThings.add(Pair.of(coordinates, new UnknownThing()));
    }

    public void addThingIfAttached(Coordinates relativeCoordinates, Thing thing) {
        Pair<Coordinates, Thing> attachedThing = attachedThings.stream().filter(pair -> pair.getFirst().equals(relativeCoordinates)).findFirst().orElse(null);
        if (attachedThing != null) {
            if (attachedThing.getSecond() instanceof UnknownThing) {
                if (thing instanceof Block || thing instanceof AgentThing) {
                    attachedThing.setSecond(thing);
                }
            } else {
                LOG.error("addThingIfAttached:  add attached thing for already known thing " + attachedThing.getSecond().toString());
            }
        }
    }

    public boolean detach(Coordinates coordinates) {
        return attachedThings.remove(Pair.of(coordinates, null));
    }

}

