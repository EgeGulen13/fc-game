package io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import entities.Animal;
import entities.ApexPredator;
import entities.Entity;
import entities.Food;
import entities.Location;
import entities.Predator;
import entities.Prey;
import exceptions.FileOperationException;
import exceptions.InvalidGameData;
import logic.GameConfig;
import logic.LoadedGameFormat;

/**
 * Handles the persistence of the game state.
 * <p>
 * This utility class provides static methods to save the current game progress to a text file
 * and load it back later.
 * </p>
 */
public class SaveLoad {
    
	/**
	 * Saves the current game state to a text file.
	 * <p>
	 * The file format consists of:
	 * <ul>
	 * <li><b>Header:</b> Era, TurnCount, TotalRounds, Rows, Cols</li>
	 * <li><b>Entity Lines:</b> Type, Row, Col, Score, Cooldown, ImagePath</li>
	 * </ul>
	 * </p>
	 * @param filePath The destination path for the save file.
	 * @param era The current Era of the game.
	 * @param turn The current turn number.
	 * @param totalRounds The total number of rounds in the game.
	 * @param rows The number of rows in the grid.
	 * @param cols The number of columns in the grid.
	 * @param entities The list of all active entities to be saved.
	 * @throws FileOperationException To catch I/O error occurs while writing to the file.
	 */
    public static void saveGame(String filePath, String era, int turn, int totalRounds, int rows, int cols, ArrayList<Entity> entities) throws FileOperationException {
        try(FileWriter writer = new FileWriter(filePath)) {
        	
            writer.write(String.format("%s,%d,%d,%d,%d\n", era, turn, totalRounds, rows, cols));
            
            for(Entity entity : entities) {
                String type = entity.getClass().getSimpleName();
                int r = entity.getLocation().getRow();
                int c = entity.getLocation().getCol();
                String imgPath = entity.getImagePath(); 
                
                int score = 0;
                int cooldown = 0;
                if(entity instanceof Animal) {
                    Animal animal = (Animal) entity;
                    score = animal.getScore();
                    cooldown = animal.getCooldown();
                }
                writer.write(String.format("%s,%d,%d,%d,%d,%s\n", type, r, c, score, cooldown, imgPath));
            }
        } catch (IOException e) {
            throw new FileOperationException("Failed to save game file.", e);
        }
    } 
    
    /**
     * Loads a previously saved game from a text file.
     * <p>
     * This method parses the file, validates the header and data integrity, 
     * and reconstructs the list of entities with their saved properties.
     * </p>
     * @param filePath The path of the save file to read.
     * @return A {@link LoadedGameFormat} object containing all the restored game data.
     * @throws FileOperationException If the file does not exist or cannot be read.
     * @throws InvalidGameData If the file content is empty, corrupt, or has an invalid format.
     */
    public static LoadedGameFormat loadGame(String filePath) throws FileOperationException, InvalidGameData {
        File file = new File(filePath);
        
        if (!file.exists()) {
            throw new FileOperationException("Save file not found: " + filePath);
        }

        try (Scanner scanner = new Scanner(file)) {
            if (!scanner.hasNextLine()) {
                throw new InvalidGameData("Save file is empty.");
            }

            String header = scanner.nextLine().trim();
            String[] line1 = header.split(",");
            
            if (line1.length < 5) {
                throw new InvalidGameData("Invalid header format.");
            }

            String era = line1[0];
            int turn = Integer.parseInt(line1[1]);
            int totalRounds = Integer.parseInt(line1[2]);
            int rows = Integer.parseInt(line1[3]);
            int cols = Integer.parseInt(line1[4]);

            ArrayList<Entity> entities = new ArrayList<>();

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length < 6) continue; 

                String name = parts[0];
                int r = Integer.parseInt(parts[1]);
                int c = Integer.parseInt(parts[2]);
                int score = Integer.parseInt(parts[3]);
                int cooldown = Integer.parseInt(parts[4]);
                String imgPath = parts[5]; 

                Location loc = new Location(r, c);
                Entity newEntity = null;
                
                switch (name) {
                    case "ApexPredator":
                        newEntity = new ApexPredator(loc, imgPath, "Apex", era);
                        break;
                    case "Predator":
                        newEntity = new Predator(loc, imgPath, "Player", era);
                        break;
                    case "Prey":
                        newEntity = new Prey(loc, imgPath, "Prey", era);
                        break;
                    case "Food":
                        newEntity = new Food(loc, imgPath); 
                        break;
                }

                if (newEntity != null) {
                    if (newEntity instanceof Animal) {
                        Animal animal = (Animal) newEntity;
                        GameConfig.configureAnimal(animal, era);
                        animal.setScore(score);
                        animal.setCooldown(cooldown);
                    }
                    entities.add(newEntity);
                }
            }
            
            return new LoadedGameFormat(era, turn, totalRounds, rows, cols, entities);

        } catch (Exception e) {
            throw new FileOperationException("Error parsing save file: " + e.getMessage(), e);
        }
    }
}