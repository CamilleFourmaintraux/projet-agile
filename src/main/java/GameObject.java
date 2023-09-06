package main.java;

import java.util.ArrayList;

public abstract class GameObject {
  /**
   * The unique name of the map.
   */
  protected String name;

  /**
   * The matrix of the map (each cell is the index of a color in the pallet)
   */
  protected ArrayList<ArrayList<Integer>> matrix;

  public GameObject(String name, ArrayList<ArrayList<Integer>> matrix) {
    this.name = name;
    this.matrix = matrix;
  }

  /**
   * Gets the current matrix's dimensions in an array: `[width, height]`.
   * @return The dimensions of the matrix.
   */
  public int[] getMatrixDimensions() {
    int mapHeight = matrix.size();
    int mapWidth = matrix.get(0).size();
    return new int[]{ mapWidth, mapHeight };
  }

  public String getName() { return this.name; }
  public ArrayList<ArrayList<Integer>> getMatrix() { return this.matrix; }
}
