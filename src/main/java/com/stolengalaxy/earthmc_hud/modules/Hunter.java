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
    private boolean expectingTeleportWithBaritone = false;
    private boolean expectingTeleportWithoutBaritone = false;
    private String targetNationName = "";


    public Hunter(){
        super(EarthMC_HUD.EarthMC, "Hunter", "Finds optimal hunting targets");
    }

    public static String currentTarget = "";

    private final SettingGroup generalSettings = settings.getDefaultGroup();

    public final SettingGroup autoHuntSettings = settings.createGroup("Auto Hunting");

    private final Setting<Boolean> autoTeleport = autoHuntSettings.add(new BoolSetting.Builder()
        .name("Auto Teleport")
        .description("Uses /n spawn to teleport to the nearest nation spawn to the target player\n (As long as the teleport would take the player more than 100 blocks closer to the target than currently)")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> useBaritone = autoHuntSettings.add(new BoolSetting.Builder()
        .name("Use Baritone (READ DESCRIPTION!)")
        .description("Attempts to use Baritone to move towards the target\nWARNING: Ensure you have Baritone enabled with a # prefix or the commands will be sent in chat!")
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
            if(expectingTeleportWithoutBaritone || expectingTeleportWithBaritone){
                if(chatNotifications.get()){
                    info("Finding new target, cancelling teleport and/or Baritone");
                }
                expectingTeleportWithBaritone = false;
                expectingTeleportWithoutBaritone = false;
            }

            findTarget();
        } else if (timer - initialTeleportTime > 150) {
            if(expectingTeleportWithBaritone){
                if(wasTeleportSuccessful()){
                    ChatUtils.sendPlayerMsg("#stop");
                    ChatUtils.sendPlayerMsg(baritoneCommand);
                }else if(chatNotifications.get()){
                    info("Teleport appears to have been unsuccessful. Cancelling Baritone.");
                }
                
                expectingTeleportWithBaritone = false;
            } else if (expectingTeleportWithoutBaritone) {
                if(!wasTeleportSuccessful()){
                    info("Teleport appears to have been unsuccessful.");
                }
                expectingTeleportWithoutBaritone = false;
            }
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
        if(autoTeleport.get()){

            //if the current distance to the target player is greater than the nearest nation spawn's distance + 100, teleport to the nearest nation spawn
            if(Calculator.myDistanceToCoords(targetCoords.getAsJsonObject()) > shortestNationSpawnDistance + 100){
                targetNationName = closestNationName;
                info("Teleporting to " + closestNationName);
                ChatUtils.sendPlayerMsg("/n spawn " + closestNationName);
            }
        }
        baritoneCommand = "#goto " + targetCoords.getAsJsonObject().get("x") + " " + targetCoords.getAsJsonObject().get("z");
        if(useBaritone.get() && !autoTeleport.get()){
            ChatUtils.sendPlayerMsg("#stop");
            ChatUtils.sendPlayerMsg(baritoneCommand);
        }
        else if(useBaritone.get()){
            initialTeleportTime = timer;
            expectingTeleportWithBaritone = true;
        } else if (autoTeleport.get()) {
            expectingTeleportWithoutBaritone = true;
        }
        currentTarget = targetName;

    }

    private boolean wasTeleportSuccessful(){
        //info(Data.nationSpawns.get(targetNationName).toString());
    }

}
