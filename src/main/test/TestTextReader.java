package main.test;

import org.junit.jupiter.api.Test;

import main.java.TextReader;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * We create a temporary test file , read its content using TextReader.getContent, and verify that the content is correctly read.
 */
public class TestTextReader {
    @Test
    public void testGetContent() throws IOException {
        String FilePath = "testfile.txt";
        FileWriter writer = new FileWriter(FilePath);
        writer.write("Line 1\nLine 2\nLine 3");
        writer.close();
        ArrayList<String> line = TextReader.getContent(FilePath);
        assertEquals(3, line.size());
        assertEquals("Line 1", line.get(0));
        assertEquals("Line 2", line.get(1));
        assertEquals("Line 3", line.get(2));
        boolean deletedFile = new java.io.File(FilePath).delete();
        assertTrue(deletedFile);
    }
}