package com.stolengalaxy.earthmc_hud.utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.orbit.EventHandler;


public class RefreshData {
    private int timer = 0;

    public static void init(){
        MeteorClient.EVENT_BUS.subscribe(new RefreshData());
    }

    @EventHandler
    private static void onGameJoin(GameJoinedEvent event){
        System.out.println("Joined game, starting data refresh.");

        refreshPlayerData();
        refreshBaseData();

    }

    @EventHandler
    private void onTick(TickEvent.Post event){
        timer++;
        if(timer % 400 == 0){
            refreshPlayerData();
        }
        if(timer % 1200 == 0){
            refreshBaseData();
        }

    }

    private static void refreshPlayerData(){
        System.out.println("Refreshing player data");

        Requests.getJson("https://map.earthmc.net/tiles/players.json")
            .thenAccept(json -> {
                JsonArray players = json.getAsJsonObject().get("players").getAsJsonArray();
                Data.setOnlinePlayers(players);
            });

    }

    private static void refreshBaseData(){
        System.out.println("Refreshing base data");

        Requests.getJson("https://map.earthmc.net/tiles/minecraft_overworld/markers.json")
            .thenAccept(json -> {
                JsonArray bases = json.getAsJsonArray().get(0).getAsJsonObject().get("markers").getAsJsonArray();

                bases.forEach(base_element -> {
                    JsonObject base = base_element.getAsJsonObject();

                    String baseName = base.get("tooltip").getAsString().split("<b>")[1].split("</b>")[0].strip();
                    System.out.println(baseName);
                });
            });


    }
}
