package entities;

/**
 * Abstract base class for all living creatures in the simulation.
 * <p>
 * This class extends {@link Entity} and introduces dynamic stats such as score,
 * cooldown management, and ability configuration (range, type) that can change
 * based on the active Era.
 * </p>
 */
public abstract class Animal extends Entity {
    
    protected String name;
    protected String era;
    protected String abilityType;
    protected int score;
    protected int cooldown;
    protected int abilityRange;        
    protected int maxCooldown;    
    protected boolean isAlive;

    
    // Constant for Linear movement type
    public static final String TYPE_LINEAR = "LINEAR";
    // Constant for Radial movement type 
    public static final String TYPE_RADIAL = "RADIAL";
    // Constant for Zigzag movement type 
    public static final String TYPE_ZIGZAG = "ZIGZAG";
        
    /**
     * Constructs a new animal with specified properties.
     * @param location  The starting location on the grid.
     * @param imagePath The file path to the animal's sprite.
     * @param name      The display name of the animal.
     * @param era       The time period this animal belongs to.
     */
    public Animal(Location location, String imagePath, String name, String era) {
        super(location, imagePath);
        this.name = name;
        this.era = era;
        this.score = 0;
        this.cooldown = 0;
        this.isAlive = true;
    }
    
    /**
     * Abstract method to move the animal to a target location.
     * Concrete implementations handles specific collision or movement rules.
     * * @param targetLoc The destination location.
     */
    public abstract void move(Location targetLoc);
    
    /**
     * Activates the animal's special ability and resets the cooldown.
     * <p>
     * <b>Note:</b> The cooldown is set to {@code maxCooldown + 1} intentionally.
     * This compensates for the automatic cooldown decrement that occurs at the 
     * end of the turn in which the ability is used.
     * </p>
     */
    public void useAbility() {
        this.cooldown = this.maxCooldown + 1; 
    }
    
    /**
     * Decreases the cooldown timer by 1 turn.
     * This is typically called by the GameEngine at the end of each round.
     */
    public void decreaseCooldown() {
        if (this.cooldown > 0) {
            this.cooldown -= 1;
        }
    }
    
    /**
     * Checks if the special ability is ready to be used.
     * @return {@code true} if cooldown is 0.
     */
    public boolean isAbilityAvailable() {
        return cooldown == 0;
    }
    
    /**
     * Sets the ability rules dynamically based on the Era.
     * <p>This allows the same class (e.g., Predator) to behave differently in Past vs Future.</p>
     * * @param range       The range of the ability (e.g., 2, 3).
     * @param maxCooldown The cooldown duration between uses.
     * @param type        The movement pattern (TYPE_LINEAR, etc.).
     */
    public void setAbilityStats(int range, int maxCooldown, String type) {
        this.abilityRange = range;
        this.maxCooldown = maxCooldown;
        this.abilityType = type;
    }


    public int getScore() {
    	return score; 
    }

    public void addScore(int points) {
    	this.score += points;
    }

    public void decreaseScore(int points) {
    	this.score -= points;
    }
    
    public void setScore(int score) {
    	this.score = score;
    }

    public int getCooldown() {
    	return this.cooldown; 
    }
    
    public void setCooldown(int cooldown) {
    	this.cooldown = cooldown; 
    }
    
    public int getAbilityRange() {
    	return abilityRange;
    }
    
    public String getAbilityType() {
    	return abilityType;
    }
    
    public boolean isAlive() {
    	return isAlive; 
    }
    
    public void setAlive(boolean isAlive) {
    	this.isAlive = isAlive; 
    }
    
    public String getName() {
    	return name; 
    }
}