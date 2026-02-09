package com.stolengalaxy.earthmc_hud.utils;

import com.google.gson.JsonArray;

import com.google.gson.JsonObject;

public class Data {
    private static JsonObject onlinePlayers = new JsonObject();
    private static JsonObject towns = new JsonObject();
    private static JsonObject nationSpawns = new JsonObject();

    public static void setOnlinePlayers(JsonObject players){
        onlinePlayers = players;
    }

    public static JsonObject getOnlinePlayers(){
        return onlinePlayers;
    }

    public static void setTowns(JsonObject townsList){
        towns = townsList;
    }

    public static void setNationSpawns(JsonObject spawnsList){
        nationSpawns = spawnsList;
    }

}
