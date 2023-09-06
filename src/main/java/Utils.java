package main.java;

import java.io.File;

/**
 * Various utility functions.
 */
public final class Utils {
  /**
   * Converts an RGB color to an ANSI color.
   * ANSI allows us to color texts and control the cursor's position.
   * Note that transparency isn't allowed.
   * @param rgb A list of 3 numbers between 0 and 255 (included), according to the RGB format.
   * @param backgroundColor Is the color supposed to only be used on the background?
   * @return The given color in ANSI format.
   */
  public static String RGBToANSI(int[] rgb, boolean backgroundColor) {
    return "\u001b[" + (backgroundColor ? "48" : "38") + ";2;" + rgb[0] + ";" + rgb[1] + ";" + rgb[2] + "m";
  }

  /**
   * Removes the file extension from a file name.
   * @param fileName The file name.
   * @return The file name without its extension.
   */
  public static String removeFileExtension(String fileName) {
    return fileName.substring(0, fileName.lastIndexOf("."));
  }

  /**
   * Gets the names of all files contained within a specific folder.
   * @param folderPath The path to the folder.
   * @return A list of strings, where each element is the name of a file contained in the folder.
   */
  public static String[] getAllFilesFromDirectory(String folderPath) {
    File[] files = new File(folderPath).listFiles();
    String[] names = new String[files.length];

    for(int i = 0, e = 0; i < files.length; ++i) {
      names[e++] = files[i].getName();
    }

    return names; 
  }
}
