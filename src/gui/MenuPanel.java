package gui;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Represents the initial configuration screen of the game.
 * <p>
 * This panel provides a form interface for the user to select the Game Era,
 * Grid Size, and Total Rounds. It handles input validation before launching
 * the simulation via the parent {@link GameFrame}.
 * </p>
 */
public class MenuPanel extends JPanel {
	private JComboBox<String> eraBox;
	private JComboBox<String> sizeBox;
	private JTextField roundField;
	private JButton startButton;
	private GameFrame parentFrame;
	
    /**
     * Constructs the configuration menu and initializes UI components.
     * Sets up the layout, dropdowns, input fields, and action listeners.
     * @param frame The parent GameFrame controller used to switch scenes.
     */
	public MenuPanel(GameFrame frame) {
		this.parentFrame = frame;
		
		this.setLayout(new GridLayout(5, 2, 10, 10)); 
        this.setBorder(BorderFactory.createEmptyBorder(200, 200, 150, 150));
        
        
        String[] eras = {"Past", "Present", "Future"};
        eraBox = new JComboBox<>(eras);
        
        String[] sizes = {"10x10", "15x15", "20x20"};
        sizeBox = new JComboBox<>(sizes);
        
        roundField = new JTextField(""); 
        
        startButton = new JButton("START GAME");
        startButton.setFont(new Font("Arial", Font.BOLD, 14));
        

        
        this.add(new JLabel("Game Mode (Era):"));
        this.add(eraBox);
        
        this.add(new JLabel("Grid Size:"));
        this.add(sizeBox);
        
        this.add(new JLabel("Total Rounds :"));
        this.add(roundField);
        
        this.add(new JLabel("")); 
        this.add(startButton);
        
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleStart();
            }
        });
	}
	
    /**
     * Logic executed when the "START GAME" button is clicked.
     * <p>
     * Parses the selected Grid Size (e.g., "15x15" -> 15, 15) and validates the 
     * Round number input. If the input is invalid (non-integer or < 10), 
     * a warning popup is shown. If valid, it triggers the game start.
     * </p>
     */
	private void handleStart() {
        String selectedEra = (String) eraBox.getSelectedItem();

        String selectedSize = (String) sizeBox.getSelectedItem();
        String[] parts = selectedSize.split("x");
        int rows = Integer.parseInt(parts[0]);
        int cols = Integer.parseInt(parts[1]);

        int rounds = 0;
        try {
            rounds = Integer.parseInt(roundField.getText());
            if (rounds < 10) {
                JOptionPane.showMessageDialog(this, "Minimum rounds must be 10!", "Warning", JOptionPane.WARNING_MESSAGE);
                return; 
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        parentFrame.startGame(selectedEra, rows, cols, rounds);
    }
}