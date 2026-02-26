package com.stolengalaxy.earthmc_meteor.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.client.MinecraftClient;

import com.stolengalaxy.earthmc_meteor.utils.RefreshData.Town;
import static com.stolengalaxy.earthmc_meteor.utils.Data.townNames;
import static com.stolengalaxy.earthmc_meteor.utils.Data.visiblePlayers;

public class Calculator {
    public static String getOwnUsername(){
        MinecraftClient client = MinecraftClient.getInstance();
        return client.getSession().getUsername();
    }

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
        //System.out.println("Finding towns nearby to " + playerName);
        townNames.forEach(townName -> {

            List<Integer> townExtrema = towns.get(townName).extrema();
            if(playerCoords.get("x").getAsInt() >= townExtrema.get(1)){
                //System.out.println("Player x " + playerCoords.get("x") + " greater than town min x " + townExtrema.get(1));
                if(playerCoords.get("x").getAsInt() <= townExtrema.get(0)){
                    //System.out.println("Player x " + playerCoords.get("x") + " less than town max x " + townExtrema.get(0));
                    if(playerCoords.get("z").getAsInt() >= townExtrema.get(3)){
                        //System.out.println("Player z " + playerCoords.get("z") + " greater than town min z " + townExtrema.get(3));
                        if(playerCoords.get("z").getAsInt() <= townExtrema.get(2)){
                            //System.out.println("Player z " + playerCoords.get("z") + " less than town max z " + townExtrema.get(2));
                            //System.out.println(townName + " is a nearby town");
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
            if(!visiblePlayers.keySet().contains(name)){
                return;
            }
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
            if(distance < closestSpawnDistance && !Data.currentNationBlacklist.contains(nationName.toLowerCase())){
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
