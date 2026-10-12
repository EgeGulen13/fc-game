package logic;

import entities.*;

/**
 * Configuration class responsible for setting up dynamic game rules and animal abilities.
 * <p>
 * Instead of hardcoding ability stats such as range, cooldown, type inside Animal classes,
 * this class injects the correct rules based on the selected Era.
 * </p>
 */
public class GameConfig {

    /**
     * Configures the specified animal with ability stats based on the current Era rules.
     * @param animal The animal instance to configure.
     * @param era    The current game era.
     */
    public static void configureAnimal(Animal animal, String era) {
        
        if (era.equals("Past")) {
            if (animal instanceof ApexPredator) {
                animal.setAbilityStats(2, 2, Animal.TYPE_LINEAR);
            } 
            else if (animal instanceof Predator) {
                animal.setAbilityStats(2, 2, Animal.TYPE_LINEAR);
            } 
            else if (animal instanceof Prey) {
                animal.setAbilityStats(1, 2, Animal.TYPE_ZIGZAG);
            }
        }
        
        else if (era.equals("Present")) {
            if (animal instanceof ApexPredator) {
                animal.setAbilityStats(3, 3, Animal.TYPE_RADIAL);
            } 
            else if (animal instanceof Predator) {
                animal.setAbilityStats(2, 0, Animal.TYPE_RADIAL);
            } 
            else if (animal instanceof Prey) { 
                animal.setAbilityStats(2, 3, Animal.TYPE_RADIAL);
            }
        }
        
        
        else if (era.equals("Future")) {
            if (animal instanceof ApexPredator) {
                animal.setAbilityStats(3, 3, Animal.TYPE_RADIAL);
            } 
            else if (animal instanceof Predator) {
                animal.setAbilityStats(2, 2, Animal.TYPE_RADIAL);
            } 
            else if (animal instanceof Prey) {
                animal.setAbilityStats(3, 2, Animal.TYPE_RADIAL);
            }
        }
    }
}