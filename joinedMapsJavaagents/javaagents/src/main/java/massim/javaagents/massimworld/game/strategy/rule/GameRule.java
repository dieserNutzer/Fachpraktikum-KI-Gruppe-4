package massim.javaagents.massimworld.game.strategy.rule;

import massim.javaagents.massimworld.map.MassimMap;

public abstract class GameRule {

    protected int priority;

    protected GameRule(int priority) {
        this.priority = priority;
    }

    public abstract boolean applies(MassimMap map);

    public int getPriority() {
        return priority;
    }
}
