package com.stolengalaxy.earthmc_meteor.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileHandling {
    public static void ensureFileExists(String filename){
        File file = new File(filename);
        try{
            if(file.createNewFile()){
                System.out.println("Created " + filename);
            } else{
                System.out.println(filename + " already exists");
            }

        } catch (IOException error) {
            error.printStackTrace();
        }
    }

    public static void addLine(String filename, String line){
        try{
            FileWriter writer = new FileWriter(filename);
            writer.append(line);
            writer.close();
        } catch (IOException error) {
            error.printStackTrace();
        }
    }
}
