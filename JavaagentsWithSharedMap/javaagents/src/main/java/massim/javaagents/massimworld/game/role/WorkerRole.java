package massim.javaagents.massimworld.game.role;

import massim.javaagents.massimworld.actions.ActionType;

import java.util.List;

import static massim.javaagents.massimworld.actions.ActionType.*;

public class WorkerRole extends DefaultRole {

    protected RoleType roleType = RoleType.WORKER;

    public WorkerRole(RoleType roleType, int vision, List<ActionType> actions, List<Integer> speed, Clear clear) {
        super(roleType, vision, actions, speed, clear);
    }

    public WorkerRole() {
        super(RoleType.WORKER,
                5,
                List.of(ROTATE, REQUEST, DISCONNECT, MOVE, SUBMIT, ADOPT, CLEAR, DETACH, SKIP, ATTACH, CONNECT),
                List.of(2, 1, 1, 0),
                new Clear(0.3, 1)
        );
    }
}
