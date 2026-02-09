package com.stolengalaxy.earthmc_hud.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.awt.Polygon;
import java.util.ArrayList;
import java.util.List;


public class Calculator {
    public static boolean isPlayerInTown(String playerName, String townName){

        if(Data.getOnlinePlayers().has(playerName)){
            JsonObject playerCoords = Data.getOnlinePlayers().get(playerName).getAsJsonObject();
            JsonArray townCoords = Data.getTowns().get(townName).getAsJsonArray();

            Polygon townShape = new Polygon();
            townCoords.forEach(coords -> {
                townShape.addPoint(coords.getAsJsonObject().get("x").getAsInt(), coords.getAsJsonObject().get("z").getAsInt());
            });

            return townShape.contains(playerCoords.get("x").getAsInt(), playerCoords.get("z").getAsInt());
        }else {
            return false;
        }




    }
}
