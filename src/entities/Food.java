package entities;

/**
 * This class represents the food resources in the simulation. 
 * This Food entities are primary target for Prey entity.
 * Food doesn't move and when eaten, respawns at a random location.
 */
public class Food extends Entity implements Respawnable{
	
	/**
	 * Constructs new Food object with specified Location object and specified image path.
	 * @param location 	Location of the food.
	 * @param imagePath The file path of the image of the food.
	 */
	public Food(Location location, String imagePath) {
		super(location, imagePath);
	}
}
