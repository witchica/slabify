package com.witchica.slabify.client;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.witchica.slabify.Slabify;
import com.witchica.slabify.block.SlabifySlabBlock;
import com.witchica.slabify.client.model.SlabifyModelLoader;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;

public class SlabifyClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ModelLoadingPlugin.register(new SlabifyModelLoader());

        for(SlabifySlabBlock slabs : Slabify.SLABIFY_SLABS) {
            BlockRenderLayerMap.INSTANCE.putBlock(slabs, ItemBlockRenderTypes.getChunkRenderType(slabs.owner.defaultBlockState()));
        }
    }
}
