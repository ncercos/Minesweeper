import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * Written by Nicholas Cercos
 */
public class Grid {

	private final boolean[][] bombGrid;
	private final int[][] countGrid;
	private final int numRows;
	private final int numColumns;
	private final int numBombs;

	public Grid(int numRows, int numColumns, int numBombs) {
		this.bombGrid = new boolean[numRows][numColumns];
		this.countGrid = new int[numRows][numColumns];
		this.numRows = numRows;
		this.numColumns = numColumns;
		this.numBombs = numBombs;
		createBombGrid();
		createCountGrid();
	}

	/**
	 * Determines the locations of all bombs.
	 */
	public void createBombGrid() {
		List<Boolean> bombs = new ArrayList<>();
		for(int i = 0; i < numBombs; i++) bombs.add(true);
		for(int i = 0; i < (numRows * numColumns) - numBombs; i++) bombs.add(false);
		Collections.shuffle(bombs);

		int index = 0;
		for(int row = 0; row < bombGrid.length; row++) {
			for(int col = 0; col < bombGrid[row].length; col++) {
				bombGrid[row][col] = bombs.get(index);
				index++;
			}
		}
	}

	/**
	 * Determines all cells' proximity to a bomb.
	 */
	public void createCountGrid() {
		for(int row = 0; row < bombGrid.length; row++) {
			for(int col = 0; col < bombGrid[row].length; col++)
				countGrid[row][col] = getCountAtLocation(row, col);
		}
	}

	/**
	 * Get a specific proximity count for a cell.
	 *
	 * @param row    The row of the cell.
	 * @param column The column of the cell.
	 * @return A number that determines the cell's proximity to bombs.
	 */
	public int getCountAtLocation(int row, int column) {
		int count = 0;
		boolean isAwayFromLeftWall = column > 0;
		boolean isAwayFromRightWall = column < (numColumns - 1);
		if(isBombAtLocation(row, column))count++;

		// Check for bombs above
		if(row > 0) {
			if(isBombAtLocation(row - 1, column))
				count++; // Above

			if(isAwayFromLeftWall)
				if(isBombAtLocation(row - 1, column - 1))
					count++; // Top Left

			if(isAwayFromRightWall)
				if(isBombAtLocation(row - 1, column + 1))
					count++; // Top Right
		}

		// Check for bombs adjacent
		if(isAwayFromLeftWall)
			if(isBombAtLocation(row, column - 1)) {
				count++; // Left
			}

		if(isAwayFromRightWall)
			if(isBombAtLocation(row, column + 1)) {
				count++; // Right
			}

		// Check for bombs underneath
		if(row < (numRows - 1)) {
			if(isBombAtLocation(row + 1, column))
				count++; // Below

			if(isAwayFromLeftWall)
				if(isBombAtLocation(row + 1, column - 1))
					count++; // Bottom Left

			if(isAwayFromRightWall)
				if(isBombAtLocation(row + 1, column + 1))
					count++; // Bottom Right
		}

		return count;
	}

	/**
	 * Determines if a cell contains a bomb.
	 *
	 * @param row    The row of the cell.
	 * @param column The column of the cell.
	 * @return True if there is a bomb at the given location.
	 */
	public boolean isBombAtLocation(int row, int column) {
		return bombGrid[row][column];
	}

	/**
	 * Returns a copy of the bomb grid, representing the placement of bombs
	 * in the grid. Each cell in the returned array is true if there is a bomb
	 * in that location, and false otherwise.
	 *
	 * @return A 2D boolean array representing the locations of bombs in the grid.
	 */
	public boolean[][] getBombGrid() {
		boolean[][] bombGridCopy = new boolean[numRows][numColumns];
		for(int row = 0; row < bombGridCopy.length; row++)
			System.arraycopy(bombGrid[row], 0, bombGridCopy[row], 0, bombGridCopy[row].length);
		return bombGridCopy;
	}

	/**
	 * Returns a copy of the count grid, representing the proximity counts of each cell
	 * in the grid to the bombs. Each value in the array indicates the number of bombs
	 * adjacent to or within the specified cell.
	 *
	 * @return A 2D array of integers where each value represents the proximity count
	 *         of a cell in the grid.
	 */
	public int[][] getCountGrid() {
		int[][] countGridCopy = new int[numRows][numColumns];
		for(int row = 0; row < countGridCopy.length; row++)
			System.arraycopy(countGrid[row], 0, countGridCopy[row], 0, countGridCopy[row].length);
		return countGridCopy;
	}
}
