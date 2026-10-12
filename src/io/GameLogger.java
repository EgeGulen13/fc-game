package io;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import entities.Location;

/**
 * Handles all logging operations for the game session.
 * <p>
 * Writes game events such as start, moves, scores, game over to a "log.txt" file with precise
 * timestamps for debugging and history tracking.
 * </p>
 */
public class GameLogger {
	
	private static final String file = "log.txt";
	private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
	
	/**
	 * The helper method to append a message to the log file.
	 * @param message The string content to write.
	 */
	private static void writeToFile(String message) {
		
		try (FileWriter writer =	 new FileWriter(file, true)){
			LocalDateTime now = LocalDateTime.now(); 
			
			String timestamp = dtf.format(now);
			writer.write("["+ timestamp + "] " + message);
			writer.write("\n"); //NewLine	
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Logs the a new game session.
	 * @param era The selected game era.
	 * @param totalRounds The total duration of the game.
	 */
	public static void logGameStart(String era, int totalRounds) {
		writeToFile("==========================================");
		String format = String.format("New Game: Era: %s, TotalRounds: %d, User Role: Predator", era, totalRounds);
		writeToFile(format + "\n");
	}
	
	/**
	 * Logs the beginning of a specific round.
	 * @param round The current round number.
	 */
	public static void logRoundStart(int round) {
		writeToFile("-----Round " + round +" started!-----");
	}
	
	/**
	 * Logs the completion of a specific round.
	 * @param round The completed round number.
	 */
	public static void logRoundEnded(int round) {
		writeToFile("-----Round " + round +" ended!-----" + "\n");
	}
	
	/**
	 * Logs the movement of an entity from one location to another.
	 * @param entityName The name/type of the moving entity.
	 * @param startLoc The starting coordinates.
	 * @param endLoc The destination coordinates.
	 */
	public static void logMove(String entityName, Location startLoc, Location endLoc) {
		String format = String.format("%s moved (%d, %d) to (%d, %d)!", entityName, startLoc.getCol(), startLoc.getRow(), endLoc.getCol(), endLoc.getRow());
		writeToFile(format);
	}
	
	/**
	 * Logs the scoring.
	 * @param entityName The entity involved in the event.
	 * @param points The amount of points added or subtracted.
	 * @param event A description of the event (ex: "Ate Food").
	 */
	public static void logScore(String entityName, int points, String event) {
		if (points >= 0) {
			String format = String.format("%s gained %d points! (%s)", entityName, points, event);
			writeToFile(format);
		} else {
			String format = String.format("%s lost %d points! (%s)", entityName, points, event);
			writeToFile(format);
		}
	}
	
	/**
	 * Logs the final result of the game session.
	 * <p>
	 * In case of a tie, the {@code winner} parameter should contain
 	 * a combined string of all winning entities (ex: "TIE! (Lion & Wolf)").
 	 * </p>
	 * @param winner The name of the winning entity.
	 * @param score The final score of the winner.
	 */
	public static void logScore(String winner, int score) {
		String format = String.format("Game Ended! WINNER: %s by %d points!", winner, score);
		writeToFile(format);
		writeToFile("==========================================");
	}
	
	
}
