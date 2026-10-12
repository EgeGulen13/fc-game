package exceptions;

/**
 * Base exception class for the entire game project.
 * All specific game exceptions should extend this class.
 */
public class GameException extends Exception {
    
    public GameException(String message) {
        super(message);
    }

    public GameException(String message, Throwable cause) {
        super(message, cause);
    }
}