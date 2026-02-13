package com.stolengalaxy.earthmc_hud.utils;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Data {
    public static JsonObject visiblePlayers = new JsonObject();
    public static List<String> visiblePlayerNames = new ArrayList<>();

    public static Map<String, RefreshData.Town> towns = new HashMap<>();
    public static List<String> townNames = new ArrayList<>();

    public static JsonObject nationSpawns = new JsonObject();

}
