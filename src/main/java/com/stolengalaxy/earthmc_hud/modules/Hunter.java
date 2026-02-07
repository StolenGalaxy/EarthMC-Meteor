package com.stolengalaxy.earthmc_hud.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.MinecraftClient;

public class Hunter extends Module {
    private int timer = 0;

    public Hunter(){
        super(Categories.Misc, "Hunter", "");
    }

    @Override
    public void onActivate(){
        timer = 0;
        info("starting!!");
    }

    @EventHandler
    private void onTick(TickEvent.Post event){
        timer++;
        System.out.println("running");
        if (timer > 99){
            MinecraftClient.getInstance().getNetworkHandler().sendChatMessage("Hello World!");
            timer = 0;
        }
    }

}
