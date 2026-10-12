package entities;

import java.security.SecureRandom;

/**
 * Defines the behavior for entities. After the entity being eaten, 
 * they can reappear in a new location.
 */
public interface Respawnable {
	
	public abstract void setLocation(Location location);
	
	/**
	 * This method respawns the entity in a new random location within grid boundaries.
	 * @param rows The total number of rows in the grid.
	 * @param cols The total number of columns in the grid.
	 */
	default void respawn(int rows, int cols) {
        SecureRandom rng = new SecureRandom();
        int r = rng.nextInt(rows);
        int c = rng.nextInt(cols);
        
        setLocation(new Location(r, c));
    }
}
