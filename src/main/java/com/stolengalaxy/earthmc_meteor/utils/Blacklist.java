package com.stolengalaxy.earthmc_meteor.utils;

import java.util.ArrayList;
import java.util.List;

public class Blacklist {
    public static void blacklistPlayer(String username){
        System.out.println("Added " + username + " to player blacklist");
    }
    public static void unBlacklistPlayer(String username){
        System.out.println("Removed " + username + " from player blacklist");
    }
    public static List<String> getPlayerBlacklist(){
        return new ArrayList<>();
    }
}
