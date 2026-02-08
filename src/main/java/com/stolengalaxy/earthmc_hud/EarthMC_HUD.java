package com.stolengalaxy.earthmc_hud;

import com.mojang.logging.LogUtils;
import com.stolengalaxy.earthmc_hud.modules.Hunter;
import com.stolengalaxy.earthmc_hud.utils.RefreshData;
import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.slf4j.Logger;

public class EarthMC_HUD extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();
    public static final Category EarthMC = new Category("EarthMC");
    public static final HudGroup HUD_GROUP = new HudGroup("Example");

    @Override
    public void onInitialize() {

        // Modules
        Modules.get().add(new Hunter());

        RefreshData.init();
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(EarthMC);
    }

    @Override
    public String getPackage() {
        return "com.stolengalaxy.earthmc_hud";
    }

    @Override
    public GithubRepo getRepo() {
        return new GithubRepo("MeteorDevelopment", "meteor-addon-template");
    }
}
