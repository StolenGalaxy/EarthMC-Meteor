package com.stolengalaxy.earthmc_hud.modules;

import com.google.gson.JsonObject;
import com.stolengalaxy.earthmc_hud.EarthMC_HUD;
import com.stolengalaxy.earthmc_hud.utils.Requests;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;


public class RefreshData extends Module {
    private int timer = 0;
    public RefreshData(){
        super(EarthMC_HUD.EarthMC, "Refresh Data", "Regularly get data from EarthMC API");
    }

    @Override
    public void onActivate(){
        timer = 0;
        info("Enabling data refreshing");

        refreshPlayerData();
    }

    @EventHandler
    private void onTick(TickEvent.Post event){
        timer++;

        if(timer % 1000 == 0){
            refreshPlayerData();
        }
        if(timer % 12000 == 0){
            //(refresh base data in future)
        }

    }

    private void refreshPlayerData(){
        System.out.println("Getting player data");

        Requests.getJson("https://api.earthmc.net/v3/aurora/online")
            .thenAccept(json -> {
                
            });


    }
}
