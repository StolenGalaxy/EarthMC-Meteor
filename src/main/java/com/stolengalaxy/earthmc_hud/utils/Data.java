package com.stolengalaxy.earthmc_hud.utils;

import java.util.ArrayList;

public class Data {
    private static ArrayList<String> onlinePlayers = new ArrayList<>();

    public static void setOnlinePlayers(ArrayList<String> players){
        onlinePlayers = players;
    }

    public ArrayList<String> getOnlinePlayers(){
        return onlinePlayers;
    }

}
