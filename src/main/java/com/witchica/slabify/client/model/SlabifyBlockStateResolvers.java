package com.witchica.slabify.client.model;

import com.witchica.slabify.block.SlabifySlabBlock;
import com.witchica.slabify.client.model.UnbakedSlabModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.BlockStateResolver;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;

@Environment(EnvType.CLIENT)
public class SlabifyBlockStateResolvers {
    public static class Slab implements BlockStateResolver {
        private final SlabifySlabBlock block;

        public Slab(SlabifySlabBlock block) {
            this.block = block;
        }

        @Override
        public void resolveBlockStates(Context context) {
            UnbakedSlabModel bottomModel = new UnbakedSlabModel(SlabType.BOTTOM, block.owner);
            context.setModel(block.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.BOTTOM), bottomModel);
            context.setModel(block.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.BOTTOM).setValue(BlockStateProperties.WATERLOGGED, true), bottomModel);

            UnbakedSlabModel topModel = new UnbakedSlabModel(SlabType.TOP, block.owner);
            context.setModel(block.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.TOP), topModel);
            context.setModel(block.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.TOP).setValue(BlockStateProperties.WATERLOGGED, true), topModel);

            UnbakedSlabModel doubleModel = new UnbakedSlabModel(SlabType.DOUBLE, block.owner);
            context.setModel(block.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.DOUBLE), doubleModel);
            context.setModel(block.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.DOUBLE).setValue(BlockStateProperties.WATERLOGGED, true), doubleModel);
        }
    }
}
