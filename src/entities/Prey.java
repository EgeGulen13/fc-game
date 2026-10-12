package entities;

/**
 * Represents the "Prey" entity in the simulation.
 * <p>
 * Prey resides at the bottom of the animal food chain. They actively seek {@link Food} 
 * to gain score points while attempting to evade both {@link Predator} and {@link ApexPredator}.
 * They typically utilize the defensive abilitiy "Hop".
 * </p>
 */
public class Prey extends Animal implements Respawnable{
    
    /**
     * Constructs a new Prey with specified properties.
     * @param location  The starting location of the Prey.
     * @param imagePath The file path to the image of Prey.
     * @param name      The name of Prey.
     * @param era       The era that the Prey belongs to.
     */
    public Prey(Location location, String imagePath, String name, String era) {
        super(location, imagePath, name, era);
    }

    /**
     * Updates the Prey's location to the specified target.
     * Moves directly to the target coordinate determined by the GameEngine's AI logic.
	 * <p>
     * Note: Validity checks and collision handling are managed 
     * externally by the GameEngine before this method is invoked.
     * </p>
     * @param targetLoc The new coordinate that Prey moves.
     */
    @Override
    public void move(Location targetLoc) {
        this.location = targetLoc;
    }
}