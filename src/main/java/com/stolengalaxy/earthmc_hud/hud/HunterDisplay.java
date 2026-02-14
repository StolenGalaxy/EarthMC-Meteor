package com.stolengalaxy.earthmc_hud.hud;

import com.stolengalaxy.earthmc_hud.EarthMC_HUD;
import com.stolengalaxy.earthmc_hud.modules.Hunter;
import com.stolengalaxy.earthmc_hud.utils.Data;
import meteordevelopment.meteorclient.systems.Systems;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class HunterDisplay extends HudElement {

    public static final HudElementInfo<HunterDisplay> INFO = new HudElementInfo<>(EarthMC_HUD.HUD_GROUP, "Current Status", "Shows the current status of EarthMC HUD", HunterDisplay::new);

    public HunterDisplay(){
        super(INFO);
    }

    @Override
    public void render(HudRenderer renderer){
        System.out.println("render called!");
        setSize(renderer.textWidth("Current target: " + Hunter.currentTarget, true), renderer.textHeight(true));

        // Render background
        renderer.quad(x, y, getWidth(), getHeight(), Color.LIGHT_GRAY);

        // Render text
        renderer.text("Current target: " + Hunter.currentTarget, x, y, Color.WHITE, true);
    }


}
