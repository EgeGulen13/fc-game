package logic;

import java.util.ArrayList;
import entities.*;

/**
 * Manages all movement calculations and validation logic for the game entities.
 * <p>
 * This class is responsible for determining valid moves for walking and special abilities
 * based on the grid boundaries, entity types, and specific Era rules.
 * It relieves the GameEngine from complex coordinate mathematics.
 * </p>
 */
public class MovementManager {
    private int rows, cols;

    /**
     * Constructs a MovementManager with the grid dimensions.
     * @param rows Total number of rows in the game grid.
     * @param cols Total number of columns in the game grid.
     */
    public MovementManager(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
    }

    /**
     * Checks if the given coordinates are within the grid boundaries.
     * @param r The row index.
     * @param c The column index.
     * @return true if the location is inside the grid, false otherwise.
     */
    public boolean isValidLocation(int r, int c) {
        return r >= 0 && r < this.rows && c >= 0 && c < this.cols;
    }

    /**
     * Checks if a target location exists within a list of locations.
     * Used to verify if a player's move is valid.
     * @param list The list of valid locations.
     * @param target The location to check.
     * @return true if the list contains the target location.
     */
    public boolean containsLocation(ArrayList<Location> list, Location target) {
        for (Location l : list) {
            if (l.equals(target)) return true;
        }
        return false;
    }
    
    /**
     * Calculates all valid standard walk moves (1 cell in any direction) for a given location.
     * @param loc The starting location.
     * @return A list of valid adjacent locations (up, down, left, right, diagonals).
     */
    public ArrayList<Location> getValidWalkMoves(Location loc) {
        ArrayList<Location> validMoves = new ArrayList<>();
        int r = loc.getRow();
        int c = loc.getCol();

        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                int nr = r + dr;
                int nc = c + dc;
                if (isValidLocation(nr, nc)) {
                    validMoves.add(new Location(nr, nc));
                }
            }
        }
        return validMoves;
    }
    
    /**
     * Calculates valid moves for an animal's special ability based on Era rules.
     * @param animal The animal attempting to use an ability.
     * @param currentEra The current game Era (Past, Present, Future).
     * @param activeEntities The list of all active entities (needed for context-aware rules like adjacency).
     * @return A list of locations reachable via the special ability.
     */
    public ArrayList<Location> getAbilityMoves(Animal animal, String currentEra, ArrayList<Entity> activeEntities) {
        ArrayList<Location> moves = new ArrayList<>();
        
        if (!animal.isAbilityAvailable()) return moves;

        Location loc = animal.getLocation();
        int range = animal.getAbilityRange();
        String type = animal.getAbilityType();
        
        if (type == null) return moves;

        
        if (currentEra.equals("Present") && animal instanceof Predator) {
            boolean isAdjacentToApex = false;
            ArrayList<Location> neighbors = getValidWalkMoves(loc);
            
            for (Location n : neighbors) {
                for (Entity e : activeEntities) {
                    if (e instanceof ApexPredator && e.getLocation().equals(n)) {
                        isAdjacentToApex = true;
                        break;
                    }
                }
                if (isAdjacentToApex) break;
            }
            if (!isAdjacentToApex) return moves; 
        }

        if (type.equals(Animal.TYPE_RADIAL)) {
            addMovesInRange(loc, range, moves);
        } else if (type.equals(Animal.TYPE_LINEAR)) {
            addLinearMoves(loc, range, moves);
        } else if (type.equals(Animal.TYPE_ZIGZAG)) {
            addZigzagMoves(loc, moves);
        }

        return moves;
    }
    
    private void addMovesInRange(Location start, int range, ArrayList<Location> moves) {
        int r = start.getRow();
        int c = start.getCol();
        for (int i = r - range; i <= r + range; i++) {
            for (int j = c - range; j <= c + range; j++) {
                if (isValidLocation(i, j) && !(i == r && j == c)) {
                    moves.add(new Location(i, j));
                }
            }
        }
    }

    private void addLinearMoves(Location start, int range, ArrayList<Location> moves) {
        int r = start.getRow();
        int c = start.getCol();
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] dir : directions) {
            for (int i = 1; i <= range; i++) {
                int nr = r + (dir[0] * i);
                int nc = c + (dir[1] * i);
                if (isValidLocation(nr, nc)) moves.add(new Location(nr, nc));
            }
        }
    }

    private void addZigzagMoves(Location start, ArrayList<Location> moves) {
        int r = start.getRow();
        int c = start.getCol();

        int[][] zigzags = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        for (int[] dir : zigzags) {
            int nr = r + dir[0];
            int nc = c + dir[1];
            if (isValidLocation(nr, nc)) moves.add(new Location(nr, nc));
        }
    }
}