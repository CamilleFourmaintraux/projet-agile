package main.java;

public class ObstacleSpawn {
    private String name;
    private double speed;
    private int y;
    private double delta;

    public ObstacleSpawn(String name, double speed, int y, double delta) {
        this.name = name;
        this.speed = speed;
        this.y = y;
        this.delta = delta;
    }

    public String getName() { return this.name; }
    public double getSpeed() { return this.speed; }
    public int getY() { return this.y; }
    public double getDelta() { return this.delta; }
}