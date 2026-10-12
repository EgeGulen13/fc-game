package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

/**
 * This class represents the abstract base class for all objects on the grid.
 * It provides location and image path for all entities.	
 */
public abstract class Entity {
	protected Location location;
	protected String imagePath;
	
	/**
     * Constructs a new Entity with a specified location and image path.
     * @param location  The coordinate of the Entity on the grid.
     * @param imagePath The relative path to the image asset.
     */
    public Entity(Location location, String imagePath) {
        this.location = location;
        this.imagePath = imagePath;
    }
    
    /**
     * Draws the entity on the grid.
     * @param g         The Graphics context used for drawing.
     * @param xOffset   The horizontal start pixel of the grid.
     * @param yOffset   The vertical start pixel of the grid.
     * @param cellSize  The size of one grid square in pixels.
     * @param img       The image object to be drawn.
     */
    public void draw(Graphics g, int xOffset, int yOffset, int cellSize, Image img) {
        if (this.location == null) return; 

        int x = xOffset + (this.location.getCol() * cellSize);
        int y = yOffset + (this.location.getRow() * cellSize);
        
        int padding = 2; 

        if (img != null) {
            g.drawImage(img, x + padding, y + padding, cellSize - 2 * padding, cellSize - 2 * padding, null);
           
        } else {
            drawFallback(g, x, y, cellSize, padding);
        }
    }
    
    /**
     * Helper method to draw a colored placeholder if the sprite image is missing.
     */
    private void drawFallback(Graphics g, int x, int y, int cellSize, int padding) {
        Color color = Color.GRAY;
        
        if (this instanceof Prey) color = Color.GREEN;
        else if (this instanceof Predator) color = Color.RED;
        else if (this instanceof ApexPredator) color = Color.BLACK;
        else if (this instanceof Food) color = Color.YELLOW;
        
        g.setColor(color);
        g.fillRect(x + padding, y + padding, cellSize - 2 * padding, cellSize - 2 * padding);
    }
    
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getImagePath() {
        return imagePath;
    }
    
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
