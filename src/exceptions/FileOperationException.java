package exceptions;

/**
 * A custom exception thrown when a file Input/Output operation fails.
 * <p>
 * This exception is typically used when the game fails to read from or write to the disk,
 * such as when a save file is missing, inaccessible, or cannot be created.
 * It wraps standard IOExceptions to provide a more specific game-context error.
 * </p>
 */

public class FileOperationException extends GameException {
    
    public FileOperationException(String message) {
        super("File Error: " + message);
    }
    
    public FileOperationException(String message, Throwable cause) {
        super("File Error: " + message, cause);
    }
}