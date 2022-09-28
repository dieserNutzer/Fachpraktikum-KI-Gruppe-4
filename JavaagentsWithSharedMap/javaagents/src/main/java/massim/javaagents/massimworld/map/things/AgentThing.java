package massim.javaagents.massimworld.map.things;

import java.util.Objects;

public class AgentThing extends Thing {
    private final String teamName;

    public AgentThing(String teamName) {
        this.teamName = teamName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AgentThing)) return false;
        AgentThing that = (AgentThing) o;
        return Objects.equals(teamName, that.teamName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamName);
    }
}
