package com.witchica.slabify.client.model;

import com.witchica.slabify.Slabify;
import com.witchica.slabify.block.SlabifySlabBlock;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.SlabType;

import java.util.HashMap;
import java.util.Map;

public class SlabifyModelLoader implements ModelLoadingPlugin {
    @Override
    public void onInitializeModelLoader(Context pluginContext) {
        for(SlabifySlabBlock slabBlock : Slabify.SLABIFY_SLABS) {
            pluginContext.registerBlockStateResolver(slabBlock, new SlabifyBlockStateResolvers.Slab(slabBlock));
        }

        Map<ResourceLocation, SlabifySlabBlock> MODELS_TO_SLABS = new HashMap<>();

        for(ResourceLocation resourceLocation : Slabify.IDS_TO_SLABS.keySet()) {
            MODELS_TO_SLABS.put(new ResourceLocation(resourceLocation.getNamespace(), "item/" + resourceLocation.getPath()) , Slabify.IDS_TO_SLABS.get(resourceLocation));
        }

        pluginContext.resolveModel().register(context -> {
            if(context.id().getNamespace().contains("slabify")) {
                System.err.println(context.id().getNamespace() + "_" + context.id().getPath());
            }
            if(MODELS_TO_SLABS.containsKey(context.id())) {
                return new UnbakedSlabModel(SlabType.BOTTOM, MODELS_TO_SLABS.get(context.id()));
            }

            return null;
        });
    }
}
