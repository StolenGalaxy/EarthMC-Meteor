package com.stolengalaxy.earthmc_hud.modules;

import com.stolengalaxy.earthmc_hud.EarthMC_HUD;
import com.stolengalaxy.earthmc_hud.utils.Calculator;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import com.stolengalaxy.earthmc_hud.utils.Data;

public class Hunter extends Module {
    private int timer = 0;

    public Hunter(){
        super(EarthMC_HUD.EarthMC, "Hunter", "");
    }

    @Override
    public void onActivate(){
        timer = 0;
    }

    @EventHandler
    private void onTick(TickEvent.Post event){
        timer++;

    }

}
