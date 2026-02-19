package com.stolengalaxy.earthmc_meteor.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
            FileWriter writer = new FileWriter(filename, true);
            writer.write("\n" + lineText);
            writer.close();
        } catch (IOException error) {
            error.printStackTrace();
        }
    }

    public static void deleteFile(String filename){
        File file = new File(filename);
        if(file.delete()){
            System.out.println("Deleted " + filename);
        } else{
            System.out.println("Unable to delete " + filename);
        }
    }

    public static void renameFile(String currentName, String newName){
        File currrentFile = new File(currentName);
        File newFile = new File(newName);

         if (currrentFile.renameTo(newFile)){
             System.out.println("Renamed " + currentName + " to " + newName);
         } else{
             System.out.println("Unable to rename " + currentName + " to " + newName);
         }
    }


    public static void removeTextFromFile(String filename, String text){
        Random rand = new Random();
        String tempFileName = "temp" + rand.nextInt(99999999) + filename;
        ensureFileExists(tempFileName);
        try(BufferedReader reader = new BufferedReader(new FileReader(filename))){
            String line;

            while((line = reader.readLine()) != null){
                if(!line.strip().equals(text)){
                    addLine(tempFileName, line);
                }
            }
            reader.close();
            deleteFile(filename);
            renameFile(tempFileName, filename);
        } catch (IOException error){
            error.printStackTrace();
        }
    }

    public static List<String> readLines(String filename){
        List<String> lines = new ArrayList<>();
        try{
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            while((line = reader.readLine()) != null){
                lines.add(line);
            }
        } catch(IOException error){
            error.printStackTrace();
        }
        return lines;

    }
}
