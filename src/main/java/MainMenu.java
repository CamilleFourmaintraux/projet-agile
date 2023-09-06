package main.java;

public class MainMenu extends GameMenu {
	public MainMenu() {
		super(new Page[]{ Page.NORMAL_MODE, Page.ARCADE_MODE, Page.CHECK_SCREEN, Page.CREDITS });
	}

	@Override
	public void display() {
		useDefaultTemplate();
		Controls.println("Disclaimer : certains terminaux sont incompatibles avec le jeu.");
	}
}
