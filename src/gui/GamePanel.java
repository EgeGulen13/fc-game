package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import entities.Animal;
import entities.ApexPredator;
import entities.Entity;
import entities.Food;
import entities.Location;
import entities.Predator;
import entities.Prey;
import logic.GameEngine;

/**
 * The main graphical user interface for the game simulation.
 * <p>
 * This panel renders the game grid, entities, and sidebars (stats/actions).
 * It acts as the bridge between the user's input (mouse clicks) and the {@link GameEngine}.
 * </p>
 */
public class GamePanel extends JPanel {
    
    private GameEngine engine;
    private GameFrame parentFrame;
    private int rows, cols;
    private int totalRounds;
    private String currentEra;
    
    private int cellSize = 40; 
    private int xOffset = 0;   
    private int yOffset = 0;
    private Map<String, Image> imageCache; 

    private JTextArea txtGeneralInfo;
    private JTextArea txtApexInfo;
    private JTextArea txtPredatorInfo;
    private JTextArea txtPreyInfo;
    private JButton btnSave, btnLoad;

    /**
     * Initializes the GamePanel and starts the simulation engine.
     * @param era    The selected time period.
     * @param rows   Grid row count.
     * @param cols   Grid column count.
     * @param rounds Maximum number of turns.
     * @param frame  Reference to the main parent window.
     */
    public GamePanel(String era, int rows, int cols, int rounds, GameFrame frame) {
        this.currentEra = era;
        this.rows = rows;
        this.cols = cols;
        this.totalRounds = rounds;
        this.parentFrame = frame;
        this.imageCache = new HashMap<>();

        this.setLayout(new BorderLayout());
        this.add(createRightPanel(), BorderLayout.EAST); 
        this.add(createLeftPanel(), BorderLayout.WEST);  

        engine = new GameEngine(rows, cols, rounds);
        engine.startGame(era);
        
        updateSidePanelInfo();

        this.addMouseListener(new MouseAdapter() {
        	@Override
            public void mousePressed(MouseEvent e) {
                handleMouseClick(e.getX(), e.getY());
            }
        });
    }

