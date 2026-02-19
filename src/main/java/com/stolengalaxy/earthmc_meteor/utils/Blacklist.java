package com.stolengalaxy.earthmc_meteor.utils;
import java.util.List;

public class Blacklist {
    public static void blacklistPlayer(String username){
        System.out.println("Added " + username + " to player blacklist");

        FileHandling.addLine("players_blacklist.txt", username);
    }
    public static void unBlacklistPlayer(String username){
        System.out.println("Removed " + username + " from player blacklist");
        FileHandling.removeTextFromFile("players_blacklist.txt", username);
    }
    public static List<String> getPlayerBlacklist(){
        return FileHandling.readLines("players_blacklist.txt");
    }
}
