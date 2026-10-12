package entities;

/**
 * This class represents a specific coordinate on the 2D game grid. It is used to track the position of the
 * all entities. Also, this class provides methods to calculate the distance and checks the equality	 
 */
public class Location {
	private int col;
	private int row;
	
	/**
	 * Constructs new Location object with the specified grid coordinates.
	 * @param col Is the x coordinate of the entity.
	 * @param row Is the y coordinate of the entity.
	 */
	public Location(int row, int col) {
		this.col = col;
		this.row = row;
	}
	
	/**
	 * This method calculates the shortest distance between this location and the target entities distance.
	 * @param entity The location of the other entity that we measure distance to.
	 * @return 		 The distance between two locations.
	 */
	public double distanceTo(Location entity) {
		int dX = this.col - entity.col;
		int dY = this.row - entity.row;	
		
		return Math.sqrt(dX * dX + dY * dY); 
	}
	
	/**
	 * Checks if the location of the is equal to another object.
	 * <p>
	 * 1.Checks if the objects are the same instance in memory.
	 * 2.Checks if the other object is null
	 * 3.Checks if the other object has a different class type. (Both need to be location class)
	 * </p>
	 * @param obj	The object to compare with this Location.
     * @return		Returns true if both objects are Locations and share the same coordinates, false otherwise. 
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Location other = (Location) obj;
		return col == other.col && row == other.row;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	
	
}
