package main.java;

import java.util.ArrayList;

public class VictoryMenu extends GameMenu {
  private static final String VICTORY_PATH = "assets/menu/victoire.txt";

  @Override
  protected void display() {
    drawSpace(10);
    ArrayList<String> victoryLogoLines = TextReader.getContent(VICTORY_PATH);
    for (String line : victoryLogoLines) {
      Controls.println(line);
    }
    drawSpace(10);
    displayQuitMessage();
  }
}
