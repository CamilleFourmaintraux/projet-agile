package main.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class TextReader {

    //La méthode getContent récupère le contenu du fichier txt à la position passée en paramètre si il éxiste
    public ArrayList<String> getContent(String path) throws IOException{

        ArrayList<String> res = new ArrayList<String>();

        File file = new File(path);
        BufferedReader br = new BufferedReader(new FileReader(file));

        while(br.ready()){
            res.add(br.readLine());
        }

        br.close();
        
        return res;

    }

}