package com.stolengalaxy.earthmc_meteor.modules;

import com.google.gson.JsonObject;
import com.stolengalaxy.earthmc_meteor.EarthMC_Meteor;
import com.stolengalaxy.earthmc_meteor.utils.Blacklist;
import com.stolengalaxy.earthmc_meteor.utils.Calculator;
import com.stolengalaxy.earthmc_meteor.utils.Data;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;

import java.util.List;

public class Hunter extends Module {
    private int timer = 0;
    private boolean initialActivation = false;


    //variables for determining when to send baritone command after teleportation in auto hunt mode
    //probably should be reworked in future
    private String baritoneCommand = "";
    private int initialTeleportTime = 0;
    private boolean expectingTeleportWithBaritone = false;
    private boolean expectingTeleportWithoutBaritone = false;
    private int initialDistanceToTarget = 0;
    private JsonObject targetCoords = new JsonObject();
    public static String currentTarget = "";

    private int targetInvisibleTicks = 0;
    private boolean teleportUnnecessary = false;

    //variables for auto town blacklisting
    private String closestNationName = "";
    private boolean consideredBlacklisting = false;
    private boolean decidedToBlacklist = false;
    private JsonObject initialTeleportCoords = new JsonObject();

    public Hunter(){
        super(EarthMC_Meteor.EarthMC, "Hunter", "Finds optimal hunting targets");
    }

    private final SettingGroup generalSettings = settings.getDefaultGroup();
    public final SettingGroup autoHuntSettings = settings.createGroup("Auto Hunting");

