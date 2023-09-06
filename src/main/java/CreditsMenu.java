package main.java;

public class CreditsMenu extends GameMenu {
  private static final String[] names = new String[]{
    "GYSEMANS Thomas",
    "Milleville Paul",
    "Bernard Ludovic",
    "Fourmaintraux Camille",
    "Top Jessy",
    "Demory Lea"
  };

  @Override
  protected void display() {
    drawMainLogo();
    drawSpace(8);
    for (String name : names) {
      printCenteredText(name);
    }
    drawSpace(8);
    displayQuitMessage();
  }
}
