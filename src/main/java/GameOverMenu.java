package main.java;

import java.util.ArrayList;

public class GameOverMenu extends GameMenu {
  private static final String GAME_OVER_PATH = "assets/menu/game_over.txt";

  @Override
  protected void display() {
    drawSpace(5);
    ArrayList<String> victoryLogoLines = TextReader.getContent(GAME_OVER_PATH);
    int width = victoryLogoLines.get(0).length() / 2;
    for (String line : victoryLogoLines) {
      Controls.println(line);
    }
    drawSpace(5);
    printCenteredText("C'est dommage...", width);
    printCenteredText("Tu dois tout recommencer !", width);
    drawSpace(5);
    displayQuitMessage();
  }
}
