import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

/*
 * Written by Nicholas Cercos
 */
public class Game extends JPanel implements GameBoard, ActionListener {

 private final JFrame frame;
 private JButton[][] buttons;
 private Grid grid;

 private final int MAX_ROWS;
 private final int MAX_COLUMNS;
 private final int MAX_BOMBS;
 private int numClicks, MAX_NUM_CLICKS;
 private boolean gameEnded = false;

 private final Font font;
 private final Insets insets;
 private final Dimension dimension;
 private final BufferedImage bombImg = Utils.loadSprite("bomb.jpg");

 public static void main(String[] args) {
  new Game();
 }

 public Game() {
  JPanel panel = new JPanel();
  panel.setPreferredSize(new Dimension(500, 500));
  panel.setFocusable(true);

  frame = new JFrame();
  frame.setTitle("Minesweeper");
  frame.setIconImage(bombImg);
  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

  panel.setLayout(new BorderLayout());

  MAX_ROWS = Utils.getPlayerInput("How many rows?");
  MAX_COLUMNS = Utils.getPlayerInput("How many columns?");
  MAX_BOMBS = Utils.getPlayerInput("How many bombs?");

  panel.add(this, BorderLayout.CENTER);

  frame.add(panel);
  frame.setResizable(false);
  frame.pack();
  frame.setLocationRelativeTo(null);
  frame.setVisible(true);

  setLayout(new GridLayout(MAX_ROWS, MAX_COLUMNS));

  this.font = new Font("Arial", Font.BOLD, 20);
  this.insets = new Insets(0,0,0,0);
  this.dimension = new Dimension(25,25);

  populateBoard();
 }

 /**
  * Handles the event triggered when a button is clicked. The method determines
  * which button was clicked, checks its state, and processes the game logic
  * accordingly. If a bomb is found, the game ends, and the bomb-related
  * actions are executed.
  *
  * @param e the ActionEvent that provides information about the button click,
  *          such as the source of the event.
  */
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

 /**
  * Initializes and populates the game board with buttons and their associated logic.
  * This method creates a new grid of buttons, associates event listeners with them,
  * and adds them to the game panel. It also resets the game's internal state,
  * recalculates the maximum number of allowed clicks, and ensures that all
  * UI components are properly initialized for a new game.
  *
  * The method performs the following actions:
  * 1. Initializes a new game grid with configured dimensions and bomb count.
  * 2. Recalculates the maximum number of clicks allowed before winning.
  * 3. Sets up the button array, assigning new buttons with default properties
  *    or reusing existing ones if the game has ended.
  * 4. Resets all button states, including enabling them, clearing text, icons,
  *    and styling.
  * 5. Displays cheat sheets for bomb locations and proximity counts in the console
  *    for debugging purposes.
  */
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

 /**
  * Handles the event when a specific cell (button) is clicked. It updates the button's
  * state based on whether the cell contains a bomb or proximity information. The method
  * also manages game logic such as checking if the game has been won or if adjacent
  * cells need to be revealed.
  *
  * @param button The button representing the cell that was clicked.
  * @param row    The row of the cell in the grid.
  * @param col    The column of the cell in the grid.
  * @return True if the clicked cell contains a bomb, false otherwise.
  */
 @Override
 public boolean cellClicked(JButton button, int row, int col) {
  numClicks++;
  button.setContentAreaFilled(false);
  if(grid.isBombAtLocation(row, col)) {
   button.setIcon(new ImageIcon(bombImg.getScaledInstance(button.getWidth(), button.getHeight(), Image.SCALE_SMOOTH)));
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

 /**
  * Handles the scenario when a bomb is found during gameplay. The method iterates
  * through all buttons on the game board, revealing their states by simulating a click
  * on each cell. For each cell, if the button is still in its default state (content
  * area filled), it triggers the associated game logic using the `cellClicked` method.
  * Once all cells are processed, the method displays a message to the player
  * indicating game loss.
  */
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

 /**
  * Checks if the win condition of the game has been met.
  * The method verifies if the required number of clicks has been reached
  * and if the game has not already ended. If the conditions are satisfied,
  * the game ends, and a message is displayed to indicate the win.
  *
  * The win condition is determined based on the comparison between the current
  * number of clicks and the maximum allowed clicks. If the win condition is met,
  * the game displays a dialog asking the player whether they want to play again.
  */
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

 /**
  * Displays a dialog prompting the user with a specified message and title,
  * asking if they want to play the game again. Based on the user's choice,
  * it either restarts the game by calling the populateBoard method or
  * closes the game window.
  *
  * @param message The message to be displayed in the dialog.
  * @param title   The title of the dialog window.
  */
 private void displayPlayAgainMessage(String message, String title) {
  int prompt = JOptionPane.showConfirmDialog(null, message + " Play Again?", title, JOptionPane.YES_NO_OPTION);
  if(prompt == JOptionPane.YES_OPTION)populateBoard();
  else frame.dispose();
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
  * Displays a bomb grid within the console.
  */
 private void displayBombsCheatSheet() {
  boolean[][] bombCounts = grid.getBombGrid();
  for(int row = 0; row < bombCounts.length; row++) {
   System.out.print("| ");
   for(int col = 0; col < bombCounts[row].length; col++) {
    boolean bomb = bombCounts[row][col];
    char o = 'O';
    if(bomb)o ='X';
    System.out.print(o + " | ");
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
