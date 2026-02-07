package com.stolengalaxy.earthmc_hud.modules;

import com.stolengalaxy.earthmc_hud.EarthMC_HUD;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

public class RefreshData extends Module {
    private int timer = 0;
    public RefreshData(){
        super(EarthMC_HUD.EarthMC, "Refresh Data", "");
    }

    @Override
    public void onActivate(){
        timer = 0;
        info("Starting data refresh");
    }

    @EventHandler
    private void onTick(TickEvent.Post event){
        timer++;
        info(String.valueOf(timer));
    }
}
