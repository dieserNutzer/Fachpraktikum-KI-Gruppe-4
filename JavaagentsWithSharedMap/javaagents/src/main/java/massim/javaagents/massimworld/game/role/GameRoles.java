package massim.javaagents.massimworld.game.role;

import java.util.Set;

public class GameRoles {

    Set<Role> gameRoles;

    public GameRoles() {
        this.gameRoles = Set.of(new DefaultRole(), new WorkerRole());
    }

    public Role getGameRoleByType(RoleType roleType) {
        return gameRoles.stream().filter(r -> r.getRoleType().equals(roleType)).findFirst().orElse(null);
    }


}
