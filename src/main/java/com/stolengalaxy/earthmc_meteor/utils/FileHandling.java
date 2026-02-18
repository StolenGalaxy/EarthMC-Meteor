package com.stolengalaxy.earthmc_meteor.utils;

import java.io.*;

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

    public static void addLine(String filename, String lineText){
        try{
            FileWriter writer = new FileWriter(filename);
            writer.append(lineText);
            writer.close();
        } catch (IOException error) {
            error.printStackTrace();
        }
    }

    public static int findLineIndexOfText(String filename, String text){
        int lineIndex = 0;
        try(BufferedReader reader = new BufferedReader(new FileReader(filename))){
            String line;

            while((line = reader.readLine()) != null){
                if(line.strip().equals(text)){
                    break;
                }
                lineIndex++;
            }
            return lineIndex;
        } catch (IOException error){
            error.printStackTrace();
        }
        return lineIndex;
    }
}
