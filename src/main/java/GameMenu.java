package main.java;

import java.util.ArrayList;

/**
 * Creates an abstract menu where options are displayed like this:
 * 
 * ```
 *     > Option 1
 *     > Option 2
 *     > Option 3
 * ```
 */
public abstract class GameMenu {
	private static final String LOGO_PATH = "assets/menu/logo.txt";
	private static final String ARROW = "> ";
	public static final int LEFT_MARGIN = 76;
	public static final int LEFT_X = LEFT_MARGIN - 1;

	public static int min_selector_y = -1;
	public static int max_selector_y = 100;
	public static int current_selector_y = 0;

	/**
	 * The possible choices that the user can select in this menu.
	 */
	protected Page[] choices;

	public GameMenu(Page[] choices) {
		this.choices = choices;
	}

	public GameMenu() {
		this.choices = new Page[0];
	}

	/**
	 * Creates what the user is going to see.
	 */
	protected abstract void display();

	/**
	 * Since some pages look the same, we'll consider this as a template.
	 */
	protected void useDefaultTemplate() {
		int logo_height = drawMainLogo();
		drawSpace(8);
		createChoices();
		drawSpace(8);
		displayQuitMessage();
		min_selector_y = logo_height + 8 + 2; // it's because we clear the screen sometimes, the console starts with two lines.
		max_selector_y = logo_height + 8 + 1 + choices.length;
		current_selector_y = min_selector_y;
	}

	/**
	 * It's possible to have a menu without any choices.
	 * As a consequence, we don't need to wait for the user to press the arrow keys.
	 * @return `true` if the menu has choices to select
	 */
	protected boolean hasChoices() {
		return choices.length > 0;
	}

	/**
	 * Can the user select something above the current position?
	 * @return True if there is an option above the current one.
	 */
	protected static boolean canGoUp() {
		return current_selector_y != min_selector_y;
	}

	/**
	 * Can the user select something below the current position?
	 * @return True if there is an option below the current one.
	 */
	protected static boolean canGoDown() {
		return current_selector_y != max_selector_y;
	}

	/**
	 * Gets the selected choice (so the one at the current selector Y-position).
	 * The difference between the current Y position and the minimal value gives the index in `choices`.
	 * @return The selected page.
	 */
	protected Page getSelectedPage() {
		return choices[current_selector_y - min_selector_y];
	}

	/**
	 * Reads the content of the game's logo line by line.
	 * 
	 * @return An array containing all lines of the logo.
	 */
	protected ArrayList<String> getLogo() {
		return TextReader.getContent(LOGO_PATH);
	}

	/**
	 * Draws the main logo with two lines of equals.
	 * It returns the exact number of lines that were drawn.
	 * @return The height of the entire logo (with the lines of equals and all).
	 */
	protected int drawMainLogo() {
		drawEqualsRow(155);
		ArrayList<String> logo = getLogo();
		for (String line : logo) {
			Controls.println(line);
		}
		drawEqualsRow(155);
		return logo.size() + 2;
	}

	/**
	 * Creates the selectable choices of the menu.
	 */
	protected void createChoices() {
		Controls.println(" ".repeat(LEFT_MARGIN - ARROW.length()) + ARROW + choices[0].getText());
		for (int i = 1; i < choices.length; i++) {
			Controls.println(" ".repeat(LEFT_MARGIN) + choices[i].getText());
		}
	}

	/**
	 * Draws a specific number of equals ("=") on a single line.
	 * @param length The number of equals to be printed.
	 */
	protected void drawEqualsRow(int length) {
		Controls.println("=".repeat(length));
	}

	/**
	 * Draws empty space (by printing empty lines).
	 * @param height The number of lines to space out.
	 */
	protected void drawSpace(int height) {
		for (int i = 0; i < height; i++) {
			Controls.println("");
		}
	}

	/**
	 * Prints text that looks centered.
	 * @param text The text to display.
	 */
	protected void printCenteredText(String text) {
		printCenteredText(text, LEFT_MARGIN);
	}

	/**
	 * Prints text that looks centered.
	 * @param text The text to display.
	 * @param width The half-width of the UI.
	 */
	protected void printCenteredText(String text, int width) {
		Controls.println(" ".repeat(width - text.length() / 2) + text);
	}

	/**
	 * Displays a line to explain how to quit.
	 */
	protected void displayQuitMessage() {
		Controls.println("Appuie sur 'q' pour quitter.");
	}
}
