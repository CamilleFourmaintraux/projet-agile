package main.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Game extends Controls {
  private final String ANSI_RESET = "\u001b[0m";
  private final String ANSI_BG_DEFAULT_COLOR = "\u001b[49m";
  private final String PIXEL = "  "; // In reality, a pixel is composed of two spaces and the background is then colored using ANSI
  private final int PIXEL_SIZE = PIXEL.length(); // we'll need this in the calculations of the movements

  /**
   * The number of pixels the player will jump upwards and downwards.
   */
  private final int JUMP_HEIGHT = 9;

  /**
   * The delay between each step of the jump.
   * A delay too low will make the jump look instantaneous or hard to follow.
   */
  private final int JUMP_DELAY_BETWEEN_EACH_FRAME = 30;
  
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
  private ArrayList<Map> allMaps = new ArrayList<>();
  private ArrayList<ArrayList<Integer>> playerCurrentMatrix = new ArrayList<>();

  /**
   * Since we don't want the main thread to terminate too soon,
   * as long as we're waiting for user inputs, we'll put it to sleep.
   * Terminate this sleep by setting this variable to `true`.
   */
  private boolean gameFinished = false;

  /**
   * Indicates if using the arrow keys is authorized.
   * It's not possible when playing the game, but needed in the menus.
   */
  private boolean isArrowUsable = false;

  /**
   * The maximum Y coordinate of the selector (`>`) when using the main menu.
   */
  private final int MAX_Y_ARROW_POSITION = 23;

  /**
   * The minimum Y coordinate of the selector (`>`) when using the main menu.
   */
  private final int MIN_Y_ARROW_POSITION = 19;

  /**
   * Default X position of the selector (`>`) in the main menu.
   */
  private final int ARROW_DEFAULT_X = 75;

  /**
   * The current Y position of the selector (`>`) in the main menu.
   */
  private int arrow_y = 19;

  /**
   * The current page the user is seeing.
   * By default, the game starts in the main menu,
   * but then a different page is used for the game, the credits, etc.
   */
  private Page currentPage = Page.MAIN_MENU;

  /**
   * Can the player jump? By default, it is `true`.
   * It's necessary to make sure that the player doesn't double-jump.
   */
  private boolean canJump = true;

  /**
   * The index of the current map.
   */
  private int currentMapIndex = 0;

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
    displayMap(currentMapIndex);
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

  /**
   * Restores the current selector position of the main menu to its minimal value (the first choice).
   */
  private void restoreSelectorPosition() {
    arrow_y = MIN_Y_ARROW_POSITION;
  }

  /**
   * Displays the content of an ArrayList<String> line by line.
   * @param fileContent Each line
   */
  private void printMultipleLines(ArrayList<String> fileContent){
    for(String line : fileContent){
      println(line);
    }
  }

  /**
   * Displays the main menu.
   * Arrow up and down can be used to move the cursor.
   */ 
  private void displayMainMenu(){
    restoreSelectorPosition();
    isArrowUsable = true;

    String SEP = File.separator;
    String mainMenuPath = "assets"+SEP+"menu"+SEP+"menu.txt";
    ArrayList<String> mainMenu = TextReader.getContent(mainMenuPath);
    printMultipleLines(mainMenu);
    println("Press 'q' to quit.");
  }

  /**
   * Displays the credits menu while selected in the main menu.
   */
  private void displayCredits() {
    isArrowUsable = false;

    String SEP = File.separator;
    String creditsPath = "assets"+SEP+"menu"+SEP+"credits.txt";
    ArrayList<String> credits = TextReader.getContent(creditsPath);
    printMultipleLines(credits);
  }

  private void displayMapSelectionMenu() {
    clearMyScreen();
    setPlayerSkin(PLAYER_DEFAULT_SKIN);
    displayMap(0);
    saveCursorPosition();
    displayPlayer();
    restoreCursorPosition();
  }

  /**
   * In the main menu, it makes the selector go down.
   */
  private void increaseArrowPosition(){
    if(isArrowUsable && arrow_y!=MIN_Y_ARROW_POSITION){
      saveCursorPosition();
      moveCursorTo(ARROW_DEFAULT_X, arrow_y);
      System.out.print(" ");
      restoreCursorPosition();
      arrow_y--;
      saveCursorPosition();
      moveCursorTo(ARROW_DEFAULT_X, arrow_y);
      System.out.print(">");    
      restoreCursorPosition();
    }
  }

  /**
   * In the main menu, it makes the selector go up.
   */
  private void decreaseArrowPosition(){
    if(isArrowUsable && arrow_y!=MAX_Y_ARROW_POSITION){
      saveCursorPosition();
      moveCursorTo(ARROW_DEFAULT_X, arrow_y);
      System.out.print(" ");
      restoreCursorPosition();
      arrow_y++;
      saveCursorPosition();
      moveCursorTo(ARROW_DEFAULT_X, arrow_y);
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
    if(currentPage==Page.CREDITS){
      currentPage = Page.MAIN_MENU;
      displayMainMenu();
    }
    else if(currentPage==Page.MAIN_MENU){
      if(arrow_y==MIN_Y_ARROW_POSITION){
        currentPage = Page.MAP_SELECTION_MENU;
        displayMapSelectionMenu();
      }
      else if(arrow_y==MIN_Y_ARROW_POSITION+3){
        screenCheck();
      }
      else if(arrow_y==MAX_Y_ARROW_POSITION){
        currentPage = Page.CREDITS;
        displayCredits();
      }
    }
    else if(currentPage==Page.MAP_SELECTION_MENU){
      // TODO.
    }
  }

  @Override
  protected void keyTypedInConsole(int keyCode) {
    switch (keyCode) {
      case JUMP_KEY:
        jump();
        break;
      case TOP_ARROW_KEY:
        increaseArrowPosition();
        break;
      case BOTTOM_ARROW_KEY:
        decreaseArrowPosition();
        break;
      case ENTER_KEY:
        select();
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
  private void displayMap(int index) {
    ArrayList<ArrayList<Integer>> grid = getMapGrid(index);
    displayMatrix(grid, false, -1, -1, -1, -1);
  }

  /**
   * Gets the matrix of a map.
   * @param index The unique index of this map.
   * @return The grid (a list of lists of integers where each integer is a color).
   */
  private ArrayList<ArrayList<Integer>> getMapGrid(int index) {
    return allMaps.get(index).getGrid();
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
            int colorIndexOfBehind = getMapGrid(currentMapIndex).get(objectY + lig - PIXEL_SIZE).get(objectX / PIXEL_SIZE + col);
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
   * Removes the player from the screen.
   */
  private void removePlayerFromScreen() {
    int absX = getPlayerAbsoluteX();
    int absY = getPlayerAbsoluteY(); // it will get incremented as we remove the player's pixels line by line
    moveCursorTo(absX, absY);
    ArrayList<ArrayList<Integer>> background = getMapGrid(currentMapIndex);
    int playerHeight = playerCurrentMatrix.size();
    int playerWidth = playerCurrentMatrix.get(0).size();
    for (int line = 0; line < playerHeight; line++) {
      for (int col = 0; col < playerWidth; col++) {
        int colorIndex = background.get(playerY + line - PIXEL_SIZE).get(playerX / PIXEL_SIZE + col);
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
  
  /**
   * Executes a little program to see if the user has a big enough console to play with.
   * It displays numbers both horizontally and vertically
   * depending on `MINIMAL_GUI_HEIGHT` and `MINIMAL_GUI_WIDTH`.
   * If the user doesn't see all of the numbers, then the screen isn't big enough.
   */
  private void screenCheck() {
    clearMyScreen();
	  for(int i=0; i<MINIMAL_GUI_HEIGHT+1; i+=1) {
		  System.out.print(String.format("%02d", i)+" ");
	  }
	  System.out.println();
	  for(int h=1; h<(MINIMAL_GUI_WIDTH+1); h+=1) {
		  this.println(String.format("%02d", h)+" ");
	  }
	  System.out.print("L'écran est à la bonne taille si vous pouvez voir les nombres "+MINIMAL_GUI_HEIGHT+" en hauteur et "+MINIMAL_GUI_WIDTH+" en largeur.");
  }

  public static void main(String[] args) {
    Game game = new Game();
    game.start(); 
  }
}
