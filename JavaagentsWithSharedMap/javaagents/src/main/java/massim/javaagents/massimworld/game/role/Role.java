package massim.javaagents.massimworld.game.role;

import massim.javaagents.massimworld.actions.ActionType;

import java.util.List;
import java.util.Objects;

/**
 * Contains the properties of a massim simulation role
 * as described in {@see https://github.com/agentcontest/massim_2022/blob/main/docs/scenario.md#percepts}
 */
public abstract class Role {

    protected RoleType roleType;

    protected int vision;

    protected List<ActionType> actions;

    protected List<Integer> speed;

    protected Clear clear;

    public Role(RoleType roleType, int vision, List<ActionType> actions, List<Integer> speed, Clear clear) {
        this.roleType = roleType;
        this.vision = vision;
        this.actions = actions;
        this.speed = speed;
        this.clear = clear;
    }

    public RoleType getRoleType() {
        return roleType;
    }

    public int getVision() {
        return vision;
    }

    public List<ActionType> getActions() {
        return actions;
    }

    public List<Integer> getSpeed() {
        return speed;
    }

    public Clear getClear() {
        return clear;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role)) return false;
        Role role = (Role) o;
        return roleType == role.roleType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleType);
    }
}
