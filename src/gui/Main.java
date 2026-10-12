package gui;
/************** Pledge of Honor ******************************************
I hereby certify that I have completed this programming project on my own without
any help from anyone else. The effort in the project thus belongs completely to me.
I did not search for a solution, or I did not consult any program written by others
or did not copy any program from other sources. I read and followed the guidelines
provided in the project description.
READ AND SIGN BY WRITING YOUR NAME SURNAME AND STUDENT ID
SIGNATURE: <Ege Gülen, 89272>
*************************************************************************/
import javax.swing.SwingUtilities;

/**
 * The entry point of the application.
 * <p>
 * This class handles the initialization of the program.
 * </p>
 */
public class Main {

	/**
	 * The standard main method to start the application.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			GameFrame frame = new GameFrame();
			frame.setVisible(true);
		});
	}

}
