import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author Nicholas Cercos
 *
 */
public class GameFrame extends JFrame {

	private final int MAX_ROWS;
	private final int MAX_COLUMNS;
	private final int MAX_BOMBS;

	public GameFrame() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		MAX_ROWS = getPlayerInput("How many rows?");
		MAX_COLUMNS = getPlayerInput("How many columns?");
		MAX_BOMBS = getPlayerInput("How many bombs?");

		panel.add(new GameBoard(), BorderLayout.CENTER);

		this.add(panel);
		setTitle("Minesweeper");

		setSize(500, 500);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	/**
	 * Prompts a player for a response.
	 *
	 * @param message The content of the prompt.
	 * @return An integer received by the user.
	 */
	private int getPlayerInput(String message) {
		String input = promptPlayerInput(message);
		while(!isInt(input) || Integer.parseInt(input) < 1)
			input = promptPlayerInput(message);
		return Integer.parseInt(input);
	}

	private String promptPlayerInput(String message) {
		return JOptionPane.showInputDialog(message);
	}

	/**
	 * Checks if a string is a number.
	 *
	 * @param s The string in question.
	 * @return True if the given string is an integer.
	 */
	private boolean isInt(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	private class GameBoard extends JPanel implements GameBoardInterface, ActionListener {

		private JButton[][] buttons;
		private Grid grid;

		private int numClicks;
		private int MAX_NUM_CLICKS;

		// Text
		private final Font font;
		private final Insets insets;
		private final Dimension dimension;

		private boolean gameEnded = false;

		public GameBoard() {
			setLayout(new GridLayout(MAX_ROWS, MAX_COLUMNS));
			this.font = new Font("Arial", Font.BOLD, 20);
			this.insets = new Insets(0,0,0,0);
			this.dimension = new Dimension(25,25);
			populateBoard();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JButton button = (JButton) e.getSource();
			if(!button.isContentAreaFilled())return;
			for(int row = 0; row < buttons.length; row++) {
				for(int col = 0; col < buttons[row].length; col++) {
					if(!button.equals(buttons[row][col]))continue;
					if(cellClicked(button, row, col)) {
						gameEnded = true;
						bombFound();
						break;
					}
				}
			}
		}

		@Override
		public void populateBoard() {
			this.grid = new Grid(MAX_ROWS, MAX_COLUMNS, MAX_BOMBS);

			MAX_NUM_CLICKS = (MAX_ROWS * MAX_COLUMNS) - MAX_BOMBS;
			numClicks = 0;

			if(buttons == null)
				this.buttons = new JButton[MAX_ROWS][MAX_COLUMNS];

			for(int row = 0; row < buttons.length; row++) {
				for(int col = 0; col < buttons[row].length; col++) {
					JButton button;
					if(!gameEnded) {
						button = new JButton();
						button.addActionListener(this);
						buttons[row][col] = button;
						this.add(buttons[row][col]);
					} else button = buttons[row][col];
					button.setEnabled(true);
					button.setContentAreaFilled(true);
					button.setText(null);
					button.setIcon(null);
				}
			}

			gameEnded = false;
			displayBombsCheatSheet();
			System.out.println();
			displayCountsCheatSheet();
			System.out.println();
		}

		@Override
		public boolean cellClicked(JButton button, int row, int col) {
			numClicks++;
			button.setContentAreaFilled(false);
			if(grid.isBombAtLocation(row, col)) {
				button.setIcon(getBombImage(button.getWidth(), button.getHeight()));
				return true;
			}
			int tileNumber = grid.getCountAtLocation(row, col);
			button.setMargin(insets);
			button.setFont(font);
			button.setPreferredSize(dimension);
			button.setText(String.valueOf(tileNumber));
			button.setForeground(getTileColor(tileNumber));
			if(tileNumber == 0)
				showAdjacentCells(row, col);
			checkForWin();
			return false;
		}

		@Override
		public void bombFound() {
			for(int row = 0; row < buttons.length; row++) {
				for(int col = 0; col < buttons[row].length; col++) {
					JButton button = buttons[row][col];
					if(!button.isContentAreaFilled())continue;
					cellClicked(button, row, col);
				}
			}
			displayPlayAgainMessage("You lost!", "You found a bomb!");
		}

		@Override
		public void checkForWin() {
			if(gameEnded || numClicks < MAX_NUM_CLICKS)return;
			gameEnded = true;
			displayPlayAgainMessage("You won!", "You avoided all bombs!");
		}

		/**
		 * Reveals a cell to the user and determines if adjacent cells need to be shown.
		 *
		 * @param row The row of the cell in question.
		 * @param col The column of the cell in question.
		 * @return True if the cell has a proximity count of zero and should reveal adjacent cells.
		 */
		private boolean showCell(int row, int col) {
			if(row < 0 || col < 0)return false;
			if(row > (MAX_ROWS - 1) || col > (MAX_COLUMNS - 1))return false;
			if(grid.isBombAtLocation(row, col))return false;
			JButton button = buttons[row][col];
			if(!button.isContentAreaFilled())return false;
			int count = grid.getCountAtLocation(row, col);
			cellClicked(button, row, col);
			return count == 0;
		}

		/**
		 * Checks all cells around a given point and reveals them
		 * if they have a proximity count of zero.
		 *
		 * @param row The row of the current cell.
		 * @param col The column of the current cell.
		 */
		private void showAdjacentCells(int row, int col) {
			// Check cells above
			if(showCell(row - 1, col))showAdjacentCells(row - 1, col);
			if(showCell(row - 1, col - 1))showAdjacentCells(row - 1, col - 1);
			if(showCell(row - 1, col + 1))showAdjacentCells(row - 1, col + 1);

			// Check cells adjacent
			if(showCell(row, col - 1))showAdjacentCells(row, col - 1);
			if(showCell(row, col + 1))showAdjacentCells(row, col + 1);

			// Check cells below
			if(showCell(row + 1, col))showAdjacentCells(row + 1, col);
			if(showCell(row + 1, col - 1))showAdjacentCells(row + 1, col - 1);
			if(showCell(row + 1, col + 1))showAdjacentCells(row + 1, col + 1);
		}

		private void displayPlayAgainMessage(String message, String title) {
			int prompt = JOptionPane.showConfirmDialog(null, message + " Play Again?", title, JOptionPane.YES_NO_OPTION);
			if(prompt == JOptionPane.YES_OPTION)populateBoard();
			else GameFrame.this.dispose();
		}

		private ImageIcon getBombImage(int width, int height) {
			ImageIcon bomb = new ImageIcon("images/bomb.jpg");
			return new ImageIcon(bomb.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
		}

		/**
		 * Determines a cell's color based on its proximity to a bomb.
		 *
		 * @param num The cell's proximity count.
		 * @return A color.
		 */
		private Color getTileColor(int num) {
			Color color;
			switch (num) {
				default -> color = Color.RED;
				case 0 -> color = Color.CYAN;
				case 1 -> color = Color.GREEN;
				case 2 -> color = Color.YELLOW;
				case 3 -> color = Color.ORANGE;
			}
			return color;
		}

		/**
		 * Displays bomb grid within the console.
		 */
		private void displayBombsCheatSheet() {
			boolean[][] bombCounts = grid.getBombGrid();
			for(int row = 0; row < bombCounts.length; row++) {
				System.out.print("| ");
				for(int col = 0; col < bombCounts[row].length; col++) {
					boolean bomb = bombCounts[row][col];
					char c = 'C';
					if(bomb)c ='X';
					System.out.print(c + " | ");
				}
				System.out.println();
			}
		}

		/**
		 * Displays proximity count for all cells within the console.
		 */
		private void displayCountsCheatSheet() {
			int[][] cellCounts = grid.getCountGrid();
			for(int row = 0; row < cellCounts.length; row++) {
				System.out.print("| ");
				for(int col = 0; col < cellCounts[row].length; col++) {
					System.out.print(cellCounts[row][col] + " | ");
				}
				System.out.println();
			}
		}
	}
}
