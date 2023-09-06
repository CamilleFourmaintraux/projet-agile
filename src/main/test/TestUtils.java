package main.test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import main.java.Utils;


/**
 * The function checks that the method correctly converts RGB colours to ANSI format.
 */
public class TestUtils {
    @Test
    public void testRGBToANSI() {
        int[] rgb = {255, 0, 0};
        String ansiRed = Utils.RGBToANSI(rgb, false);
        assertEquals("\u001b[38;2;255;0;0m", ansiRed);
        int[] rgbBackground = {0, 0, 255};
        String ansiBlueBackground = Utils.RGBToANSI(rgbBackground, true);
        assertEquals("\u001b[48;2;0;0;255m", ansiBlueBackground);
    }

}