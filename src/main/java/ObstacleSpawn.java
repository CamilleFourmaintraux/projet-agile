package main.java;

public class ObstacleSpawn {
	private String name;
	private int speed;
	private int y;

	public ObstacleSpawn(String name, int speed, int y) {
		this.name = name;
		this.speed = speed;
		this.y = y;
	}

	public String getName() {
		return this.name;
	}

	public int getSpeed() {
		return this.speed;
	}

	public int getY() {
		return this.y;
	}
}