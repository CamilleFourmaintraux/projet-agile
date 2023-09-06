package main.java;

public class MapSelectionMenu extends GameMenu {
	public MapSelectionMenu() {
		super(new Page[] { Page.MAP_FAR_WEST, Page.MAP_MEDIEVAL, Page.MAP_CITY, Page.MAP_MARIO, Page.MAP_AMONGUS });
	}

	public void display() {
		useDefaultTemplate();
	}
}
