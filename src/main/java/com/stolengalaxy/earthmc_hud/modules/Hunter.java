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
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;

public class Hunter extends Module {
    private int timer = 0;

    private boolean initialActivation = false;

    //variables for determining when to send baritone command after teleportation in auto hunt mode
    //probably should be reworked in future
    private String baritoneCommand = "";
    private int initialTeleportTime = 0;
    private boolean expectingTeleport = false;


    public Hunter(){
        super(EarthMC_HUD.EarthMC, "Hunter", "Finds optimal hunting targets");
    }

    public static String currentTarget = "";

    private final SettingGroup generalSettings = settings.getDefaultGroup();

    private final Setting<Boolean> autoHunt = generalSettings.add(new BoolSetting.Builder()
        .name("Auto Hunt (READ DESCRIPTION!)")
        .description("Uses Baritone to automatically go to target player\nWARNING: Ensure you have Baritone installed or it will send a chat message!")
        .defaultValue(false)
        .build()
    );
    private final Setting<Integer> targetRefreshTime = generalSettings.add(new IntSetting.Builder()
        .name("Target Refresh")
        .description("How often to refresh targets (ticks)")
        .defaultValue(2000)
        .min(1000)
        .sliderRange(1000, 36000)
        .build()
    );
    private final Setting<Boolean> chatNotifications = generalSettings.add(new BoolSetting.Builder()
        .name("Chat Notifications")
        .description("Display module notifications in chat")
        .defaultValue(true)
        .build()
    );

    @Override
    public void onActivate(){
        timer = 0;
        initialActivation = false;
    }

    @Override
    public void onDeactivate(){
        currentTarget = "";
    }

    @EventHandler
    private void onTick(TickEvent.Post event){
        timer++;

        if(!initialActivation && Data.playersInitialised && Data.townsInitialised){
            findTarget();
            initialActivation = true;
        }
        else if(timer % targetRefreshTime.get() == 0 && Data.playersInitialised && Data.townsInitialised && initialActivation){
            expectingTeleport = false;
            findTarget();
        } else if (timer - initialTeleportTime > 150 && expectingTeleport) {
            ChatUtils.sendPlayerMsg("#stop");
            ChatUtils.sendPlayerMsg(baritoneCommand);
            expectingTeleport = false;
        }

    }

    private void findTarget(){
        System.out.println("finding target");
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
        if(autoHunt.get()){

            //if the current distance to the target player is greater than the nearest nation spawn's distance + 100, teleport to the nearest nation spawn
            if(Calculator.myDistanceToCoords(targetCoords.getAsJsonObject()) > shortestNationSpawnDistance + 100){
                info("Teleporting to " + closestNationName);
                ChatUtils.sendPlayerMsg("/n spawn " + closestNationName);
            }

            baritoneCommand = "#goto " + targetCoords.getAsJsonObject().get("x") + " " + targetCoords.getAsJsonObject().get("z");
            initialTeleportTime = timer;
            expectingTeleport = true;
        }
        currentTarget = targetName;

    }

}
