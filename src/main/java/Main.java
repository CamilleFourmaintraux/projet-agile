package main.java;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    
    public static void main(String[] args) throws IOException {

        String mainMenuPath = "res"+File.separator+"Menu"+File.separator+"Menu.txt";
        
        TextReader tr = new TextReader();
        ArrayList<String> mainMenu = new ArrayList<>();

        mainMenu = tr.getContent(mainMenuPath);

        for(int i=0;i<mainMenu.size();i++){
            System.out.println(mainMenu.get(i));
        }

    }

}
