package com.stolengalaxy.earthmc_hud.modules;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.stolengalaxy.earthmc_hud.EarthMC_HUD;
import com.stolengalaxy.earthmc_hud.utils.Calculator;
import com.stolengalaxy.earthmc_hud.utils.Data;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

public class Hunter extends Module {
    private int timer = 0;

    public Hunter(){
        super(EarthMC_HUD.EarthMC, "Hunter", "");
    }

    public static String currentTarget = "";

    private final SettingGroup generalSettings = settings.getDefaultGroup();

    private final Setting<Boolean> autoHunt = generalSettings.add(new BoolSetting.Builder()
        .name("Auto Hunt")
        .description("Uses Baritone to automatically go to target player")
        .defaultValue(false)
        .build()
    );
    private final Setting<Integer> targetRefreshTime = generalSettings.add(new IntSetting.Builder()
        .name("Target Refresh")
        .description("How often to refresh targets (ticks)")
        .defaultValue(750)
        .min(150)
        .sliderRange(150, 200000)
        .build()
    );
    private final Setting<Boolean> chatNotifications = generalSettings.add(new BoolSetting.Builder()
        .name("Chat Notifications")
        .description("Display notifications in chat")
        .defaultValue(true)
        .build()
    );

    @Override
    public void onActivate(){
        timer = 0;
        findTarget();
    }

    @Override
    public void onDeactivate(){
        currentTarget = "";
    }

    @EventHandler
    private void onTick(TickEvent.Post event){
        timer++;
        if(timer % targetRefreshTime.get() == 0){
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
        if(chatNotifications.get()){
            info("New target: " + targetName + "\nCoordinates: (" + targetCoords.getAsJsonObject().get("x")
                + ", "  + targetCoords.getAsJsonObject().get("z") + ")" + "\nNearest nation spawn: "
                + closestNationName + " (" + shortestNationSpawnDistance + " blocks)");
        }
        currentTarget = targetName;
        
    }

}
