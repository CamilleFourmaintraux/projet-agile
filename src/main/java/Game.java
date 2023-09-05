package main.java;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class Game extends Controls {
  private final String ANSI_RESET = "\u001b[0m";
  private final String ANSI_BG_DEFAULT_COLOR = "\u001b[49m";
  private final String PIXEL = "  "; // In reality, a pixel is composed of two spaces and the background is then colored using ANSI
  // private final int PIXEL_SIZE = PIXEL.length(); // we'll need this in the calculations of the movements

  private final String COLORS_PATH = "assets/0-colors.csv";
  private final String PLAYER_DEFAULT_SKIN = "assets/player.csv";
  private final String MAPS_DIRECTORY = "assets/maps";

  private final int JUMP_KEY = 32;
  // private final int TOP_ARROW_KEY = 17;
  // private final int BOTTOM_ARROW_KEY = 18;
  // private final int RIGHT_ARROW_KEY = 19;
  // private final int LEFT_ARROW_KEY = 20;

  // private int playerX = 0;
  // private int playerY = 0;
  private ArrayList<Color> allColors = new ArrayList<>();
  private ArrayList<Map> allMaps = new ArrayList<>();
  private ArrayList<ArrayList<Integer>> playerCurrentMatrix = new ArrayList<>();

  /**
   * Since we don't want the main thread to terminate too soon,
   * as long as we're waiting for user inputs, we'll put it to sleep.
   * Terminate this sleep by setting this variable to `true`.
   */
  private boolean gameFinished = false;

  public void start() {
    enableKeyTypedInConsole(true);
    initializeColors();
    initializeAllMaps();
    setPlayerSkin(PLAYER_DEFAULT_SKIN);
    displayMap(allMaps.get(0));
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
        println("Jump key pressed");
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
   * Each integer is the index of a color in the pallet.
   */
  private void initializeAllMaps() {
    String[] maps = Utils.getAllFilesFromDirectory(MAPS_DIRECTORY);

    for (String map : maps) {
      try (BufferedReader reader = new BufferedReader(new FileReader(MAPS_DIRECTORY + "/" + map))) {
        ArrayList<ArrayList<Integer>> grid = new ArrayList<>();
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
        allMaps.add(new Map(map.substring(0, map.length()-4), grid));
      } catch (Exception ignore) {}
    }
  }

  // private ArrayList<ArrayList<Integer>> readMatrix(BufferedReader reader) {
  //   ArrayList<ArrayList<Integer>> grid = new ArrayList<>();
  // }

  /**
   * Initializes the skin of the player.
   */
  private void setPlayerSkin(String skin) {
    playerCurrentMatrix.clear();

    try (BufferedReader reader = new BufferedReader(new FileReader(skin))) {
      // TODO
    } catch (Exception ignore) { }
  }

  /**
   * Displays a map onto the console.
   * @param map The map and its matrix.
   */
  private void displayMap(Map map) {
    ArrayList<ArrayList<Integer>> grid = map.getGrid();
    int mapHeight = grid.size();
    int mapWidth = grid.get(0).size();
    for (int lig = 0; lig < mapHeight; lig++) {
      for (int col = 0; col < mapWidth; col++) {
        int n = grid.get(lig).get(col);
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

  public static void main(String[] args) {
    Game game = new Game();
    game.start();
  }
}