    /**
     * Creates the left sidebar containing Save/Load control buttons.
     * @return A constructed JPanel containing action buttons.
     */
    private JPanel createLeftPanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(150, 0));
        panel.setBackground(new Color(220, 220, 220));
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY));
        panel.setLayout(new GridLayout(8, 1, 10, 10)); 
        panel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        btnSave = new JButton("Save");
        btnLoad = new JButton("Load");
        
        String savePath = "savegame.txt";
        
        btnSave.addActionListener(e -> {
            try {
                engine.saveGame(savePath);
                JOptionPane.showMessageDialog(this, "Game Saved Successfully!");
                this.requestFocusInWindow(); 
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Save Failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnLoad.addActionListener(e	 -> {
            File saveFile = new File(savePath);
            if (!saveFile.exists()) {
                JOptionPane.showMessageDialog(this, "No save file found!", "Load Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                engine.loadGame(savePath);
                this.currentEra = engine.getCurrentEra();
                updateSidePanelInfo();
                repaint(); 
                JOptionPane.showMessageDialog(this, "Game Loaded Successfully!");
                this.requestFocusInWindow();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Load Failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(new JLabel("Save Options", SwingConstants.CENTER));
        panel.add(btnSave);
        panel.add(btnLoad);
        
        return panel;
    }

    /**
     * Creates the right sidebar containing entity statistics.
     * @return A constructed JPanel with text areas for stats.
     */
    private JPanel createRightPanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(180, 0)); 
        panel.setLayout(new GridLayout(4, 1, 10, 50)); 
        panel.setBackground(Color.LIGHT_GRAY);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, Color.GRAY), 
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        txtGeneralInfo = new JTextArea();
        panel.add(createStatCard("Game Info", Color.WHITE, txtGeneralInfo));
        
        txtApexInfo = new JTextArea();
        panel.add(createStatCard("Apex Predator (AI)", new Color(230, 100, 100), txtApexInfo));

        txtPredatorInfo = new JTextArea();
        panel.add(createStatCard("Predator (You)", new Color(230, 160, 80), txtPredatorInfo));

        txtPreyInfo = new JTextArea();
        panel.add(createStatCard("Prey (AI)", new Color(100, 180, 230), txtPreyInfo));

        return panel;
    }

    /**
     * Helper method to create a styled information card for the sidebar.
     * @param title The title of the card border.
     * @param bg The background color.
     * @param contentArea The JTextArea component to embed.
     * @return The styled container panel.
     */
    private JPanel createStatCard(String title, Color bg, JTextArea contentArea) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(bg);
        
        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2), title);
        titledBorder.setTitleFont(titledBorder.getTitleFont().deriveFont(java.awt.Font.BOLD, 14f));
        card.setBorder(titledBorder);
        
        contentArea.setEditable(false);       
        contentArea.setFocusable(false);      
        contentArea.setOpaque(false);         
        contentArea.setWrapStyleWord(true);   
        contentArea.setLineWrap(true);        
        contentArea.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 12)); 
        contentArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); 
        
        card.add(contentArea, BorderLayout.CENTER);
        
        return card;
    }

    /**
     * Custom painting method to render the grid, entities, and move highlights.
     * @param g The graphics context.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        g.setColor(new Color(240, 240, 240));
        g.fillRect(0, 0, getWidth(), getHeight());

        int sidePanelsWidth = 150 + 180;
        int availableW = getWidth() - sidePanelsWidth; 
        int availableH = getHeight();
        
        if (availableW <= 0) availableW = 100;
        
        int cellW = availableW / cols;
        int cellH = availableH / rows;
        this.cellSize = Math.min(cellW, cellH);
        
        this.xOffset = 150 + (availableW - (cols * cellSize)) / 2;
        this.yOffset = (availableH - (rows * cellSize)) / 2;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int x = xOffset + (c * cellSize);
                int y = yOffset + (r * cellSize);
                
                g.setColor(Color.WHITE);
                g.fillRect(x, y, cellSize, cellSize);
                g.setColor(new Color(200, 200, 200));
                g.drawRect(x, y, cellSize, cellSize);
            }
        }
        
        drawValidMoves(g);

        ArrayList<Entity> entities = engine.getActiveEntities();
        drawLayer(g, entities, Food.class);
        drawLayer(g, entities, Prey.class);
        drawLayer(g, entities, Predator.class);
        drawLayer(g, entities, ApexPredator.class);
    }
    
    /**
     * Draws specific type of entities to ensure correct z-ordering.
     * @param g Graphics context.
     * @param list List of all entities.
     * @param cls The class type to filter and draw.
     */
    private void drawLayer(Graphics g, ArrayList<Entity> list, Class<?> cls) {
        for (Entity e : list) {
            if (cls.isInstance(e)) {
                Image img = getImage(e.getImagePath());
                e.draw(g, xOffset, yOffset, cellSize, img);
            }
        }
    }
    
    /**
     * Retrieves an image from the cache or loads it from disk if missing.
     * @param path File path of the image.
     * @return The buffered Image object.
     */
    private Image getImage(String path) {
        if (imageCache.containsKey(path)) return imageCache.get(path);
        try {
            File f = new File(path);
            if (f.exists()) {
                Image img = new ImageIcon(path).getImage();
                imageCache.put(path, img);
                return img;
            }
        } catch (Exception e) { }
        return null;
    }

    /**
     * Renders transparent overlays for valid movement options.
     * Green for walking, Cyan for abilities.
     */
    private void drawValidMoves(Graphics g) {
        ArrayList<Location> walkMoves = engine.getPlayerWalkMoves();
        ArrayList<Location> abilityMoves = engine.getPlayerAbilityMoves();

        g.setColor(new Color(0, 255, 255, 100)); 
        for (Location loc : abilityMoves) {
            boolean isWalkable = false;
            for(Location w : walkMoves) {
                if(w.equals(loc)) { isWalkable = true; break; }
            }
            if (!isWalkable) {
                paintCell(g, loc);
                g.setColor(new Color(0, 150, 200));
                drawCellBorder(g, loc);
                g.setColor(new Color(0, 255, 255, 100));
            }
        }

        g.setColor(new Color(100, 255, 100, 100)); 
        for (Location loc : walkMoves) {
            paintCell(g, loc);
            g.setColor(new Color(0, 200, 0));
            drawCellBorder(g, loc);
            g.setColor(new Color(100, 255, 100, 100));
        }
    }

    private void paintCell(Graphics g, Location loc) {
        int x = xOffset + (loc.getCol() * cellSize);
        int y = yOffset + (loc.getRow() * cellSize);
        g.fillRect(x + 1, y + 1, cellSize - 1, cellSize - 1);
    }

    private void drawCellBorder(Graphics g, Location loc) {
        int x = xOffset + (loc.getCol() * cellSize);
        int y = yOffset + (loc.getRow() * cellSize);
        g.drawRect(x + 1, y + 1, cellSize - 2, cellSize - 2);
    }


    /**
     * Handles mouse click events on the canvas.
     * Converts pixel coordinates to grid coordinates and triggers a turn.
     * @param mouseX X coordinate of the click.
     * @param mouseY Y coordinate of the click.
     */
    private void handleMouseClick(int mouseX, int mouseY) {
    	
        if (mouseX < xOffset || mouseX > xOffset + (cols * cellSize) ||
            mouseY < yOffset || mouseY > yOffset + (rows * cellSize)) {
            return;
        }

        int col = (mouseX - xOffset) / cellSize;
        int row = (mouseY - yOffset) / cellSize;

        boolean turnSuccess = engine.processTurn(row, col);

        if (turnSuccess) {
            updateSidePanelInfo();
            repaint();
            checkGameOver();
        }
    }

    /**
     * Updates the text labels in the right sidebar.
     * Fetches current stats from the engine and formats them for display.
     */
    private void updateSidePanelInfo() {
        txtGeneralInfo.setText(String.format("Round: %d / %d\nEra: %s", 
                engine.getTurnCount(), totalRounds, currentEra));
        
        String apexTxt = "Not Active";
        String predTxt = "Not Active";
        String preyTxt = "Not Active";

        for (Entity e : engine.getActiveEntities()) {
            if (!(e instanceof Animal)) continue;

            Animal animal = (Animal) e;
            String status = getFormattedStatusText(animal); 

            if (e instanceof ApexPredator) {
                apexTxt = String.format("Score: %d\nAbility: %s", animal.getScore(), status);
            } 
            else if (e instanceof Predator) {
                predTxt = String.format("Score: %d\nAbility: %s", animal.getScore(), status);
            } 
            else if (e instanceof Prey) {
                preyTxt = String.format("Score: %d\nAbility: %s", animal.getScore(), status);
            }
        }
        
        txtApexInfo.setText(apexTxt);
        txtPredatorInfo.setText(predTxt);
        txtPreyInfo.setText(preyTxt);
    }
    
    /**
     * Helper method to format the ability status text for the UI.
     * <p>
     * Checks the animal's cooldown state. Returns "READY" if the ability is available,
     * otherwise returns "WAIT" with the remaining turn count.
     * </p>
     * @param a The animal entity to check.
     * @return A formatted string (e.g., "READY" or "WAIT (2)").
     */
    private String getFormattedStatusText(Animal a) {
        int cd = a.getCooldown();
        if (cd == 0) {
            return "READY";
        } else {
            return "WAIT (" + cd + ")";
        }
    }
    
    
    /**
     * Checks if the game has ended based on turn limits.
     * If ended, calculates the winner, logs the result, and shows the summary dialog.
     */
    private void checkGameOver() {
        if (engine.getTurnCount() < totalRounds) {
            return;
        }

        int maxScore = Integer.MIN_VALUE;
        ArrayList<Animal> allAnimals = new ArrayList<>();
        
        for (Entity e : engine.getActiveEntities()) {
            if (e instanceof Animal) {
                Animal a = (Animal) e;
                allAnimals.add(a);
                if (a.getScore() > maxScore) {
                    maxScore = a.getScore();
                }
            }
        }

        ArrayList<String> winners = new ArrayList<>();
        for (Animal a : allAnimals) {
            if (a.getScore() == maxScore) {
                winners.add(a.getClass().getSimpleName());
            }
        }

        String winnerResult;
        if (winners.size() > 1) {
            winnerResult = "TIE! (" + String.join(" & ", winners) + ")";
        } else {
            winnerResult = winners.get(0);
        }
        
        io.GameLogger.logScore(winnerResult, maxScore);
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== GAME OVER ===\n\n");
        sb.append("WINNER: ").append(winnerResult).append("\n\n");
        
        sb.append("--- FINAL SCORES ---\n");
        for (Animal a : allAnimals) {
            sb.append(String.format("%15s: %d\n", a.getClass().getSimpleName(), a.getScore()));
        }

        JOptionPane.showMessageDialog(this, sb.toString(), "End Screen", JOptionPane.INFORMATION_MESSAGE);
        
        parentFrame.returnToMenu();
    }
}