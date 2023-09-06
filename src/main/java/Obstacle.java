package main.java;

import java.util.ArrayList;

/**
 * One obstacle in the game (its name and its matrix).
 */
public class Obstacle extends GameObject {
  public Obstacle(String name, ArrayList<ArrayList<Integer>> matrix) {
    super(name, matrix);
  }
}
