package main.java;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Reads a text file.
 */
public class TextReader {
	/**
	 * Gets the content of the text file and returns each line in an `ArrayList`.
	 * If the file doesn't exist, an empty list is returned.
	 * 
	 * @param path The path to the file.
	 * @return Each line in a list.
	 */
	public static ArrayList<String> getContent(String path) {
		ArrayList<String> lines = new ArrayList<>();

		try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
			String line = "";
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
		} catch (Exception e) {
			return new ArrayList<String>();
		}

		return lines;
	}
}