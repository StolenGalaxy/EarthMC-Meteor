package com.stolengalaxy.earthmc_hud.modules;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.stolengalaxy.earthmc_hud.EarthMC_HUD;
import com.stolengalaxy.earthmc_hud.utils.Calculator;
import com.stolengalaxy.earthmc_hud.utils.Data;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

public class Hunter extends Module {
    private int timer = 0;

    public Hunter(){
        super(EarthMC_HUD.EarthMC, "Hunter", "");
    }

    public static String currentTarget = "";

    @Override
    public void onActivate(){
        timer = 0;
        findTarget();
    }

    @EventHandler
    private void onTick(TickEvent.Post event){
        timer++;
        if(timer % 150 == 0){
            findTarget();
        }

    }


    private void findTarget(){
        int shortestNationSpawnDistance = 9999999;
        String closestNationName = "";
        String targetName = "";
        JsonElement targetCoords = new JsonObject();

        for(String playerName : Calculator.findOutOfTownPlayers()){
            JsonObject nearestSpawnObject = Calculator.nearestSpawn(playerName);
            int distance = nearestSpawnObject.get("distance").getAsInt();

            if(distance < shortestNationSpawnDistance){
                targetName = playerName;
                shortestNationSpawnDistance = distance;
                targetCoords = Data.visiblePlayers.get(targetName).getAsJsonObject();
                closestNationName = nearestSpawnObject.get("name").getAsString();
            }
        }

        //System.out.println(targetName + " "  + shortestNationSpawnDistance + " " + targetCoords + " " + closestNationName);

    }

}
