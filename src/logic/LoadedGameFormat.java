package logic;

import java.util.ArrayList;
import entities.Entity;

/**
 * A simple data transfer object used during the game loading process.
 * <p>
 * Since the {@link io.SaveLoad#loadGame} method needs to return multiple pieces of data
 * (global settings, grid dimensions, and the list of entities) at once, this class
 * acts as a container to package all that information together.
 * </p>
 */
public class LoadedGameFormat {
    public String era;
    public int turnCount;
    public int totalRounds;
    public int rows;
    public int cols;
    public ArrayList<Entity> entities;

    public LoadedGameFormat(String era, int turnCount, int totalRounds, int rows, int cols, ArrayList<Entity> entities) {
        this.era = era;
        this.turnCount = turnCount;
        this.totalRounds = totalRounds;
        this.rows = rows;
        this.cols = cols;
        this.entities = entities;
    }
}