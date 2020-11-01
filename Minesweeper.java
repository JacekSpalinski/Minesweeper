/* *****************************************************************************
 *  Name:              Ada Lovelace
 *  Coursera User ID:  123456
 *  Last modified:     October 16, 1842
 **************************************************************************** */

import java.util.Random;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;

public class Minesweeper {

    // randomly assigns bombs to cells in the grid
    public static void assignMines(boolean[][] grid, int n, int x, int y) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < 81; i++) {
            list.add(Integer.valueOf(i));
        }
        Collections.shuffle(list);
        int mines = 0;
        int cell = 0;
        while (mines < n) {
            int row = list.get(cell) / 9;
            int col = list.get(cell) % 9;
            // first selected cell cannot be mine
            if (x != row || y != col) {
                grid[row][col] = true;
                mines++;
            }
            cell++;
        }
    }

    // checks for number of bombs on nearby cells and returns grid displaying that
    public static String[][] checkMines(boolean[][] grid) {
        // helping grid that would account for boundary cases
        boolean[][] helpGrid = new boolean[11][11];
        for (int i = 1; i < 10; i++) {
            for (int j = 1; j < 10; j++) {
                helpGrid[i][j] = grid[i - 1][j - 1];
            }
        }

        // grid to count bombs nearby
        int[][] countGrid = new int[11][11];
        for (int i = 1; i < 10; i++) {
            for (int j = 1; j < 10; j++) {
                // if cells doesn't contain a bomb, check all nearby cells
                if (!helpGrid[i][j]) {
                    if (helpGrid[i - 1][j - 1]) countGrid[i][j]++;
                    if (helpGrid[i - 1][j]) countGrid[i][j]++;
                    if (helpGrid[i - 1][j + 1]) countGrid[i][j]++;
                    if (helpGrid[i][j + 1]) countGrid[i][j]++;
                    if (helpGrid[i + 1][j + 1]) countGrid[i][j]++;
                    if (helpGrid[i + 1][j]) countGrid[i][j]++;
                    if (helpGrid[i + 1][j - 1]) countGrid[i][j]++;
                    if (helpGrid[i][j - 1]) countGrid[i][j]++;
                }
            }
        }

        // final grid displaying the field
        String[][] finalGrid = new String[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                // if countGrid shows no bombs (=0), it might be because there are
                // actually no bombs, or the cell itself is a bomb
                // if it is a bomb assign "X", else "."
                if (countGrid[i + 1][j + 1] == 0 && helpGrid[i + 1][j + 1]) {
                    finalGrid[i][j] = "X";
                }
                else if (countGrid[i + 1][j + 1] == 0 && !helpGrid[i + 1][j + 1]) {
                    finalGrid[i][j] = ".";
                }
                // if there are bombs nearby, just assign the number of bombs
                else {
                    finalGrid[i][j] = Integer.toString(countGrid[i + 1][j + 1]);
                }
            }
        }
        return finalGrid;
    }

    // prints grid with coordinates
    public static void printGrid(String[][] grid) {
        System.out.println(" │123456789│");
        System.out.println("-│---------│");
        for (int i = 0; i < 9; i++) {
            System.out.print(i+1 + "│");
            for (int j = 0; j < 9; j++) {
                System.out.print(grid[i][j]);
            }
            System.out.print("|");
            System.out.println();
        }
        System.out.println("-│---------│");
    }

    // prompts user for coordinates and updates the game until it is finished
    public static void updateGame() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("How many mines do you want on the field? ");
        int n = scanner.nextInt();

        // creates 9x9 boolean grid, where false means no bomb
        boolean[][] grid = new boolean[9][9];

        // created initial grid to print with all cells empty
        String[][] gridToPrint = new String[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                gridToPrint[i][j] = ".";
            }
        }

        // Prints initial empty grid
        printGrid(gridToPrint);

        // First checked cell cannot must be empty, that's why mines are assinged after this step
        System.out.println("Set/unset mines marks or claim a cell as free: ");
        int a = scanner.nextInt() - 1;
        int b = scanner.nextInt() - 1;
        scanner.next();

        // assign randomly mines to boolean grid and create auxiliary grid2 to keep track of # of mines at nearby cells
        assignMines(grid, n, b, a);
        String[][] grid2;
        grid2 = checkMines(grid);
        // printGrid(grid2);
        updateGridToPrint(grid2, gridToPrint, b, a);
        printGrid(gridToPrint);

        // main game loop, until game is not finished it would prompt player for input
        boolean gameEnded = false;
        int selectedMines = 0;
        while (!gameEnded) {
            System.out.println("Already selected mines: " + selectedMines);
            System.out.println("Set/unset mines marks or claim a cell as free: ");
            int y = scanner.nextInt() - 1;
            int x = scanner.nextInt() - 1;
            String mode = scanner.next();

            if (gridToPrint[x][y].matches("[1-9]")) {
                System.out.println("There is a number here!");
            }
            else if ("mine".equals(mode)) {
                if (gridToPrint[x][y].equals("*")) {
                    gridToPrint[x][y] = ".";
                    printGrid(gridToPrint);
                    selectedMines--;
                }
                else {
                    gridToPrint[x][y] = "*";
                    printGrid(gridToPrint);
                    gameEnded = checkGameState1(grid, gridToPrint);
                    selectedMines++;
                }
            }
            else if ("free".equals(mode)) {
                // if player steps on a mine, it would reveal all the mines and end the game
                if (grid2[x][y].equals("X")) {
                    for (int i = 0; i < 9; i++) {
                        for (int j = 0; j < 9; j++) {
                            if (grid2[i][j].equals("X")) {
                                gridToPrint[i][j] = "X";
                            }
                        }
                    }
                    printGrid(gridToPrint);
                    System.out.println("You stepped on a mine and failed!");
                    break;
                }
                updateGridToPrint(grid2, gridToPrint, x, y);
                printGrid(gridToPrint);
                gameEnded = checkGameState2(grid, gridToPrint);
            }
            if (gameEnded) System.out.println("Congratulations! You found all mines!");
        }
    }

    // update gridToPrint with appropriate signs
    public static void updateGridToPrint (String[][] grid, String[][] gridToPrint, int x, int y) {
        if (grid[x][y].matches("[1-9]")) {
            gridToPrint[x][y] = grid[x][y];
        }
        else if (grid[x][y].equals(".")) {
            gridToPrint[x][y] = "/";
            exploreEmptyCells(grid, gridToPrint, x, y);
        }
    }

    // automatically reveales all cells without bombs nearby that are connected to cell chosen by the player
    public static void exploreEmptyCells(String[][] grid, String[][] gridToPrint, int x, int y) {
        if (grid[x][y].equals(".")) {
            for (int i = x - 1; i <= x + 1; i++) {
                if (i >= 0 && i < 9) {
                    for (int j = y - 1; j <= y + 1; j++) {
                        if (j >= 0 && j < 9) {
                            if (i!=x || j!=y) {
                                if (grid[i][j].matches("[1-9]")) {
                                    gridToPrint[i][j] = grid[i][j];
                                }
                                else if (!gridToPrint[i][j].equals("/")) {
                                    gridToPrint[i][j] = "/";
                                    exploreEmptyCells(grid, gridToPrint, i, j);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // checks if all mines are marked and if empty cells are not marked
    public static boolean checkGameState1(boolean[][] grid, String[][] gridToPrint) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (grid[i][j]) {
                    if (!gridToPrint[i][j].equals("*")) return false;
                }
                else {
                    if (gridToPrint[i][j].equals("*")) return false;
                }
            }
        }
        return true;
    }

    // checks 2nd way to win by checking all safe cells
    public static boolean checkGameState2(boolean[][] grid, String[][] gridToPrint) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (!grid[i][j]) {
                    if (gridToPrint[i][j].equals(".")) return false;
                }
            }
        }
        return true;
    }

    public static void main(String[] args) {
        updateGame();
    }
}
