package com.stolengalaxy.earthmc_meteor.utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.orbit.EventHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RefreshData {
    private int timer = 0;

    public static void init(){
        MeteorClient.EVENT_BUS.subscribe(new RefreshData());
    }

    public record Town (String name, JsonArray points, List<Integer> extrema){}
    @EventHandler
    private static void onGameJoin(GameJoinedEvent event){
        System.out.println("Joined game, starting data refresh.");

        refreshPlayerData();
        refreshTownData();
    }

    @EventHandler
    private void onTick(TickEvent.Post event){
        timer++;
        if(timer % 150 == 0){
            refreshPlayerData();
        }
        if(timer % 12000 == 0){
            refreshTownData();
        }
    }

    public static void refreshPlayerData(){
        System.out.println("Refreshing player data");

        Data.currentPlayerBlacklist = Blacklist.getBlacklist("player");

        Requests.getJson("https://map.earthmc.net/tiles/players.json")
            .thenAccept(json -> {
                JsonObject players = new JsonObject();
                List<String> visiblePlayerNames = new ArrayList<>();
                JsonArray playersArray = json.getAsJsonObject().get("players").getAsJsonArray();

                String ownUsername = Calculator.getOwnUsername();
                playersArray.forEach(player -> {
                    JsonObject playerObject = player.getAsJsonObject();

                    String playerName = playerObject.get("name").getAsString().toLowerCase();

                    if(playerName.equalsIgnoreCase(ownUsername) || Data.currentPlayerBlacklist.contains(playerName)){
                        return;
                    }

                    JsonObject playerCoords = new JsonObject();
                    playerCoords.add("x", playerObject.get("x"));
                    playerCoords.add("y", playerObject.get("y"));
                    playerCoords.add("z", playerObject.get("z"));

                    players.add(playerName, playerCoords);
                    visiblePlayerNames.add(playerName);

                });
                Data.visiblePlayers = players;
                Data.visiblePlayerNames = visiblePlayerNames;
                Data.playersInitialised = true;
            }).exceptionally(exception -> {
                System.err.println("Failed to get player data: " + exception);
                exception.printStackTrace();
                return null;
            });
    }

    public static void refreshTownData(){
        System.out.println("Refreshing town data");

        Data.currentNationBlacklist = Blacklist.getBlacklist("nation");

        Requests.getJson("https://map.earthmc.net/tiles/minecraft_overworld/markers.json")
            .thenAccept(json -> {
                JsonArray towns_data = json.getAsJsonArray().get(0).getAsJsonObject().get("markers").getAsJsonArray();

                Map<String, Town> towns = new HashMap<>();
                JsonObject  nationSpawns = new JsonObject();

                towns_data.forEach(town_element -> {
                    JsonObject town_object = town_element.getAsJsonObject();
                    if(town_object.toString().contains("points")){
                        String townName = town_object.get("tooltip").getAsString().split("<b>")[1].split("</b>")[0].strip();
                        JsonArray points = new JsonArray();

                        town_object.get("points").getAsJsonArray().forEach(pointsGroup ->{
                            JsonArray pointsSubset = pointsGroup.getAsJsonArray().get(0).getAsJsonArray();
                            pointsSubset.forEach(point -> {
                                points.add(point.getAsJsonObject());
                            });
                        });

                        int max_x = points.get(0).getAsJsonObject().get("x").getAsInt();
                        int max_z = points.get(0).getAsJsonObject().get("z").getAsInt();
                        int min_x = max_x;
                        int min_z = max_z;

                        for(int i = 1; i < points.size(); i++){
                            JsonObject point = points.get(i).getAsJsonObject();
                            int this_x = point.getAsJsonObject().get("x").getAsInt();
                            int this_z = point.getAsJsonObject().get("z").getAsInt();

                            if(this_x > max_x){
                                max_x = this_x;
                            }else if (this_x < min_x){
                                min_x = this_x;
                            }

                            if(this_z > max_z){
                                max_z = this_z;
                            }else if (this_z < min_z){
                                min_z = this_z;
                            }
                        }

                        List<Integer> extrema = new ArrayList<>(List.of(max_x, min_x, max_z, min_z));

                        Town town = new Town(townName, points, extrema);
                        towns.put(townName, town);
                        Data.townNames.add(townName);

                    } else if (town_object.toString().contains("point")) {
                        String nationName = town_object.get("tooltip").getAsString().split("(Capital of )")[1].split("\\)\\n")[0].strip();

                        if(!Data.currentNationBlacklist.contains(nationName.toLowerCase())){
                            JsonObject spawnPoint = town_object.get("point").getAsJsonObject();
                            nationSpawns.add(nationName, spawnPoint);
                        }
                    }

                });
                Data.towns = towns;
                Data.nationSpawns = nationSpawns;
                Data.townsInitialised = true;

            })
            .exceptionally(exception -> {
                System.err.println("Failed to get town data: " + exception);
                exception.printStackTrace();
                return null;
            });

    }
}
