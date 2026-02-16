package com.stolengalaxy.earthmc_meteor;

import com.stolengalaxy.earthmc_meteor.modules.Hunter;
import com.stolengalaxy.earthmc_meteor.utils.RefreshData;
import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import com.stolengalaxy.earthmc_meteor.hud.HunterDisplay;

public class EarthMC_Meteor extends MeteorAddon {
    public static final Category EarthMC = new Category("EarthMC");
    public static final HudGroup HUD_GROUP = new HudGroup("EarthMC");

    @Override
    public void onInitialize() {

        Modules.get().add(new Hunter());
        RefreshData.init();

        Hud.get().register(HunterDisplay.INFO);
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(EarthMC);
    }

    @Override
    public String getPackage() {
        return "com.stolengalaxy.earthmc_meteor";
    }

    @Override
    public GithubRepo getRepo() {
        return new GithubRepo("StolenGalaxy", "EarthMC-HUD");
    }
}
