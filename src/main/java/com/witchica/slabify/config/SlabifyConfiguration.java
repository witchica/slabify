package com.witchica.slabify.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
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
                new ResourceLocation("minecraft", "fletching_table"),
                new ResourceLocation("minecraft", "dragon_egg"),
                new ResourceLocation("minecraft", "smithing_table"),
                new ResourceLocation("minecraft", "cartography_table"),
                new ResourceLocation("minecraft", "cauldron"),
                new ResourceLocation("minecraft", "lava_cauldron"),
                new ResourceLocation("minecraft", "dirt_path")));

        public List<ResourceLocation> forcedSlabBlock = new ArrayList<ResourceLocation>();

        public List<ResourceLocation> blacklistedWallBlocks = new ArrayList<ResourceLocation>(List.of(
                new ResourceLocation("minecraft", "crafting_table"),
                new ResourceLocation("minecraft", "fletching_table"),
                new ResourceLocation("minecraft", "dragon_egg"),
                new ResourceLocation("minecraft", "smithing_table"),
                new ResourceLocation("minecraft", "cartography_table"),
                new ResourceLocation("minecraft", "cauldron"),
                new ResourceLocation("minecraft", "lava_cauldron"),
                new ResourceLocation("minecraft", "dirt_path")));

        public List<ResourceLocation> forcedWallBlocks = new ArrayList<ResourceLocation>();

        public boolean loadWallsForModdedBlocks = true;
        public boolean loadSlabsForModdedBlocks = true;

        @SerializedName("internal_config_version")
        public int version = 1;

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
    }

    private void load() {
        if(!configurationFile.exists()) {
            save();
        }

        try {
            FileReader reader = new FileReader(configurationFile);
            JsonReader json = new JsonReader(reader);

            this.configData = gson.fromJson(json, ConfigData.class);

            if(this.configData.version == 0) {
                ConfigData defaults = new ConfigData();

                this.configData.blacklistedWallBlocks = defaults.blacklistedWallBlocks;
                this.configData.forcedWallBlocks = defaults.forcedWallBlocks;
                this.configData.loadWallsForModdedBlocks = defaults.loadWallsForModdedBlocks;
                this.configData.version = 1;
                save();
            }

            json.close();
            save();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
