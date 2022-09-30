package massim.javaagents.massimworld.map.view;

import massim.javaagents.massimworld.agent.MassimTeam4Agent;
import massim.javaagents.massimworld.map.Coordinates;
import massim.javaagents.massimworld.map.MassimCell;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Describes the current view of a {@link MassimTeam4Agent} (with vision = 5) and
 * provides methods for testing if the non-empty intersection of two views is the same.
 * Used to identify an encountered previously unknown agent.
 */
public class MassimMapView {

    Map<Coordinates, MassimCell> massimCellsByRelativeCoordinates;

    public MassimMapView(Map<Coordinates, MassimCell> massimCellsByRelativeCoordinates) {
        this.massimCellsByRelativeCoordinates = massimCellsByRelativeCoordinates;
    }

    public MassimCell getByCoordinates(Coordinates coor) {
        return massimCellsByRelativeCoordinates.get(coor);
    }

    public Set<Coordinates> getCoordinates() {
        return massimCellsByRelativeCoordinates.keySet();
    }

    public boolean isSame(MassimMapView otherView) {
        Set<Coordinates> intersection = new HashSet<>(massimCellsByRelativeCoordinates.keySet());
        intersection.retainAll(otherView.getCoordinates());

         List<Coordinates> unident = massimCellsByRelativeCoordinates.keySet().stream()
                 .filter(coor -> intersection.contains(coor))
                 .filter(coor -> !massimCellsByRelativeCoordinates.get(coor).isSame(otherView.getByCoordinates(coor))).toList();

         return unident.size() < 3;
    }

    // method for debugging purpose
    public void printView(MassimTeam4Agent agent) {
        Map<Coordinates, MassimCell> m = massimCellsByRelativeCoordinates;
        System.out.println("################ " + agent.getName());
        System.out.println("     " + c(0,-5) + "      ");
        System.out.println("    " + c(-1,-4) + c(0,-4) + c(1,-4)+ "     ");
        System.out.println("   " + c(-2,-3) + c(-1,-3) + c(0,-3) + c(1,-3) + c(2,-3) +"     ");
        System.out.println("  " + c(-3,-2) + c(-2,-2) + c(-1,-2) + c(0,-2) + c(1,-2) + c(2,-2) +c(3,-2) + "   ");
        System.out.println(" " + c(-4,-1) + c(-3,-1) + c(-2,-1) + c(-1,-1) + c(0,-1) + c(1,-1) + c(2,-1) +c(3,-2) + c(4,-1) +"   ");
        System.out.println("" + c(-5,0) + c(-4,0) + c(-3,0) + c(-2,0) + c(-1,0) + c(0,0) + c(1,0) + c(2,0) +c(3,0) + c(4,0) + c(5,0) + "");
        System.out.println(" " + c(-4,1) + c(-3,1) + c(-2,1) + c(-1,1) + c(0,1) + c(1,1) + c(2,1) +c(3,1) + c(4,1) + " ");
        System.out.println("  " + c(-3,2) + c(-2,2) + c(-1,2) + c(0,2) + c(1,2) + c(2,2) +c(3,2) + "  ");
        System.out.println("   " + c(-2,3) + c(-1,3) + c(0,3) + c(1,3) + c(2,3) + "   ");
        System.out.println("    " + c(-1,4) + c(0,4) + c(1,4)  + "    ");
        System.out.println("     " + c(0,5) +  "  ");
        System.out.println("12345678901 ################");
    }

    // method for debugging purpose
    public String getViewString() {
        Map<Coordinates, MassimCell> m = massimCellsByRelativeCoordinates;
        return  "     " + c(0,-5) + "      " + "\n" +
                "    " + c(-1,-4) + c(0,-4) + c(1,-4)+ "     "+ "\n" +
                "   " + c(-2,-3) + c(-1,-3) + c(0,-3) + c(1,-3) + c(2,-3) +"     "+ "\n" +
                "  " + c(-3,-2) + c(-2,-2) + c(-1,-2) + c(0,-2) + c(1,-2) + c(2,-2) +c(3,-2) + "   "+ "\n" +
                " " + c(-4,-1) + c(-3,-1) + c(-2,-1) + c(-1,-1) + c(0,-1) + c(1,-1) + c(2,-1) +c(3,-2) + c(4,-1) +"   "+ "\n" +
                "" + c(-5,0) + c(-4,0) + c(-3,0) + c(-2,0) + c(-1,0) + c(0,0) + c(1,0) + c(2,0) +c(3,0) + c(4,0) + c(5,0) + ""+ "\n" +
                " " + c(-4,1) + c(-3,1) + c(-2,1) + c(-1,1) + c(0,1) + c(1,1) + c(2,1) +c(3,1) + c(4,1) + " "+ "\n" +
                "  " + c(-3,2) + c(-2,2) + c(-1,2) + c(0,2) + c(1,2) + c(2,2) +c(3,2) + "  "+ "\n" +
                "   " + c(-2,3) + c(-1,3) + c(0,3) + c(1,3) + c(2,3) + "   "+ "\n" +
                "    " + c(-1,4) + c(0,4) + c(1,4)  + "    "+ "\n" +
                "     " + c(0,5) +  "  "+ "\n";
    }

    private String c(int x, int y) {
        return massimCellsByRelativeCoordinates.get(Coordinates.of(x,y)).printThingOrZone();
    }

}
