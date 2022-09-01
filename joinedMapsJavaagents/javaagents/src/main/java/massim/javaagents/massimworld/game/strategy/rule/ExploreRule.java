package massim.javaagents.massimworld.game.strategy.rule;

import massim.javaagents.massimworld.map.MassimMap;

public class ExploreRule extends GameRule {

    public ExploreRule() {
        super(100);
    }

    @Override
    public boolean applies(MassimMap map) {
        return !map.containsGoalZone();
    }
}
