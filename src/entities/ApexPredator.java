package entities;

/**
 * Represents the "Apex Predator" entity (e.g., Lion, T-Rex), the ruler of the grid.
 * <p>
 * Resides at the absolute top of the food chain. It actively hunts both {@link Prey} 
 * and {@link Predator}. Unlike other animals, it typically cannot be eaten and 
 * does not respawn. But it implements the respawnable because if it starts at the same location
 * with another entity, they must find another locaiton with "respawn".
 * </p>
 */
public class ApexPredator extends Animal implements Respawnable{

    /**
     * Constructs a new Apex Predator with specified properties.
     * @param location  The starting location of the Apex Predator on the grid.
     * @param imagePath The file path to the image of the Apex Predator.
     * @param name      The name of the Apex Predator.
     * @param era       The era the Apex Predator belongs to.
     */
    public ApexPredator(Location location, String imagePath, String name, String era) {
        super(location, imagePath, name, era);
    }

    /**
     * Updates the Apex Predator's position on the grid.
     * Moves directly to the target coordinate determined by the GameEngine's AI logic.
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