    private final Setting<Boolean> autoTeleport = autoHuntSettings.add(new BoolSetting.Builder()
        .name("Auto Teleport")
        .description("Uses /n spawn to teleport to the nearest nation spawn to the target player\n (As long as the " +
            "teleport would take the player more than 100 blocks closer to the target than currently)")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> autoBlacklistTowns = autoHuntSettings.add(new BoolSetting.Builder()
        .name("Auto Blacklist Towns")
        .description("Will attempt to automatically blacklist towns that are blocked to prevent exit.\n(Auto Teleport and Use Baritone must both be enabled as well)")
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
        .defaultValue(400)
        .min(400)
        .sliderRange(400, 6000)
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
        } else if(timer % targetRefreshTime.get() == 0 && Data.playersInitialised && Data.townsInitialised && initialActivation){
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
                    initialTeleportCoords = Calculator.myCoords();
                    ChatUtils.sendPlayerMsg("#stop");
                    ChatUtils.sendPlayerMsg(baritoneCommand);
                    consideredBlacklisting = false;
                    decidedToBlacklist = false;
                }else{
                    if(chatNotifications.get()){
                        info("Teleport appears to have been unsuccessful. Cancelling Baritone.");
                    }
                    currentTarget = "";
                }
                expectingTeleportWithBaritone = false;

            } else if (expectingTeleportWithoutBaritone) {
                if(!wasTeleportSuccessful()){
                    if(chatNotifications.get()){
                        info("Teleport appears to have been unsuccessful.");
                    }
                    currentTarget = "";
                }
                expectingTeleportWithoutBaritone = false;

            }
        }

        //if a target is set, and they are not visible on the map, increase their invisible ticks, otherwise reset to 0
        if(!currentTarget.isEmpty() && !Data.visiblePlayerNames.contains(currentTarget)){
            targetInvisibleTicks++;
        } else if (!currentTarget.isEmpty()) {
            targetInvisibleTicks = 0;
        }

        // has enough time passed to consider auto blacklisting town?
        if(!consideredBlacklisting && timer - initialTeleportTime > 600 && autoBlacklistTowns.get()){
            considerAutoTownBlacklist();
        }
    }

    private void findTarget(){
        teleportUnnecessary = false;

        if(!currentTarget.isEmpty() && targetAvailable() && !decidedToBlacklist){
            if(chatNotifications.get()){
                info("Target still available. Continuing.");
            }
            return;
        } else if (!targetAvailable()) {
            if(chatNotifications.get()){
                info("Target appears to have become unavailable.");
            }
            if(useBaritone.get()){
                ChatUtils.sendPlayerMsg("#stop");
            }
        } else if (decidedToBlacklist) {
            if(chatNotifications.get()){
                info("Nation spawn appears to not have an exit. Blacklisting " + closestNationName);
            }
            Blacklist.blacklist("nation", closestNationName);
            ChatUtils.sendPlayerMsg("#stop");
        }

        int shortestNationSpawnDistance = 9999999;
        closestNationName = "";
        String targetName = "";

        List<String> availablePlayers = Calculator.findOutOfTownPlayers();
        if (availablePlayers.isEmpty()){
            if(chatNotifications.get()){
                info("No targets found.");
            }
            return;
        }

        for(String playerName : availablePlayers){
            JsonObject nearestSpawnObject = Calculator.nearestSpawn(playerName);
            int distance = nearestSpawnObject.get("distance").getAsInt();

            if(distance < shortestNationSpawnDistance){
                targetName = playerName;
                shortestNationSpawnDistance = distance;
                targetCoords = Data.visiblePlayers.get(targetName).getAsJsonObject();
                closestNationName = nearestSpawnObject.get("name").getAsString();
            }
        }
        targetInvisibleTicks = 0;
        initialDistanceToTarget = Calculator.myDistanceToCoords(targetCoords);

        if(chatNotifications.get()){
            info("New target: " + targetName + "\nCoordinates: (" + targetCoords.getAsJsonObject().get("x")
                + ", "  + targetCoords.getAsJsonObject().get("z") + ")" + "\nNearest nation spawn: "
                + closestNationName + " (" + shortestNationSpawnDistance + " blocks)");
        }
        if(autoTeleport.get()){
            //if the current distance to the target player is greater than the nearest nation spawn's distance + 100, teleport to the nearest nation spawn
            if(Calculator.myDistanceToCoords(targetCoords.getAsJsonObject()) > shortestNationSpawnDistance + 100){
                if(chatNotifications.get()){
                    info("Teleporting to " + closestNationName);
                }

                //ChatUtils.sendPlayerMsg("/tp " + Data.nationSpawns.get(closestNationName).getAsJsonObject().get("x") + " 90 " + Data.nationSpawns.get(closestNationName).getAsJsonObject().get("z"));
                ChatUtils.sendPlayerMsg("/n spawn " + closestNationName);
            }else{
                if(chatNotifications.get()){
                    info("Already close to target. Teleport not needed.");
                }
                teleportUnnecessary = true;
            }

        }

        // probably a much easier way to lay out this whole section
        baritoneCommand = "#goto " + targetCoords.getAsJsonObject().get("x") + " " + targetCoords.getAsJsonObject().get("z");
        if(useBaritone.get() && !autoTeleport.get()){
            ChatUtils.sendPlayerMsg("#stop");
            ChatUtils.sendPlayerMsg(baritoneCommand);
        }
        else if(useBaritone.get() && !teleportUnnecessary){
            initialTeleportTime = timer;
            expectingTeleportWithBaritone = true;
        } else if (autoTeleport.get() && !teleportUnnecessary) {
            expectingTeleportWithoutBaritone = true;
        } else if (teleportUnnecessary && useBaritone.get()) {
            ChatUtils.sendPlayerMsg("#stop");
            ChatUtils.sendPlayerMsg(baritoneCommand);
        }
        currentTarget = targetName;
    }

    private boolean wasTeleportSuccessful(){
        return Calculator.myDistanceToCoords(targetCoords) <= initialDistanceToTarget - 100;
    }

    private boolean targetAvailable(){
        boolean available = true;

        // if target has gone invisible for too long, set to unavailable
        if(targetInvisibleTicks > 1200){
            available = false;
        }
        // if the target has gone inside a town and player is not nearby (so unlikely in combat), set to unavailable
        if(Data.visiblePlayerNames.contains(currentTarget)){
            if(Calculator.isPlayerInAnyTown(currentTarget) && Calculator.myDistanceToCoords(targetCoords) > 100){
                available = false;
            }
        }
        return available;
    }

    private void considerAutoTownBlacklist(){
        consideredBlacklisting = true;
        if (useBaritone.get() && autoTeleport.get() && autoBlacklistTowns.get() && Calculator.myDistanceToCoords(initialTeleportCoords) < 50){
            decidedToBlacklist = true;
            findTarget();
        }
    }
}
