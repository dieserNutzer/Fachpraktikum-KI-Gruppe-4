package massim.javaagents.massimworld.game.role;

/**
 * Describes the properties of the clear action.
 */
public class Clear {

    double chance;
    int maxDistance;

    public Clear(double chance, int maxDistance) {
        this.chance = chance;
        this.maxDistance = maxDistance;
    }
}
