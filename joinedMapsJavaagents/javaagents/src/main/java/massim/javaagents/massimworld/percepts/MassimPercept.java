package massim.javaagents.massimworld.percepts;

import eis.iilang.*;
import massim.javaagents.massimworld.game.Game;
import massim.javaagents.massimworld.map.MassimCell;

import java.util.List;

public abstract class MassimPercept {

    protected int readInt(Percept percept, int pos) {
        return ((Numeral) percept.getParameters().get(pos)).getValue().intValue();
    }

    protected int readInt(List<Parameter> params, int pos) {
        return ((Numeral) params.get(pos)).getValue().intValue();
    }

    protected String readString(Percept percept, int pos) {
        return ((Identifier)percept.getParameters().get(pos)).getValue();
    }

    protected String readString(List<Parameter> params, int pos) {
        return ((Identifier)params.get(pos)).getValue();
    }

    protected boolean readBoolean(Percept percept, int pos) {
        return ((Identifier) percept.getParameters().get(pos)).getValue().equals("true");
    }

    protected ParameterList readParameterList(Percept percept, int pos){
        return (ParameterList) percept.getParameters().get(pos);
    }

    public void updateGame(Game game) {}

    public abstract void updateMassimCell(MassimCell massimCell);
}
