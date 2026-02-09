package com.stolengalaxy.earthmc_hud.utils;

import com.google.gson.JsonArray;

public class Data {
    private static JsonArray onlinePlayers = new JsonArray();

    private static JsonArray bases = new JsonArray();

    public static void setOnlinePlayers(JsonArray players){

        onlinePlayers = players;
    }

    public static JsonArray getOnlinePlayers(){
        return onlinePlayers;
    }

    public static void setBases(JsonArray basesList){
        bases = basesList;
    }

}
