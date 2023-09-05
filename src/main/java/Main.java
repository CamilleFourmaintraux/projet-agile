package main.java;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException {
        String SEP = File.separator;
        String mainMenuPath = "res"+SEP+"Menu"+SEP+"Menu.txt";
        ArrayList<String> mainMenu = TextReader.getContent(mainMenuPath);

        for (int i = 0; i < mainMenu.size(); i++) {
            System.out.println(mainMenu.get(i));
        }
    }
}
