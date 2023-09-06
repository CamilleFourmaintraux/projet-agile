package main.java;

public class ObstacleSpawn {
    private String name;
    private int speed;
    private int y;
    private double delta;

    public ObstacleSpawn(String name, int speed, int y, double delta) {
        this.name = name;
        this.speed = speed;
        this.y = y;
        this.delta = delta;
    }

    public String getName() { return this.name; }
    public int getSpeed() { return this.speed; }
    public int getY() { return this.y; }
    public double getDelta() { return this.delta; }
}