package com.stolengalaxy.earthmc_hud.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.awt.Polygon;
import java.util.ArrayList;
import java.util.List;


public class Calculator {
    public static boolean isPlayerInTown(String playerName, String townName){

        if(Data.visiblePlayers.has(playerName)){
            JsonObject playerCoords = Data.visiblePlayers.get(playerName).getAsJsonObject();
            JsonArray townCoords = Data.towns.get(townName).getAsJsonArray();

            Polygon townShape = new Polygon();
            townCoords.forEach(coords -> {
                townShape.addPoint(coords.getAsJsonObject().get("x").getAsInt(), coords.getAsJsonObject().get("z").getAsInt());
            });

            return townShape.contains(playerCoords.get("x").getAsInt(), playerCoords.get("z").getAsInt());
        }else {
            return false;
        }

    }
    public static boolean isPlayerInAnyTown(String playerName){
        List<String> townNames = Data.townNames;

        boolean in_a_town = false;
        for (String townName : townNames) {
            if(isPlayerInTown(playerName, townName)){
                in_a_town = true;
                break;
            }
        }
        return in_a_town;
    }
    public static List<String> findOutOfTownPlayers(){
        List<String> visiblePlayerNames = Data.visiblePlayerNames;

        List<String> playersOutOfTowns = new ArrayList<>();
        visiblePlayerNames.forEach(name -> {
            if(!isPlayerInAnyTown(name)){
                playersOutOfTowns.add(name);
            }
        });
        return playersOutOfTowns;
    }
}
