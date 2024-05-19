package com.witchica.slabify.client.model;

import com.witchica.slabify.block.SlabifySliceBlock;
import com.witchica.slabify.block.base.BaseSlabifyBlock;
import com.witchica.slabify.client.model.slab.UnbakedSlabModel;
import com.witchica.slabify.client.model.slice.UnbakedSliceModel;
import com.witchica.slabify.client.model.wall.UnbakedWallModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.BlockStateResolver;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;

import java.util.List;
import java.util.Set;

@Environment(EnvType.CLIENT)
public class SlabifyBlockStateResolvers {
    public static class Slab implements BlockStateResolver {
        private final BaseSlabifyBlock block;

        public Slab(BaseSlabifyBlock block) {
            this.block = block;
        }

        @Override
        public void resolveBlockStates(Context context) {
            List<BlockState> states = block.getSelf().getStateDefinition().getPossibleStates();

            for(BlockState state : states) {
                UnbakedSlabModel model = new UnbakedSlabModel(state.getValue(SlabBlock.TYPE), block.getParent());
                context.setModel(state, model);
            }
        }
    }
    public static class Slice implements BlockStateResolver {
        private final BaseSlabifyBlock block;

        public Slice(BaseSlabifyBlock block) {
            this.block = block;
        }

        @Override
        public void resolveBlockStates(Context context) {
            List<BlockState> states = block.getSelf().getStateDefinition().getPossibleStates();

            for(BlockState state : states) {
                UnbakedSliceModel model = new UnbakedSliceModel(state.getValue(SlabifySliceBlock.HEIGHT), block.getParent());
                context.setModel(state, model);
            }
        }
    }

    public static class Wall implements BlockStateResolver {
        private final BaseSlabifyBlock block;

        public Wall(BaseSlabifyBlock block) {
            this.block = block;
        }

        @Override
        public void resolveBlockStates(Context context) {
            List<BlockState> possibleStates = block.getSelf().getStateDefinition().getPossibleStates();

            for(BlockState state : possibleStates) {
                UnbakedWallModel model = new UnbakedWallModel(state.getValue(WallBlock.UP), state.getValue(WallBlock.NORTH_WALL), state.getValue(WallBlock.EAST_WALL), state.getValue(WallBlock.SOUTH_WALL), state.getValue(WallBlock.WEST_WALL), block.getParent());
                context.setModel(state, model);
            }
        }
    }
}
