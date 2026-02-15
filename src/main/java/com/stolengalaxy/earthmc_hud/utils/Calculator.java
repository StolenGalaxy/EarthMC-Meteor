package com.stolengalaxy.earthmc_hud.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.client.MinecraftClient;

import com.stolengalaxy.earthmc_hud.utils.RefreshData.Town;
import static com.stolengalaxy.earthmc_hud.utils.Data.townNames;

public class Calculator {
    public static Integer myDistanceToCoords(JsonObject coords){
        MinecraftClient client = MinecraftClient.getInstance();
        int xDistance = 0;
        int zDistance = 0;
        if(client.player != null){
            xDistance = coords.get("x").getAsInt() - client.player.getBlockX();
            zDistance = coords.get("z").getAsInt() - client.player.getBlockZ();
        }
        int distance = (int) Math.pow(Math.pow(xDistance, 2) + Math.pow(zDistance, 2), 0.5);
        return distance;
    }

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
        //System.out.println(playersOutOfTowns.size() + "/" + visiblePlayerNames.size());
        return playersOutOfTowns;
    }

    public static Integer distanceBetweenCoords(JsonObject coordsOne, JsonObject coordsTwo){
        int xDistance = coordsTwo.get("x").getAsInt() - coordsOne.get("x").getAsInt();
        int zDistance = coordsTwo.get("z").getAsInt() - coordsOne.get("z").getAsInt();

        int distance = (int) Math.pow(Math.pow(xDistance, 2) + Math.pow(zDistance, 2), 0.5);

        return distance;
    }

    public static JsonObject nearestSpawn(String playerName){
        JsonObject playerCoords = Data.visiblePlayers.get(playerName).getAsJsonObject();

        int closestSpawnDistance = 1000000;
        String closestSpawnName = "";

        for(String nationName : Data.nationSpawns.keySet()){
            int distance = distanceBetweenCoords(playerCoords, Data.nationSpawns.get(nationName).getAsJsonObject());
            if(distance < closestSpawnDistance){
                closestSpawnDistance = distance;
                closestSpawnName = nationName;
            }
        }
        JsonObject closestSpawnObject = new JsonObject();
        closestSpawnObject.addProperty("name", closestSpawnName);
        closestSpawnObject.addProperty("distance", closestSpawnDistance);
        return closestSpawnObject;
    }
}
