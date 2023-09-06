package main.java;

import java.util.ArrayList;

/**
 * One map in the game (its name and its matrix).
 */
public class Map extends GameObject {
  public Map(String name, ArrayList<ArrayList<Integer>> matrix) {
    super(name, matrix);
  }
}