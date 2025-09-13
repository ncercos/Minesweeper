
<p align="center">
  <a href="https://youtu.be/deQg4iv0wBo?si=S4ay1qUOPT6CY73T" target="_blank" rel="noreferrer"><img src="https://i.imgur.com/JPns3Pn.png" alt="Minesweeper"></a>
</p>


Dive into the classic fun of **[Minesweeper](https://youtu.be/deQg4iv0wBo?si=A2UHGcFiA7-8lVqB)**, crafted from scratch in Java! Using the Swing library, this game brings a vibrant, interactive twist to your screen. With each click, discover hidden cells rendered with JButtons, all while strategically avoiding concealed mines. Can you clear the board and avoid the explosive surprises lurking beneath the surface? Test your wit and reflexes in this engaging challenge!  
&nbsp;

<p align="center">
  <a href="https://i.imgur.com/IeVRZth.gif">
    <img src="https://i.imgur.com/IeVRZth.gif" alt="Minesweeper Demo">
  </a>
</p>

## 🎮 How to Play

**Objective**: Reveal all safe cells while avoiding hidden bombs. Use numbers as clues - they indicate how many bombs are in the adjacent cells (including diagonals).

- `Left Mouse Button` - Reveal cell
- `Input Dialog` - Set custom board size and bomb count at startup
- `Yes/No Dialog` - Choose to play again after game ends
_Tip: Look for patterns! When a cell shows '0', it means there are no bombs in any of the adjacent cells, and they will be automatically revealed for you._
&nbsp;

## ⚙️ Technologies Used

-   **Java** - Core programming language
-   **Java Swing (javax.swing)** - For GUI components (JFrame, JPanel, JButton)
-   **Java AWT (java.awt)** - For graphics, events, and layouts
&nbsp;

## ✨ Key Features

### 1. Dynamic Game Board Generation

Customizable board size and bomb count through interactive player input. The game adapts to player preferences for difficulty.  
&nbsp;

**How it works**: When initializing, the game prompts for board dimensions and bomb count.

```java
public static int getPlayerInput(String message) {  
  String input = promptPlayerInput(message);  
  
  if (input == null) {  
  System.exit(0);  
  return 0;  
 }  
  while(!isInt(input) || Integer.parseInt(input) < 1)  
  input = promptPlayerInput(message);  
  return Integer.parseInt(input);  
}  
  
private static String promptPlayerInput(String message) {  
  return JOptionPane.showInputDialog(message);  
}
```

### 2. Intelligent Cell Revealing System

Smart cell revealing mechanism that automatically expands empty regions when clicked.  
&nbsp;

**How it works**: When a cell with no adjacent bombs is clicked, it recursively reveals neighboring cells.

```java
private boolean showCell(int row, int col) {  
  if(row < 0 || col < 0) return false;  
  if(row > (MAX_ROWS - 1) || col > (MAX_COLUMNS - 1)) return false;  
  if(grid.isBombAtLocation(row, col)) return false;  
  JButton button = buttons[row][col];  
  if(!button.isContentAreaFilled()) return false;  
  int count = grid.getCountAtLocation(row, col);  
  // ...cell clicked logic...
  return count == 0;  
}

private void showAdjacentCells(int row, int col) {  
  /* Above */
  if(showCell(row - 1, col)) showAdjacentCells(row - 1, col);  
  if(showCell(row - 1, col - 1)) showAdjacentCells(row - 1, col - 1);  
  if(showCell(row - 1, col + 1)) showAdjacentCells(row - 1, col + 1);  
  
  /* Adjacent */ 
  if(showCell(row, col - 1)) showAdjacentCells(row, col - 1);  
  if(showCell(row, col + 1)) showAdjacentCells(row, col + 1);  
  
  /* Below */
  if(showCell(row + 1, col)) showAdjacentCells(row + 1, col);  
  if(showCell(row + 1, col - 1)) showAdjacentCells(row + 1, col - 1);  
  if(showCell(row + 1, col + 1)) showAdjacentCells(row + 1, col + 1);  
}
```

### 3. Color-Coded Proximity Indicators

Visual feedback system using different colors to indicate the number of adjacent bombs.  
&nbsp;

**How it works**: Each cell is assigned a color based on its proximity to bombs.

```java
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
```

### 4. Random Bomb Placement

Simple but effective system for randomly distributing bombs across the game board while ensuring fair gameplay.  
&nbsp;

**How it works**: Shuffles a list of bomb and empty cell markers before mapping them to the game grid.

```java
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
```

## 📜 License

This project is licensed under the **MIT License**.  
