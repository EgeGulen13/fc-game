package exceptions;

/**
 * A custom exception thrown when the game data exists but is corrupted or logically invalid.
 * <p>
 * It is used when a file is successfully read, but its content does not match the expected format (ex: missing headers, 
 * negative grid sizes, or malformed entity lines).
 * </p>
 */
public class InvalidGameData extends GameException {
	public InvalidGameData(String message) {
        super("Map Data Error: " + message);
    }
}
