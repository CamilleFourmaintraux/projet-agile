package main.java;

import java.util.ArrayList;

/**
 * One map in the game (its name and its matrix).
 */
public class Map {
  /**
   * The unique name of the map.
   */
  private String name;

  /**
   * The matrix of the map (each cell is the index of a color in the pallet)
   */
  private ArrayList<ArrayList<Integer>> grid;

  public Map(String name, ArrayList<ArrayList<Integer>> grid) {
    this.name = name;
    this.grid = grid;
  }

  public String getName() { return this.name; }
  public ArrayList<ArrayList<Integer>> getGrid() { return this.grid; }
}