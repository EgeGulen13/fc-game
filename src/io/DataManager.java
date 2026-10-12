package io;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.SecureRandom;
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
import logic.GameConfig;

/**
 * Handles the initial data loading and parsing for the game.
 * This class reads configuration files, parses the entity data and instantiates the game objects dynamically.
 */
public class DataManager {
	private SecureRandom secureRandom;

	public DataManager() {
		this.secureRandom = new SecureRandom();
	}
	
	/**
	 * Reads the game file, creates entities, and configures their abilities based on the Era.
	 * @param fileName The path to the text file (ex: "src/assets/past_animals.txt").
	 * @param rows Total grid rows for random positioning.
	 * @param cols Total grid columns for random positioning.
	 * @return A list of initialized and configured entities.
	 * @throws FileOperationException If the file path is incorrect.
	 */
	public ArrayList<Entity> loadGameFile(String fileName, int rows, int cols) throws FileNotFoundException, FileOperationException {
		ArrayList<Entity> entities = new ArrayList<>();
		File file = new File(fileName);
		
		try (Scanner scanner = new Scanner(file)) {
			if (!scanner.hasNextLine()) {
				throw new FileNotFoundException("File is empty: " + fileName);
			}
			
			String firstLine = scanner.nextLine();
			String[] parts = firstLine.split("Era:");
			if (parts.length < 2) {
				throw new IllegalArgumentException("Invalid file format: Era missing.");
			}
			String currentEra = parts[1].trim();
			
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if (line.trim().isEmpty()) continue; 
				
				String[] splitColon = line.split(":");
				if (splitColon.length < 2) continue;
				
				String chainLine = splitColon[1];
				String[] animalArray = chainLine.split(",");
				
				for (int i = 0; i < animalArray.length; i++) {
					String name = animalArray[i].trim();
					String imagePath = "src/assets/" + name.toLowerCase() + ".png";
					
					int r = secureRandom.nextInt(rows);
					int c = secureRandom.nextInt(cols);
					Location loc = new Location(r, c);
					
					Entity newEntity = null;
					
					if (i == 0) {
						newEntity = new ApexPredator(loc, imagePath, name, currentEra);
					} else if (i == 1) {
						newEntity = new Predator(loc, imagePath, name, currentEra);
					} else if (i == 2) {
						newEntity = new Prey(loc, imagePath, name, currentEra);
					} else if (i == 3) {
						newEntity = new Food(loc, imagePath); 
					}
					
					if (newEntity instanceof Animal) {
						GameConfig.configureAnimal((Animal) newEntity, currentEra);
					}
					
					if (newEntity != null) {
						entities.add(newEntity);
					}
				}
			} 	
		}
		catch (FileNotFoundException e) {
            throw new FileOperationException("Asset file not found: " + fileName, e);
        }
		return entities;
	}
}