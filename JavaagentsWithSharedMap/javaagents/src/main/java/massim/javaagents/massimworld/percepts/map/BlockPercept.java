package massim.javaagents.massimworld.percepts.map;

import eis.iilang.Identifier;
import eis.iilang.Percept;
import massim.javaagents.massimworld.agent.AgentState;
import massim.javaagents.massimworld.game.Game;
import massim.javaagents.massimworld.map.Coordinates;
import massim.javaagents.massimworld.map.MassimCell;
import massim.javaagents.massimworld.map.things.Block;
import massim.javaagents.massimworld.map.things.BlockType;

public class BlockPercept extends MapPercept {
    Coordinates coordinates;
    BlockType blockType;

    public BlockPercept(Percept percept) {
        super(percept);
        this.coordinates = Coordinates.of(readInt(percept,0), readInt(percept,1));
        this.blockType = BlockType.getByTypeName(readString(percept, 3));
    }

    public BlockType getBlockType() {
        return blockType;
    }

    @Override
    public void updateMassimCell(MassimCell massimCell) {
        if (massimCell.getLastUpdatedStep() < Game.getCurrentStep()) {
            massimCell.clear();
        }
        massimCell.setThing(new Block(blockType));
    }

    @Override
    public void updateAgentState(AgentState agentState) {
        agentState.addThingIfAttached(coordinates, new Block(blockType));
    }

}
