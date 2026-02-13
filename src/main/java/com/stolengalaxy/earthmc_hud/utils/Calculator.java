package com.stolengalaxy.earthmc_hud.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.stolengalaxy.earthmc_hud.utils.RefreshData.Town;

import static com.stolengalaxy.earthmc_hud.utils.Data.townNames;

public class Calculator {
    public static boolean isPlayerInTown(String playerName, String townName){

        if(Data.visiblePlayers.has(playerName)){
            JsonObject playerCoords = Data.visiblePlayers.get(playerName).getAsJsonObject();
            JsonArray townPoints = Data.towns.get(townName).points().getAsJsonArray();

            Polygon townShape = new Polygon();
            townPoints.forEach(coords -> {
                townShape.addPoint(coords.getAsJsonObject().get("x").getAsInt(), coords.getAsJsonObject().get("z").getAsInt());
            });

            boolean contains = townShape.contains(playerCoords.get("x").getAsInt(), playerCoords.get("z").getAsInt());

            return contains;
        }else {
            return false;
        }

    }

    public static JsonArray getNearbyTowns(String playerName){
        Map<String, Town> towns = Data.towns;
        JsonObject playerCoords = Data.visiblePlayers.get(playerName).getAsJsonObject();

        JsonArray nearbyTowns = new JsonArray();
        townNames.forEach(townName -> {
            if(playerCoords.get("x").getAsInt() >= towns.get(townName).extrema().get(1)){
                if(playerCoords.get("x").getAsInt() <= towns.get(townName).extrema().get(0)){
                    if(playerCoords.get("z").getAsInt() >= towns.get(townName).extrema().get(3)){
                        if(playerCoords.get("z").getAsInt() <= towns.get(townName).extrema().get(2)){
                            nearbyTowns.add(townName);
                        }
                    }
                }
            }
        });
        return nearbyTowns;
    }

    public static boolean isPlayerInAnyTown(String playerName){
        System.out.println("Finding nearby towns to " + playerName);

        JsonArray nearbyTowns = getNearbyTowns(playerName);

        boolean in_a_town = false;
        for (JsonElement townNameElement : nearbyTowns) {
            String townName = townNameElement.getAsString();
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
