package logic;

import java.security.SecureRandom;
import java.util.ArrayList;

import entities.Animal;
import entities.ApexPredator;
import entities.Entity;
import entities.Food;
import entities.Location;
import entities.Predator;
import entities.Prey;
import entities.Respawnable;
import exceptions.FileOperationException;
import exceptions.InvalidGameData;
import io.DataManager;
import io.GameLogger;
import io.SaveLoad;

/**
 * The core engine of the game simulation.
 * <p>
 * This class manages the game loop and entity turns, interactions such as eating and hunting,
 * and delegates movement calculations to the {@link MovementManager}.
 * </p>
 */
public class GameEngine {
    private int rows, cols;
    private int turnCount;
    private int totalRounds;
    private ArrayList<Entity> activeEntities;    
    private DataManager dataManager;
    private MovementManager movementManager; 
    private String currentEra;
    
    /**
     * Initializes the GameEngine with grid dimensions and round limit.
     * @param rows Number of rows in the grid.
     * @param cols Number of columns in the grid.
     * @param totalRounds The maximum number of turns for this session.
     */
    public GameEngine(int rows, int cols, int totalRounds) { 
        this.rows = rows;
        this.cols = cols;
        this.dataManager = new DataManager();
        this.movementManager = new MovementManager(rows, cols); 
        this.activeEntities = new ArrayList<>();
        this.turnCount = 0;
        this.totalRounds = totalRounds;
    }
    
