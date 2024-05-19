package com.witchica.slabify.client.model;

import com.witchica.slabify.Slabify;
import com.witchica.slabify.block.base.BaseSlabifyBlock;
import com.witchica.slabify.client.model.slab.UnbakedSlabModel;
import com.witchica.slabify.client.model.slice.UnbakedSliceModel;
import com.witchica.slabify.client.model.wall.UnbakedWallModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.WallSide;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class SlabifyModelLoader implements ModelLoadingPlugin {
    @Override
    public void onInitializeModelLoader(Context pluginContext) {
        for(BaseSlabifyBlock slabBlock : Slabify.SLAB_TYPE.entries()) {
            pluginContext.registerBlockStateResolver(slabBlock.getSelf(), new SlabifyBlockStateResolvers.Slab(slabBlock));
        }

        for(BaseSlabifyBlock wallBlock : Slabify.WALL_TYPE.entries()) {
            pluginContext.registerBlockStateResolver(wallBlock.getSelf(), new SlabifyBlockStateResolvers.Wall(wallBlock));
        }

        for(BaseSlabifyBlock sliceBlock : Slabify.SLICE_TYPE.entries()) {
                pluginContext.registerBlockStateResolver(sliceBlock.getSelf(), new SlabifyBlockStateResolvers.Slice(sliceBlock));
        }

        Map<ResourceLocation, Block> MODELS_TO_SLABS = new HashMap<>();
        Map<ResourceLocation, Block> MODELS_TO_WALLS = new HashMap<>();
        Map<ResourceLocation, Block> MODELS_TO_SLICES = new HashMap<>();

        for(BaseSlabifyBlock baseSlabifyBlock : Slabify.SLAB_TYPE.entries()) {
            ResourceLocation resourceLocation = baseSlabifyBlock.getRegisteredName();
            MODELS_TO_SLABS.put(new ResourceLocation(resourceLocation.getNamespace(), "item/" + resourceLocation.getPath()) , baseSlabifyBlock.getSelf());
        }

        for(BaseSlabifyBlock baseSlabifyBlock : Slabify.WALL_TYPE.entries()) {
            ResourceLocation resourceLocation = baseSlabifyBlock.getRegisteredName();
            MODELS_TO_WALLS.put(new ResourceLocation(resourceLocation.getNamespace(), "item/" + resourceLocation.getPath()) , baseSlabifyBlock.getSelf());
        }

        for(BaseSlabifyBlock baseSlabifyBlock : Slabify.SLICE_TYPE.entries()) {
            ResourceLocation resourceLocation = baseSlabifyBlock.getRegisteredName();
            MODELS_TO_SLICES.put(new ResourceLocation(resourceLocation.getNamespace(), "item/" + resourceLocation.getPath()) , baseSlabifyBlock.getSelf());
        }

        pluginContext.resolveModel().register(context -> {
            if(MODELS_TO_SLABS.containsKey(context.id())) {
                return new UnbakedSlabModel(SlabType.BOTTOM, MODELS_TO_SLABS.get(context.id()));
            } else if(MODELS_TO_WALLS.containsKey(context.id())) {
                return new UnbakedWallModel(true, WallSide.NONE, WallSide.LOW, WallSide.NONE, WallSide.LOW, MODELS_TO_WALLS.get(context.id()));
            }else if(MODELS_TO_SLICES.containsKey(context.id())) {
                return new UnbakedSliceModel(1, MODELS_TO_SLICES.get(context.id()));
            }

            return null;
        });
    }
}
