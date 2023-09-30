import javax.swing.*;

/**
 *
 * @author Nicholas Cercos
 *
 */
public interface GameBoardInterface {
	void populateBoard();
	boolean cellClicked(JButton button, int row, int col);
	void bombFound();
	void checkForWin();
}
