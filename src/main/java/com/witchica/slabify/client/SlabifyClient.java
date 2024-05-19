package com.witchica.slabify.client;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.witchica.slabify.Slabify;
import com.witchica.slabify.block.SlabifySlabBlock;
import com.witchica.slabify.block.base.BaseSlabifyBlock;
import com.witchica.slabify.client.model.SlabifyModelLoader;
import com.witchica.slabify.client.screen.SawingTableScreen;
import com.witchica.slabify.menu.SawingTableMenu;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;

@Environment(EnvType.CLIENT)
public class SlabifyClient implements ClientModInitializer {

    public static SlabifyClient INSTANCE;

    public SlabifyClient() {
        INSTANCE = this;
    }

    public void onClientPostInitialize() {
        ModelLoadingPlugin.register(new SlabifyModelLoader());

        for(BaseSlabifyBlock slabs : Slabify.SLAB_TYPE.entries()) {
            BlockRenderLayerMap.INSTANCE.putBlock(slabs.getSelf(), ItemBlockRenderTypes.getChunkRenderType(slabs.getParent().defaultBlockState()));
        }

        for(BaseSlabifyBlock wall : Slabify.WALL_TYPE.entries()) {
            BlockRenderLayerMap.INSTANCE.putBlock(wall.getSelf(), ItemBlockRenderTypes.getChunkRenderType(wall.getParent().defaultBlockState()));
        }
    }

    @Override
    public void onInitializeClient() {
        MenuScreens.register(Slabify.SAWING_MENU_TYPE, SawingTableScreen::new);
    }
}