    /**
     * Starts a new game session by loading initial entities from a file based on the selected Era.
     * @param selectedEra The time period selected by the users.
     */
    public void startGame(String selectedEra) {
        this.currentEra = selectedEra;
        String filePath = "";
        
        switch (selectedEra) {
            case "Past": filePath = "src/assets/past_animals.txt"; break;
            case "Present": filePath = "src/assets/present_animals.txt"; break;
            case "Future": filePath = "src/assets/future_animals.txt"; break;
            default: System.out.println("Error: Invalid Era!"); return;
        }

        try {
            ArrayList<Entity> allEntities = dataManager.loadGameFile(filePath, this.rows, this.cols);
            this.activeEntities = chooseAnimals(allEntities);
            validateInitialPositions(); 
            
            GameLogger.logGameStart(this.currentEra, this.totalRounds);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Filters and prepares the list of entities for the game.
     * Randomly selects one entity of each available type and configures their stats.
     * @param list The raw list of entities parsed from the file.
     * @return A consolidated list containing only the active entities for this session.
     */
    private ArrayList<Entity> chooseAnimals(ArrayList<Entity> list) {
        ArrayList<Entity> gameList = new ArrayList<>();
        ArrayList<Food> foods = new ArrayList<>(); 
        ArrayList<Prey> preys = new ArrayList<>();
        ArrayList<Predator> predators = new ArrayList<>();
        ArrayList<ApexPredator> apexes = new ArrayList<>();
        
        for(Entity entity : list) {
            if(entity instanceof Food) foods.add((Food) entity);
            else if(entity instanceof Prey) preys.add((Prey) entity);
            else if(entity instanceof Predator) predators.add((Predator) entity);
            else apexes.add((ApexPredator) entity);
        }
        
        SecureRandom rng = new SecureRandom();
        if (!apexes.isEmpty()) gameList.add(apexes.get(rng.nextInt(apexes.size())));
        if (!predators.isEmpty()) gameList.add(predators.get(rng.nextInt(predators.size())));
        if (!preys.isEmpty()) gameList.add(preys.get(rng.nextInt(preys.size())));
        if (!foods.isEmpty()) gameList.add(foods.get(rng.nextInt(foods.size())));
        
        for (Entity e : gameList) {
            if (e instanceof Animal) {
                GameConfig.configureAnimal((Animal)e, this.currentEra);
            }
        }
        return gameList;
    }

    /**
     * Executes a complete game turn in a specific order: Prey AI -> Player -> Apex AI.
     * <p>
     * This method is the main entry point called by the GUI when the user attempts a move.
     * </p>
     * @param targetRow The row index of the player's intended destination.
     * @param targetCol The column index of the player's intended destination.
     * @return {@code true} if the turn was processed successfully; {@code false} if the move was invalid.
     */
    public boolean processTurn(int targetRow, int targetCol) {
        if (!isValidMoveForPlayer(targetRow, targetCol)) {
            return false; 
        }
        
        GameLogger.logRoundStart(turnCount + 1);
        
        movePrey();
        movePlayer(targetRow, targetCol);
        moveApex();
        endTurn();
        
        return true;
    }

    /**
     * Finalizes the turn by incrementing the turn counter and reducing ability cooldowns for all animals.
     */
    private void endTurn() {
        this.turnCount++;
        for (Entity enitity : activeEntities) {
            if (enitity instanceof Animal) {
                ((Animal) enitity).decreaseCooldown();
            }
        }
        GameLogger.logRoundEnded(this.turnCount);
    }

    /**
     * Handles the movement logic for the player (Predator).
     * Validates the move, updates position, checks for collisions, and triggers ability usage if applicable.
     * @param r The target row index.
     * @param c The target column index.
     */
    private void movePlayer(int r, int c) {
        Predator player = getPlayer();
        Location target = new Location(r, c);
        Location oldLoc = player.getLocation(); 
        
        ArrayList<Location> walkMoves = movementManager.getValidWalkMoves(player.getLocation());
        boolean isWalk = movementManager.containsLocation(walkMoves, target);
        
        player.move(target);
        
        GameLogger.logMove("Player (Predator)", oldLoc, target);
        
        checkCollision(player);
        
        if (!isWalk) {
            player.useAbility();
        }
    }
    
    /**
     * Executes a move for an AI-controlled animal.
     * Automatically detects if the move requires using a special ability based on standard walk availability.
     * @param animal The animal entity performing the move.
     * @param targetLoc The target destination.
     * @param walkMoves The list of valid standard walking moves (used to distinguish abilities).
     */
    private void performSmartMove(Animal animal, Location targetLoc, ArrayList<Location> walkMoves) {
        if (targetLoc == null) return;
        
        Location oldLoc = animal.getLocation(); 

        boolean isAbilityUsed = !movementManager.containsLocation(walkMoves, targetLoc);

        animal.move(targetLoc);
        
        GameLogger.logMove(animal.getClass().getSimpleName(), oldLoc, targetLoc);
        
        if (isAbilityUsed) {
            animal.useAbility();
        }

        checkCollision(animal); 
    }

    /**
     * Controls the AI behavior for the Prey.
     * Uses a "greedy" algorithm to maximize distance from threats while minimizing distance to Food.
     */
    private void movePrey() {
        Prey prey = getEntityByType(Prey.class);
        if (prey == null) return;

        ArrayList<Location> walkMoves = movementManager.getValidWalkMoves(prey.getLocation());
        ArrayList<Location> abilityMoves = movementManager.getAbilityMoves(prey, currentEra, activeEntities);
        
        ArrayList<Location> allMoves = new ArrayList<>(walkMoves);
        allMoves.addAll(abilityMoves);

        Entity threat = getClosestThreat(prey);
        Entity food = findClosestTarget(prey, Food.class);

        Location bestMove = null;
        double bestScore = -Double.MAX_VALUE;

        for (Location move : allMoves) {
            double score = 0.0;
            if (threat != null) score += move.distanceTo(threat.getLocation()) * 2.0; 
            if (food != null) score -= move.distanceTo(food.getLocation()); 
            
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }
        performSmartMove(prey, bestMove, walkMoves);
    }

    /**
     * Controls the AI behavior for the Apex Predator.
     * Prioritizes instant kills if a target is within range, otherwise chases the nearest valid target (Prey or Player).
     */
    private void moveApex() {
        ApexPredator apex = getEntityByType(ApexPredator.class);
        if (apex == null) return;

        ArrayList<Location> walkMoves = movementManager.getValidWalkMoves(apex.getLocation());
        ArrayList<Location> abilityMoves = movementManager.getAbilityMoves(apex, currentEra, activeEntities);
        
        ArrayList<Location> allMoves = new ArrayList<>(walkMoves);
        allMoves.addAll(abilityMoves);

        Entity prey = getEntityByType(Prey.class);
        Entity player = getPlayer(); 

        for (Location move : allMoves) {
            if (prey != null && move.equals(prey.getLocation())) {
                performSmartMove(apex, move, walkMoves);
                return; 
            }
            if (player != null && move.equals(player.getLocation())) {
                performSmartMove(apex, move, walkMoves);
                return; 
            }
        }

        Entity target = null;
        if (prey != null && player != null) {
            double d1 = apex.getLocation().distanceTo(prey.getLocation());
            double d2 = apex.getLocation().distanceTo(player.getLocation());
            target = (d1 < d2) ? prey : player;
        } else {
            target = (prey != null) ? prey : player;
        }

        if (target != null) {
            Location bestMove = getClosestMove(allMoves, target.getLocation());
            performSmartMove(apex, bestMove, walkMoves);
        } else if (!walkMoves.isEmpty()) {
            performSmartMove(apex, walkMoves.get(0), walkMoves);
        }
    }


    /**
     * Checks if the attacker has moved into a location occupied by another entity.
     * If a collision is detected, triggers the encounter resolution.
     * @param attacker The entity that just moved.
     */
    private void checkCollision(Entity attacker) {
        Entity victim = null;
        for (Entity other : activeEntities) {
            if (other != attacker && other.getLocation().equals(attacker.getLocation())) {
                victim = other;
                break;
            }
        }
        if (victim != null) resolveEncounter(attacker, victim);
    }

    /**
     * Handles the interaction logic when two entities collide (e.g., eating, hunting).
     * Updates scores, logs the event, and respawns the victim if necessary.
     * @param attacker The entity initiating the collision.
     * @param victim The entity being collided with.
     */
    private void resolveEncounter(Entity attacker, Entity victim) {
        if (attacker instanceof Prey && victim instanceof Food) {
            ((Prey) attacker).addScore(3);
            GameLogger.logScore("Prey", 3, "Ate Food"); 
            safeRespawn(victim);
        }
        else if (attacker instanceof Predator) {
            if (victim instanceof Prey) {
                ((Predator) attacker).addScore(3);
                ((Prey) victim).decreaseScore(1);
                
                GameLogger.logScore("Player", 3, "Caught Prey");
                GameLogger.logScore("Prey", -1, "Eaten by Player");
                
                safeRespawn(victim);
            } 
            else if (victim instanceof ApexPredator) {
                ((ApexPredator) victim).addScore(1);
                ((Predator) attacker).decreaseScore(1);
                
                GameLogger.logScore("Apex", 1, "Caught Player");
                GameLogger.logScore("Player", -1, "Ran into Apex");
                
                safeRespawn(attacker); 
            }
        }
        else if (attacker instanceof ApexPredator) {
            if (victim instanceof Animal) {
                ((ApexPredator) attacker).addScore(1);
                ((Animal) victim).decreaseScore(1);
                
                GameLogger.logScore("Apex", 1, "Hunted " + victim.getClass().getSimpleName());
                GameLogger.logScore(victim.getClass().getSimpleName(), -1, "Hunted by Apex");
                
                safeRespawn(victim);
            }
        }
    }

    /**
     * Respawns an entity at a random valid location on the grid.
     * Ensures the new location is not currently occupied by another entity.
     * @param entity The entity to be respawned.
     */
    private void safeRespawn(Entity entity) {
        Respawnable respawnable = (Respawnable) entity;
        int attempts = 0;
        final int MAX_ATTEMPTS = rows * cols;
        do {
            respawnable.respawn(rows, cols);
            attempts++;
        } while (isOccupied(entity.getLocation(), entity) && attempts < MAX_ATTEMPTS);
    }
    
    /**
     * Validates if a target coordinate is a legal move for the player.
     * Checks both standard walk moves and special ability moves.
     * @param r Target row index.
     * @param c Target column index.
     * @return {@code true} if the move is allowed.
     */
    private boolean isValidMoveForPlayer(int r, int c) {
        Predator player = getPlayer();
        if (player == null) return false;
        Location target = new Location(r, c);
        
        ArrayList<Location> walkMoves = movementManager.getValidWalkMoves(player.getLocation());
        if (movementManager.containsLocation(walkMoves, target)) return true;

        ArrayList<Location> abilityMoves = movementManager.getAbilityMoves(player, currentEra, activeEntities);
        if (movementManager.containsLocation(abilityMoves, target)) return true;

        return false;
    }

    /**
     * Checks if a specific location is currently occupied by any entity other than the one specified.
     * @param loc The location to check.
     * @param ignoreSelf The entity to exclude from the check (usually the one moving/spawning).
     * @return {@code true} if the location is occupied.
     */
    private boolean isOccupied(Location loc, Entity ignoreSelf) {
        for (Entity enitity : activeEntities) {
            if (enitity != ignoreSelf && enitity.getLocation().equals(loc)) return true;
        }
        return false;
    }
    
    /**
     * Ensures that all entities start at unique positions at the beginning of the game.
     * If a collision is detected during initialization, the entity is respawned.
     */
    private void validateInitialPositions() {
        for (Entity enitity : activeEntities) {
            if (isOccupied(enitity.getLocation(), enitity)) safeRespawn(enitity);
        }
    }
    
    /**
     * Helper method to find the first instance of a specific entity type in the active list.
     * @param <T> The type of the entity to find (must extend Entity).
     * @param type The class object of the entity.
     * @return The found entity cast to the specific type, or null if not found.
     */
	private <T extends Entity> T getEntityByType(Class<T> type) {
        for (Entity e : activeEntities) if (type.isInstance(e)) return (T) e;
        return null;
    }
    
	/**
     * Convenience method to retrieve the player's character (Predator) from the entity list.
     * @return The Predator instance, or null if dead/not found.
     */
    private Predator getPlayer() {
    	return getEntityByType(Predator.class); 
    }

    /**
     * Identifies the closest threat (Apex Predator or Predator) to the given Prey.
     * @param prey The prey entity.
     * @return The closest threatening entity, or null if none exist.
     */
    private Entity getClosestThreat(Prey prey) {
        Entity pred = findClosestTarget(prey, Predator.class);
        Entity apex = findClosestTarget(prey, ApexPredator.class);
        if (pred != null && apex != null) {
            double d1 = prey.getLocation().distanceTo(pred.getLocation());
            double d2 = prey.getLocation().distanceTo(apex.getLocation());
            return (d1 < d2) ? pred : apex;
        }
        return (pred != null) ? pred : apex;
    }

    /**
     * Calculates distances to find the nearest entity of a specific type.
     * @param current The reference entity.
     * @param targetClass The class type of the target to look for (e.g., Food.class).
     * @return The closest entity of the specified type, or null if none exist.
     */
    private Entity findClosestTarget(Entity current, Class<? extends Entity> targetClass) {
        Entity closest = null;
        double minDst = Double.MAX_VALUE;
        for (Entity entity : activeEntities) {
            if (targetClass.isInstance(entity) && entity != current) {
                double dst = current.getLocation().distanceTo(entity.getLocation());
                if (dst < minDst) {
                    minDst = dst;
                    closest = entity;
                }
            }
        }
        return closest;
    }
    
    /**
     * Selects the move from a list of options that brings the entity closest to a specific target.
     * Used by AI to navigate towards a goal (Chase logic).
     * @param moves A list of possible valid moves.
     * @param targetLoc The destination coordinates to approach.
     * @return The best location from the list, or null if the list is empty.
     */
    private Location getClosestMove(ArrayList<Location> moves, Location targetLoc) {
        double minDst = Double.MAX_VALUE;
        Location best = null;
        for (Location loc : moves) {
            double dst = loc.distanceTo(targetLoc);
            if (dst < minDst) {
                minDst = dst;
                best = loc;
            }
        }
        return best;
    }
       
    
    public ArrayList<Entity> getActiveEntities() {
    	return activeEntities; 
    }
    
    public int getTurnCount() {
    	return turnCount; 
    }
    
    /**
     * Retrieves valid standard walk moves for the player.
     * @return A list of valid locations for GUI highlighting.
     */
    public ArrayList<Location> getPlayerWalkMoves() {
        Predator player = getPlayer();
        if (player == null) return new ArrayList<>();
        return movementManager.getValidWalkMoves(player.getLocation());
    }

    /**
     * Retrieves valid special ability moves for the player.
     * @return A list of valid locations for GUI highlighting.
     */
    public ArrayList<Location> getPlayerAbilityMoves() {
        Predator player = getPlayer();
        if (player == null) return new ArrayList<>();
        return movementManager.getAbilityMoves(player, currentEra, activeEntities);
    }
    
    /**
     * Saves the current game state to the specified file.
     * @param filePath The destination path for the save file.
     * @throws FileOperationException If saving fails.
     */
    public void saveGame(String filePath) throws FileOperationException {
        SaveLoad.saveGame(filePath, this.currentEra, this.turnCount, this.totalRounds, this.rows, this.cols, this.activeEntities);
    }
    
    /**
     * Loads a previously saved game state.
     * Updates the engine's internal state (Era, Turns, Entities, Grid Size).
     * @param filePath The path of the save file.
     * @throws FileOperationException If the file cannot be read.
     * @throws InvalidGameData If the file content is corrupt.
     */
    public void loadGame(String filePath) throws FileOperationException, InvalidGameData {
        LoadedGameFormat state = SaveLoad.loadGame(filePath); 

        this.currentEra = state.era;
        this.turnCount = state.turnCount;
        this.totalRounds = state.totalRounds;
        this.rows = state.rows;
        this.cols = state.cols;
        
        this.activeEntities = state.entities;
        
        this.movementManager = new MovementManager(state.rows, state.cols);
    }
    
    public String getCurrentEra() {
        return currentEra;
    }
}