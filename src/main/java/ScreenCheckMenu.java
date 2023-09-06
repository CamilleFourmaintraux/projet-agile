package main.java;

public class ScreenCheckMenu extends GameMenu {
  private final int min_height;
  private final int min_width;
  private final int pixel_size;

  public ScreenCheckMenu(int min_height, int min_width, int pixel_size) {
    this.min_height = min_height;
    this.min_width = min_width;
    this.pixel_size = pixel_size;
  }

  /**
   * Executes a little program to see if the user has a big enough console to play with.
   * It displays numbers both horizontally and vertically
   * depending on `min_height` and `min_width`.
   * If the user doesn't see all of the numbers, then the screen isn't big enough.
   */
  @Override
  protected void display() {
    Controls.println("L'écran est à la bonne taille si vous pouvez voir les nombres " + min_height + " en hauteur et " + min_width + " en largeur.");
    for(int i = 0; i < min_height; i++) {
		  System.out.print(".".repeat(pixel_size));
	  }
    System.out.print(min_height);
	  Controls.println("");
	  for(int h = 1; h < min_width + 1; h++) {
		  Controls.println(String.format("%0" + pixel_size + "d", h) + " ");
	  }
  }
}
