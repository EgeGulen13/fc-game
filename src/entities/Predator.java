package entities;

/**
 * Represents the "Predator" animal in the simulation which the user controls.
 * <p>
 * Predators occupy the middle of the food chain: they hunt {@link Prey} 
 * to survive but must evade the {@link ApexPredator}.
 * They typically utilize the ability "Dash".
 * </p>
 */
public class Predator extends Animal implements Respawnable {
    
    /**
     * Constructs a new Predator with specified properties.
     * @param location  The starting location of the Predator on the grid.
     * @param imagePath The file path to the image of Predator.
     * @param name      The name of the predator.
     * @param era       The era the Apex Predator belongs to.
     */
    public Predator(Location location, String imagePath, String name, String era) {
        super(location, imagePath, name, era);
    }

    /**
     * Updates the Predator's position on the grid.
     * <p>
     * Note: Validity checks and collision handling are managed 
     * externally by the GameEngine before this method is invoked.
     * </p>
     * @param targetLoc The destination coordinates.
     */
    @Override
    public void move(Location targetLoc) {
        this.location = targetLoc;
    }
}