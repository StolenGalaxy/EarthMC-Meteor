package com.stolengalaxy.earthmc_meteor.utils;
import java.util.List;

public class Blacklist {
    public static void blacklist(String type, String name){
        FileHandling.addLine(type + "_blacklist.txt", name.toLowerCase());
        System.out.println("Added " + name + " to " + type + " blacklist");

        if(type.equals("player")){
            Data.currentPlayerBlacklist = getBlacklist("player");
            RefreshData.refreshPlayerData();
        } else{
            Data.currentNationBlacklist = getBlacklist("nation");
            RefreshData.refreshTownData();
        }

    }

    public static void unBlacklist(String type, String name){
        FileHandling.removeTextFromFile(type + "_blacklist.txt", name);
        System.out.println("Removed " + name + " from " + type + " blacklist");

        if(type.equals("player")){
            Data.currentPlayerBlacklist = getBlacklist("player");
            RefreshData.refreshPlayerData();
        } else{
            Data.currentNationBlacklist = getBlacklist("nation");
            RefreshData.refreshTownData();
        }
    }
    public static List<String> getBlacklist(String type){
        return FileHandling.readLines(type + "_blacklist.txt");
    }

}
