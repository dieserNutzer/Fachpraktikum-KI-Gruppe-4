package massim.javaagents.massimworld.percepts.map;

import eis.iilang.Percept;
import massim.javaagents.massimworld.map.MassimCell;
import massim.javaagents.massimworld.map.things.Marker;
import massim.javaagents.massimworld.map.things.MarkerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MarkerPercept extends MapPercept {

    private final MarkerType markerType;

    private static final Logger LOG = LoggerFactory.getLogger(MarkerPercept.class);

    public MarkerPercept(Percept percept) {
        super(percept);
        markerType = MarkerType.getByTypeName(readString(percept, 3));

    }

    @Override
    public void updateMassimCell(MassimCell massimCell) {
        massimCell.setMarker(new Marker(markerType));
        LOG.warn("MarkerPrecept updateMassimCell cordinates {} type {} not implemented", coordinates, markerType);
    }
}
