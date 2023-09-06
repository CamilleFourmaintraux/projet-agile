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
      Controls.println(" ".repeat(LEFT_MARGIN - name.length() / 2) + name);
    }
    drawSpace(8);
    displayQuitMessage();
  }
}
