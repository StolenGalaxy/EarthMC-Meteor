package com.stolengalaxy.earthmc_hud.utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.orbit.EventHandler;

import java.util.ArrayList;


public class RefreshData {
    private int timer = 0;

    public static void init(){
        MeteorClient.EVENT_BUS.subscribe(new RefreshData());
    }

    @EventHandler
    private static void onGameJoin(GameJoinedEvent event){
        System.out.println("Joined game, starting data refresh.");

    }

    @EventHandler
    private void onTick(TickEvent.Post event){
        timer++;
        if(timer % 400 == 0){
            refreshPlayerData();
        }
        if(timer % 12000 == 0){
            //(refresh base data in future)
        }

    }

    private void refreshPlayerData(){
        System.out.println("Refreshing player data");
        ArrayList<String> onlinePlayers = new ArrayList<>();

        Requests.getJson("https://api.earthmc.net/v3/aurora/online")
            .thenAccept(json -> {
                json.get("players").getAsJsonArray().forEach(player -> {
                    onlinePlayers.add(player.getAsJsonObject().get("name").getAsString());
                });
                Data.setOnlinePlayers(onlinePlayers);
            });



    }
}
