package main.java;

/**
 * An enum that holds the different pages there are in the game.
 */
public enum Page {
	NORMAL_MODE("Mode normal"),
	ARCADE_MODE("Mode Arcade"),
	CHECK_SCREEN("Vérifier l'écran"),
	CREDITS("Crédits"),
	MAP_FAR_WEST("Far West", "desert"),
	MAP_MEDIEVAL("Médiéval", "medieval"),
	MAP_CITY("City", "city"),
	MAP_MARIO("Mario World", "mario"),
	MAP_AMONGUS("Among Us", "polus");

	private String text;
	private String mapName; // in case it's a map

	Page(String text) {
		this.text = text;
	}

	Page(String text, String mapName) {
		this.text = text;
		this.mapName = mapName;
	}

	public String getText() { return this.text; }
	public String getMapName() { return this.mapName; }
	public boolean isMap() { return this.mapName != null; }
}
