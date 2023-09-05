package main.java;

/**
 * Represents a pixel in the game.
 * Cannot be transparent.
 */
public class Color {
  /**
   * ANSI format of the color.
   */
  String ANSI;

  /**
   * Can the user walk on this color?
   * By default it is `true` for all colors, but if you want to make a wall, then set it to `false`.
   */
  boolean x = true;

  public Color(String ansi, boolean x) {
    this.ANSI = ansi;
    this.x = x;
  }
}