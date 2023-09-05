package main.java;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Game extends Controls {
  private final String ANSI_RESET = "\u001b[0m";
  private final String ANSI_BG_DEFAULT_COLOR = "\u001b[49m";
  private final String PIXEL = "  "; // In reality, a pixel is composed of two spaces and the background is then colored using ANSI
  // private final int PIXEL_SIZE = PIXEL.length(); // we'll need this in the calculations of the movements
  private final int JUMP_HEIGHT = 9;
  private final int JUMP_DELAY_BETWEEN_EACH_FRAME = 30;

  private final String COLORS_PATH = "assets/0-colors.csv";
  private final String PLAYER_DEFAULT_SKIN = "assets/skins/amongus.csv";
  private final String MAPS_DIRECTORY = "assets/maps/examples";

  /**
   * The number of pixels on the Y-axis between the top of the map and the floor.
   * It must be the same on all maps, hence this constant.
   */
  private final int MAP_DISTANCE_UNTIL_FLOOR = 14;

  private final int JUMP_KEY = 32;
  // private final int TOP_ARROW_KEY = 17;
  // private final int BOTTOM_ARROW_KEY = 18;
  // private final int RIGHT_ARROW_KEY = 19;
  // private final int LEFT_ARROW_KEY = 20;

  /**
   * The player's position on the X-axis in the map.
   * Should always be the same.
   */
  private int playerX = 0;

  /**
   * The player's position on the Y-axis in the map.
   * This position is within the map itself, so y=0 means the top of the map, not the top of the GUI.
   */
  private int playerY = 0;

  private ArrayList<Color> allColors = new ArrayList<>();
  private ArrayList<Map> allMaps = new ArrayList<>();
  private ArrayList<ArrayList<Integer>> playerCurrentMatrix = new ArrayList<>();

  /**
   * Since we don't want the main thread to terminate too soon,
   * as long as we're waiting for user inputs, we'll put it to sleep.
   * Terminate this sleep by setting this variable to `true`.
   */
  private boolean gameFinished = false;

  /**
   * Starts the game.
   * This function blocks the main thread.
   * When this function stops, it means the game ended.
   */
  public void start() {
    enableKeyTypedInConsole(true);
    initializeColors();
    initializeAllMaps();
    setPlayerSkin(PLAYER_DEFAULT_SKIN);
    displayMap(allMaps.get(0));
    saveCursorPosition();
    displayPlayer();
    restoreCursorPosition();
    println("Press 'q' to quit.");
    while (!gameFinished) {
      sleep(100);
    }
    println("Game was terminated.");
    enableKeyTypedInConsole(false);
  }

  @Override
  protected void keyTypedInConsole(int keyCode) {
    switch (keyCode) {
      case JUMP_KEY:
        jump();
        break;
      case 'q': // 'q' is a `char` and as such it is being translated into its integer form and it gets detected.
        gameFinished = true; // we stop the main loop by setting this to `true`
        break;
    }
  }

  /**
   * Reads a file containing all the colors and metadata associated with them.
   * Each color has one metadata called "x".
   * If "x" is set `true` then it means the user can walk on it.
   * For obstacles, this variable will be `false`.
   * 
   * This function will only get called once at game initialization.
   */
  private void initializeColors() {
    try (BufferedReader reader = new BufferedReader(new FileReader(COLORS_PATH))) {
      reader.readLine(); // voluntarily ignoring the header
      String line = "";
      while ((line = reader.readLine()) != null) {
        Scanner scanner = new Scanner(line).useDelimiter(",");
        int x = scanner.nextInt();
        int r = scanner.nextInt();
        int g = scanner.nextInt();
        int b = scanner.nextInt();
        allColors.add(new Color(Utils.RGBToANSI(new int[]{r,g,b}, true), x == 1));
        scanner.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Reads all maps contained in `MAPS_DIRECTORY`.
   * Each CSV file is a unique map, which is a matrix of integers.
   */
  private void initializeAllMaps() {
    String[] maps = Utils.getAllFilesFromDirectory(MAPS_DIRECTORY);

    for (String map : maps) {
      try (BufferedReader reader = new BufferedReader(new FileReader(MAPS_DIRECTORY + "/" + map))) {
        allMaps.add(new Map(map.substring(0, map.length()-4), readMatrix(reader)));
      } catch (Exception ignore) {}
    }
  }

  /**
   * Reads a matrix of integers (the grid of a colored element on the map).
   * Useful to get the style of an obstacle, a map and a player skin.
   * Each integer is the index of a color in the pallet.
   * 
   * Note that the header is ignored.
   * @param reader The reader for the CSV file containing the matrix.
   * @return 
   */
  private ArrayList<ArrayList<Integer>> readMatrix(BufferedReader reader) {
    ArrayList<ArrayList<Integer>> grid = new ArrayList<>();
    try {
      reader.readLine(); // voluntarily ignoring the header
      String line = "";
      while ((line = reader.readLine()) != null) {
        ArrayList<Integer> pixels = new ArrayList<>();
        Scanner scanner = new Scanner(line).useDelimiter(",");
        while (scanner.hasNext()) {
          pixels.add(scanner.nextInt());
        }
        grid.add(pixels);
        scanner.close();
      }
    } catch (IOException ignore) {}
    return grid;
  }

  /**
   * Initializes the skin of the player.
   */
  private void setPlayerSkin(String skin) {
    playerCurrentMatrix.clear();

    try (BufferedReader reader = new BufferedReader(new FileReader(skin))) {
      playerCurrentMatrix = readMatrix(reader);
    } catch (Exception ignore) { }
  }

  /**
   * Displays a map onto the console.
   * @param map The map and its matrix.
   */
  private void displayMap(Map map) {
    ArrayList<ArrayList<Integer>> grid = map.getGrid();
    displayMatrix(grid);
  }

  /**
   * Displays a matrix of colors (an image).
   * @param matrix The matrix of an obstacle, a map or the player.
   */
  private void displayMatrix(ArrayList<ArrayList<Integer>> matrix) {
    int mapHeight = matrix.size();
    int mapWidth = matrix.get(0).size();
    for (int lig = 0; lig < mapHeight; lig++) {
      for (int col = 0; col < mapWidth; col++) {
        int n = matrix.get(lig).get(col);
        if (n == -1) {
          printTransparentPixel();
        } else {
          printPixel(allColors.get(n));
        }
      }
      println(""); // jump a line
    }
  }

  /**
   * Displays a matrix at specific coordinates.
   * Useful to place the player and the objects.
   * It starts drawing the image from the top-left corner.
   * @param matrix The matrix to draw.
   * @param posX The X coordinate.
   * @param posY The Y coordinate.
   */
  private void displayMatrix(ArrayList<ArrayList<Integer>> matrix, int posX, int posY) {
    moveCursorTo(posX, posY);
    displayMatrix(matrix);
  }

  /**
   * Creates a colored pixel.
   * @param color The color to use for this pixel.
   */
  private void printPixel(Color color) {
    System.out.print(color.ANSI + PIXEL + ANSI_RESET);
  }

  /**
   * Adds an empty space whose background color is the same as the terminal.
   * The exact color of the console is unknown, but ANSI allows us to use a special character for this.
   */
  private void printTransparentPixel() {
    System.out.print(ANSI_BG_DEFAULT_COLOR + PIXEL + ANSI_RESET);
  }

  /**
   * Places the player on the map at the coordinates (`playerX`;`playerY`)
   */
  private void displayPlayer() {
    displayMatrix(playerCurrentMatrix, getPlayerAbsoluteX(), getPlayerAbsoluteY());
  }
  
  /**
   * Gets the actual X coordinate of the player in the screen.
   * @return
   */
  private int getPlayerAbsoluteX() {
    return playerX;
  }

  /**
   * Gets the actual Y coordinate of the player in the screen.
   * @return The current Y coordinate of the player + the distance between the top of the map and the floor.
   */
  private int getPlayerAbsoluteY() {
    return MAP_DISTANCE_UNTIL_FLOOR + playerY;
  }

  /**
   * Removes the player from the screen.
   */
  private void removePlayerFromScreen() {
    saveCursorPosition();
    moveCursorTo(getPlayerAbsoluteX(), getPlayerAbsoluteY());
    int playerHeight = playerCurrentMatrix.size();
    int playerWidth = playerCurrentMatrix.get(0).size();
    for (int line = 0; line < playerHeight; line++) {
      for (int col = 0; col < playerWidth; col++) {
        printTransparentPixel();
      }
      println("");
    }
    restoreCursorPosition();
  }

  /**
   * Makes the player jump.
   * @param step
   */
  private void jump() {
    // going up
    for (int i = 0; i < JUMP_HEIGHT; i++) {
      removePlayerFromScreen();
      playerY -= 1;
      displayPlayer();
      sleep(JUMP_DELAY_BETWEEN_EACH_FRAME);
    }
    // going down
    for (int i = 0; i < JUMP_HEIGHT; i++) {
      removePlayerFromScreen();
      playerY += 1;
      displayPlayer();
      sleep(JUMP_DELAY_BETWEEN_EACH_FRAME);
    }
  }

  public static void main(String[] args) {
    Game game = new Game();
    game.start();
  }
}
