package massim.javaagents.massimworld.map.things;

import java.util.Objects;

public class Marker extends Thing {
    private final MarkerType markerType;

    public Marker(MarkerType markerType) {
        this.markerType = markerType;
    }

    public MarkerType getMarkerType() {
        return markerType;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Marker)) return false;
        Marker dispenser = (Marker) o;
        return Objects.equals(markerType, dispenser.markerType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(markerType);
    }
}
