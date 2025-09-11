import javax.swing.*;

/*
 * Written by Nicholas Cercos
 */
public interface GameBoard {

	void populateBoard();
	boolean cellClicked(JButton button, int row, int col);
	void bombFound();
	void checkForWin();

}
