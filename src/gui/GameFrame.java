package gui;

import java.awt.CardLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * The main application window of the game.
 * <p>
 * It utilizes a {@link CardLayout} to manage scene navigation, allowing smooth 
 * transitions between the Main Menu and the active Game Simulation.
 * </p>
 */
public class GameFrame extends JFrame {
    
    private GamePanel gamePanel; 
    private CardLayout cardLayout;
    private JPanel cardPanel;
    
    /**
     * Initializes the main window with standard display settings (800x600, centered).
     * <p>
     * This constructor sets up the {@code CardLayout} container and immediately 
     * loads and displays the {@link MenuPanel} as the starting screen.
     * </p>
     */
    public GameFrame() {
        this.setTitle("Food Chain Through Time");
        this.setSize(800, 600);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        
        MenuPanel menu = new MenuPanel(this); 
        cardPanel.add(menu, "MENU"); 

        this.add(cardPanel);
        cardLayout.show(cardPanel, "MENU");
    }
    
    /**
     * Instantiates the game logic and switches the view to the simulation screen.
     * <p>
     * This method is called by the {@link MenuPanel} after successful configuration.
     * </p>
     * @param era The selected time period.
     * @param rows The number of rows for the grid.
     * @param cols The number of columns for the grid.
     * @param rounds The total duration of the game.
     */
    public void startGame(String era, int rows, int cols, int rounds) {        
        gamePanel = new GamePanel(era, rows, cols, rounds, this);
        cardPanel.add(gamePanel, "GAME");
        cardLayout.show(cardPanel, "GAME");
    }
    
    /**
     * When the game finishes, the frame returns to "MENU" screen.
     */
    public void returnToMenu() {
        cardLayout.show(cardPanel, "MENU");
    }
    
}