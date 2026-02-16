package com.stolengalaxy.earthmc_meteor.hud;

import com.stolengalaxy.earthmc_meteor.EarthMC_Meteor;
import com.stolengalaxy.earthmc_meteor.modules.Hunter;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class HunterDisplay extends HudElement {

    public static final HudElementInfo<HunterDisplay> INFO = new HudElementInfo<>(EarthMC_Meteor.HUD_GROUP, "Current Status", "Shows the current status of EarthMC HUD", HunterDisplay::new);

    public HunterDisplay(){
        super(INFO);
    }

    @Override
    public void render(HudRenderer renderer){
        setSize(renderer.textWidth("Current target: " + Hunter.currentTarget, true), renderer.textHeight(true));

        // Render background
        renderer.quad(x, y, getWidth(), getHeight(), Color.LIGHT_GRAY);

        // Render text
        renderer.text("Current target: " + Hunter.currentTarget, x, y, Color.RED, true);
    }


}
