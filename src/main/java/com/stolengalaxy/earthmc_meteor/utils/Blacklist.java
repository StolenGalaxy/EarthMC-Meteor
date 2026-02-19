package com.stolengalaxy.earthmc_meteor.utils;
import java.util.List;

public class Blacklist {
    public static void blacklistPlayer(String username){
        FileHandling.addLine("players_blacklist.txt", username);
        System.out.println("Added " + username + " to player blacklist");

        Data.currentPlayerBlacklist = getPlayerBlacklist();
    }
    public static void unBlacklistPlayer(String username){
        FileHandling.removeTextFromFile("players_blacklist.txt", username);
        System.out.println("Removed " + username + " from player blacklist");

        Data.currentPlayerBlacklist = getPlayerBlacklist();
    }
    public static List<String> getPlayerBlacklist(){
        return FileHandling.readLines("players_blacklist.txt");
    }
}
