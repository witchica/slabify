package com.witchica.slabify.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.witchica.slabify.Slabify;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SlabifyConfiguration {
    private final File configurationFile;
    private static SlabifyConfiguration _instance;
    private final Gson gson;

    public ConfigData configData;

    public static SlabifyConfiguration instance() {
        return _instance;
    }

    public class ConfigData {
        public List<ResourceLocation> blacklistedSlabBlocks = new ArrayList<ResourceLocation>(List.of(
                new ResourceLocation("minecraft", "crafting_table"),
                new ResourceLocation("minecraft", "fletching_table")));

        public List<ResourceLocation> forcedSlabBlock = new ArrayList<ResourceLocation>();

        public ConfigData() {

        }
    }

    public SlabifyConfiguration() {
        configurationFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), "slabify.json");
        gson = new GsonBuilder().setPrettyPrinting().create();
        load();
    }

    public void save() {
        try {
            if(!configurationFile.exists()) {
                this.configData = new ConfigData();
                configurationFile.getParentFile().mkdirs();
                configurationFile.createNewFile();
            }

            FileWriter fileWriter = new FileWriter(configurationFile);
            gson.toJson(configData, ConfigData.class, fileWriter);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        load();
    }

    private void load() {
        if(!configurationFile.exists()) {
            save();
        }

        try {
            FileReader reader = new FileReader(configurationFile);
            JsonReader json = new JsonReader(reader);

            this.configData = gson.fromJson(json, ConfigData.class);

            json.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
