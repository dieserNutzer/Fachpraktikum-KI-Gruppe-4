package massim.javaagents.massimworld.game.role;

import massim.javaagents.massimworld.actions.ActionType;

import java.util.List;

import static massim.javaagents.massimworld.actions.ActionType.*;

public class DefaultRole extends Role {

    protected final RoleType roleType = RoleType.DEFAULT;

    public DefaultRole(RoleType roleType, int vision, List<ActionType> actions, List<Integer> speed, Clear clear) {
        super(roleType, vision, actions, speed, clear);
    }

    public DefaultRole() {
        super(RoleType.DEFAULT,
                5,
                List.of(ROTATE, MOVE, ADOPT, CLEAR, DETACH, SKIP),
                List.of(2,1,1,0),
                new Clear(0.3, 1)
        );
    }
}
