package massim.javaagents.massimworld.game.task;

import massim.javaagents.massimworld.actions.MassimAction;

import java.util.List;

public abstract class MassimTask {

    protected final String name;

    public MassimTask(String name) {
        this.name = name;

    }

    public String getName() {
        return name;
    }

    public abstract MassimAction getNextAction();

}
