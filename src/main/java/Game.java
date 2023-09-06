package main.java;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Game extends Controls {
  private final String ANSI_RESET = "\u001b[0m";
  private final String ANSI_BG_DEFAULT_COLOR = "\u001b[49m";
  private final String PIXEL = "  "; // In reality, a pixel is composed of two spaces and the background is then colored using ANSI
  private final int PIXEL_SIZE = PIXEL.length(); // we'll need this in the calculations of the movements

  /**
   * The number of pixels the player will jump upwards and downwards.
   */
  private final int JUMP_HEIGHT = 13;

  /**
   * The delay between each step of the jump.
   * A delay too low will make the jump look instantaneous or hard to follow.
   */
  private final int JUMP_DELAY_BETWEEN_EACH_FRAME = 80;
  
  /**
   * The minimal height, in characters, for the console so that the game can be played normally.
   */
  private final int MINIMAL_GUI_HEIGHT = 50;

  /**
   * The minimal width, in semi-characters, for the console so that the game can be played normally.
   * We display each number from "00" up to this value so each number takes two characters (the size of a pixel).
   * TODO: we might have to use as many characters as there are in `PIXEL_SIZE` to make changing `PIXEL` possible
   */
  private final int MINIMAL_GUI_WIDTH = 35;

  private final String COLORS_PATH = "assets/0-colors.csv";
  private final String PLAYER_DEFAULT_SKIN = "assets/skins/amongus.csv";
  private final String MAPS_DIRECTORY = "assets/maps";
  private final String OBSTACLES_DIRECTORY = "assets/obstacles";
  private final String CONFIGS_DIRECTORY = "assets/map-configs";

  /**
   * The number of pixels on the Y-axis between the top of the map and the floor.
   * It must be the same on all maps, hence this constant.
   */
  private final int MAP_DISTANCE_UNTIL_FLOOR = 32;

  private final int JUMP_KEY = 32;
  private final int TOP_ARROW_KEY = 17;
  private final int BOTTOM_ARROW_KEY = 18;
  // private final int RIGHT_ARROW_KEY = 19;
  // private final int LEFT_ARROW_KEY = 20;
  private final int ENTER_KEY = 13;

  /**
   * The player's position on the X-axis in the map.
   * In theory, it should always be the same.
   */
  private int playerX = 2; // ! MUST BE DIVISIBLE BY `PIXEL_SIZE` AND > 0 !

  /**
   * The player's position on the Y-axis in the map.
   * This position is within the map itself, so y=0 means the top of the map, not the top of the GUI.
   * By default, the player needs to be placed on the floor.
   */
  private int playerY = MAP_DISTANCE_UNTIL_FLOOR;

  private ArrayList<Color> allColors = new ArrayList<>();
  private HashMap<String, Map> allMaps = new HashMap<>();
  private HashMap<String, Obstacle> allObstacles = new HashMap<>();
  private HashMap<String, MapSpawnConfig> allConfigs = new HashMap<>(); // where, when and how fast the obstacles spawn for each map
  private ArrayList<ArrayList<Integer>> playerCurrentMatrix = new ArrayList<>();

  /**
   * Since we don't want the main thread to terminate too soon,
   * as long as we're waiting for user inputs, we'll put it to sleep.
   * Terminate this sleep by setting this variable to `true`.
   */
  private boolean gameFinished = false;

  /**
   * Can the player jump? By default, it is `true`.
   * It's necessary to make sure that the player doesn't double-jump.
   */
  private boolean canJump = true;

  /**
   * The name of the current map.
   */
  private String currentMapName = "desert";

  /**
   * The current menu being displayed to the user.
   * This variable is useful to detect what choice the user made
   * when pressing the Enter key.
   * 
   * Since the only page that doesn't have menus is the game itself,
   * then if this is `null` it means the user's playing the game.
   */
  private GameMenu currentMenu = null;

  /**
   * Starts the game.
   * This function blocks the main thread.
   * When this function stops, it means the game ended.
   */
  public void start() {
    currentMenu = new MainMenu(); // the player starts with the main menu
    enableKeyTypedInConsole(true);

    println("Chargement...");

    initializeColors();
    initializeAllMaps();
    initializeAllObstacles();
    initializeAllConfigs();

    clearMyScreen();
    currentMenu.display();

    while (!gameFinished) {
      sleep(100);
    }
    println("Game was terminated.");
    enableKeyTypedInConsole(false);
  }

  /**
   * Spawns the objects for the given map and make them move.
   * Two separate threads are created during this process.
   * 
   * 1. Wait `delta` seconds and then start the second thread
   * 2. Move the object until it reaches the beginning of the map.
   * 
   * TODO: this code doesn't work if the map is not at (0;0)
   */
  public void startSpawningObjects() {
    for (ObstacleSpawn spawn : allConfigs.get(currentMapName).getSpawns()) {
      Obstacle obstacle = allObstacles.get(spawn.getName());
      int[] mapDimensions = allMaps.get(currentMapName).getMatrixDimensions();
      int[] obstacleDimensions = obstacle.getMatrixDimensions();
      int posX = (mapDimensions[0] - obstacleDimensions[0]) * PIXEL_SIZE;
      int posY = spawn.getY();
      Thread movementThread = new Thread() {
        public void run() {
          int maxX = obstacleDimensions[0];
          int x = posX;
          while (x > maxX) {
            if (!canJump) {
              // Stop showing the obstacles when the player is jumping
              try {
                x--;
                Thread.sleep((long)spawn.getSpeed());
                continue;
              } catch (InterruptedException ignore) {}
            }
            // just to make sure this thread gets the word
            if (gameFinished) {
              break;
            }
            try {
              removeElementFromForeground(obstacle.getMatrix(), x, posY, x, posY);
              x--;
              displayMatrix(obstacle.getMatrix(), true, x, posY, x, posY);
              Thread.sleep((long)spawn.getSpeed());
            } catch (InterruptedException ignore) { }
            removeElementFromForeground(obstacle.getMatrix(), x, posY, x, posY);
          }
        }
      };
      // Start the movement thread after its own specific delay (called `delta`).
      new Thread() {
        public void run() {
          try {
            Thread.sleep((long)(spawn.getDelta() * 1000));
            movementThread.start();
          } catch (InterruptedException ignore) { }
        }
      }.start();
    }
  }

  /**
   * Makes the selector go up in the menu.
   */
  private void selectMenuUp() {
    if (GameMenu.canGoUp()){
      saveCursorPosition();
      moveCursorTo(GameMenu.LEFT_X, GameMenu.current_selector_y);
      System.out.print(" ");
      restoreCursorPosition();
      GameMenu.current_selector_y--;
      saveCursorPosition();
      moveCursorTo(GameMenu.LEFT_X, GameMenu.current_selector_y);
      System.out.print(">");    
      restoreCursorPosition();
    }
  }

  /**
   * Makes the selector go down in the menu.
   */
  private void selectMenuDown() {
    if (GameMenu.canGoDown()) {
      saveCursorPosition();
      moveCursorTo(GameMenu.LEFT_X, GameMenu.current_selector_y);
      System.out.print(" ");
      restoreCursorPosition();
      GameMenu.current_selector_y++;
      saveCursorPosition();
      moveCursorTo(GameMenu.LEFT_X, GameMenu.current_selector_y);
      System.out.print(">");
      restoreCursorPosition();
    }
  }

  /**
   * Enter the selected option in the main menu.
   * 
   * Verify what page is the user,
   * then verify what's the selected option using
   * the Y position of the selector.
   */
  private void select() {
    Page selectedPage = currentMenu.getSelectedPage();

    clearMyScreen();

    if (selectedPage.isMap()) {
      currentMenu = null;
      currentMapName = selectedPage.getMapName();
      setPlayerSkin(PLAYER_DEFAULT_SKIN);
      displayMap(currentMapName);
      saveCursorPosition();
      displayPlayer();
      restoreCursorPosition();
    } else {
      switch (selectedPage) {
        case NORMAL_MODE:
          (currentMenu = new UnknownMenu("Mais tu crois j'ai le temps de coder ça!?\r\nJ'ai besoin de dormir aussi ;(\r\nPar contre on a codé le mode Arcade! Allez zou.")).display();
          return;
        case ARCADE_MODE:
          (currentMenu = new MapSelectionMenu()).display();
          return;
        case CHECK_SCREEN:
          (currentMenu = new ScreenCheckMenu(MINIMAL_GUI_HEIGHT, MINIMAL_GUI_WIDTH, PIXEL_SIZE)).display();
          return;
        case CREDITS:
          (currentMenu = new CreditsMenu()).display();
          return;
        default:
          (currentMenu = new UnknownMenu()).display();
      }
    }
  }

  @Override
  protected void keyTypedInConsole(int keyCode) {
    if (currentMenu != null && currentMenu.hasChoices()) {
      switch (keyCode) {
        case TOP_ARROW_KEY:
          selectMenuUp();
          return;
        case BOTTOM_ARROW_KEY:
          selectMenuDown();
          return;
        case ENTER_KEY:
          select();
          return;
      }
    } else if (currentMenu == null) { // meaing the player is on a map
      if (keyCode == JUMP_KEY) {
        jump();
        return;
      }
    }
    if (keyCode == (int)'q') {
      if (currentMenu instanceof MainMenu) {
        gameFinished = true; // we stop the main loop by setting this to `true`
      } else {
        clearMyScreen();
        currentMenu = new MainMenu();
        currentMenu.display();
      }
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
        String mapName = map.substring(0, map.length()-4);
        allMaps.put(mapName, new Map(mapName, readMatrix(reader)));
      } catch (Exception ignore) {}
    }
  }

  /**
   * Reads all obstacles contained in `OBSTACLES_DIRECTORY`.
   */
  private void initializeAllObstacles() {
    String[] obstacles = Utils.getAllFilesFromDirectory(OBSTACLES_DIRECTORY);

    for (String obstacle : obstacles) {
      try (BufferedReader reader = new BufferedReader(new FileReader(OBSTACLES_DIRECTORY + "/" + obstacle))) {
        String obstacleName = obstacle.substring(0, obstacle.length()-4);
        allObstacles.put(obstacleName, new Obstacle(obstacleName, readMatrix(reader)));
      } catch (Exception ignore) {}
    }
  }
  
  /**
   * Reads all the maps' unique config.
   * A map config contains where, when and how fast its obstacles spawn.
   */
  private void initializeAllConfigs() {
    String[] configs = Utils.getAllFilesFromDirectory(CONFIGS_DIRECTORY);

    for (String config : configs) {
      String mapName = config.substring(0, config.indexOf("-"));
      allConfigs.put(mapName, MapSpawnConfig.fromCSV(CONFIGS_DIRECTORY + "/" + config));
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
  private void displayMap(String mapName) {
    ArrayList<ArrayList<Integer>> grid = getMapMatrix(mapName);
    displayMatrix(grid, false, -1, -1, -1, -1);
  }

  /**
   * Gets the matrix of a map.
   * @param index The unique index of this map.
   * @return The grid (a list of lists of integers where each integer is a color).
   */
  private ArrayList<ArrayList<Integer>> getMapMatrix(String mapName) {
    return allMaps.get(mapName).getMatrix();
  }

  /**
   * Displays a matrix of colors (an image) at either the background or the foreground.
   * If the image must be drawn on the foreground,
   * then instead of drawing transparent pixels
   * that would take the same color as the console,
   * we paint the corresponding pixel of the background.
   * 
   * Choose at what coordinates to start drawing the image.
   * Use -1 so as not to change the cursor from its current position.
   * 
   * <b>Note that a foreground element must be given precise coordinates.</b>
   * @param matrix The matrix of an obstacle, a map or the player.
   * @param foreground Is the element on the foreground or the background?
   * @param cursorX The X-coordinate at which to start drawing the image.
   * @param cursorY The Y-coordinate at which to start drawing the image.
   * @param objectX The X-coordinate of the object within the map itself.
   * @param objectY The Y-coordinate of the object within the map itself.
   */
  private void displayMatrix(ArrayList<ArrayList<Integer>> matrix, boolean foreground, int cursorX, int cursorY, int objectX, int objectY) {
    boolean useCoordinates = cursorX != 1 && cursorY != 1 && objectX != -1 && objectY != -1;
    if (useCoordinates) {
      moveCursorTo(cursorX, cursorY);
    }
    int mapHeight = matrix.size();
    int mapWidth = matrix.get(0).size();
    for (int lig = 0; lig < mapHeight; lig++) {
      for (int col = 0; col < mapWidth; col++) {
        int n = matrix.get(lig).get(col);
        if (n == -1) {
          if (foreground) {
            int colorIndexOfBehind = getMapMatrix(currentMapName).get(objectY + lig - PIXEL_SIZE).get(objectX / PIXEL_SIZE + col);
            if (colorIndexOfBehind == -1) {
              printTransparentPixel();
            } else {
              printPixel(allColors.get(colorIndexOfBehind));
            }
          } else {
            printTransparentPixel();
          }
        } else {
          printPixel(allColors.get(n));
        }
      }
      println(""); // jump a line
      if (useCoordinates) {
        moveCursorTo(cursorX, ++cursorY); // replaces the cursor on the same X shift, and one line below the previous one
      }
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

  /**
   * Places the player on the map at the exact player's coordinates.
   */
  private void displayPlayer() {
    displayMatrix(playerCurrentMatrix, true, getPlayerAbsoluteX(), getPlayerAbsoluteY(), playerX, playerY);
  }

  /**
   * Gets the actual X coordinate of the player in the screen.
   * @return The current X coordinate.
   */
  private int getPlayerAbsoluteX() {
    return playerX + 1;
  }

  /**
   * Gets the actual Y coordinate of the player in the screen.
   * @return The current Y coordinate of the player.
   */
  private int getPlayerAbsoluteY() {
    return playerY;
  }

  /**
   * Removes the player from the foreground.
   * It replaces the pixels by those that should be "behind" the player.
   */
  private void removePlayerFromScreen() {
    int absX = getPlayerAbsoluteX();
    int absY = getPlayerAbsoluteY(); // it will get incremented as we remove the player's pixels line by line
    removeElementFromForeground(playerCurrentMatrix, absX, absY, playerX, playerY);
  }

  /**
   * Removes an element from the foreground.
   * This way, the element shall not be replaced with transparent pixels,
   * but rather the pixels that should be "behind" the element itself,
   * from the current background's matrix.
   * @param matrix The element's matrix to be removed
   * @param absX The X position where to place the cursor.
   * @param absY The Y position where to place the cursor.
   * @param x The X position of the element in the map.
   * @param y The Y position of the element in the map.
   */
  private void removeElementFromForeground(ArrayList<ArrayList<Integer>> matrix, int absX, int absY, int x, int y) {
    moveCursorTo(absX, absY);
    ArrayList<ArrayList<Integer>> background = getMapMatrix(currentMapName);
    int elementHeight = matrix.size();
    int elementWidth = matrix.get(0).size();
    for (int line = 0; line < elementHeight; line++) {
      for (int col = 0; col < elementWidth; col++) {
        int colorIndex = background.get(y + line - PIXEL_SIZE).get(x / PIXEL_SIZE + col);
        if (colorIndex == -1) {
          printTransparentPixel();
        } else {
          printPixel(allColors.get(colorIndex));
        }
      }
      println("");
      moveCursorTo(absX, ++absY);
    }
  }

  /**
   * Makes the player jump.
   * @param step
   */
  private void jump() {
    if (!canJump) {
      return;
    }
    canJump = false;
    /**
     * So as not to interrupt the normal game execution when jumping,
     * we execute the code responsible of making the player jump in another thread.
     * This way, we can do other actions while jumping (like quitting the game or moving the obstacles).
     */
    Thread jumpThread = new Thread() {
      public void run() {
        saveCursorPosition();
        try {
          // going up
          for (int i = 0; i < JUMP_HEIGHT; i++) {
            removePlayerFromScreen();
            playerY -= 1;
            displayPlayer();
            Thread.sleep(JUMP_DELAY_BETWEEN_EACH_FRAME);
          }
          // going down
          for (int i = 0; i < JUMP_HEIGHT; i++) {
            removePlayerFromScreen();
            playerY += 1;
            displayPlayer();
            Thread.sleep(JUMP_DELAY_BETWEEN_EACH_FRAME);
          }
          canJump = true;
          restoreCursorPosition();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    };
    jumpThread.start();
  }

  public static void main(String[] args) {
    Game game = new Game();
    game.start(); 
  }
}
