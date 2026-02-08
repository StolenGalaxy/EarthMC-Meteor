package com.stolengalaxy.earthmc_hud.utils;

import com.google.gson.JsonArray;

public class Data {
    private static JsonArray onlinePlayers = new JsonArray();

    public static void setOnlinePlayers(JsonArray players){

        onlinePlayers = players;
    }

    public static JsonArray getOnlinePlayers(){
        return onlinePlayers;
    }

}
