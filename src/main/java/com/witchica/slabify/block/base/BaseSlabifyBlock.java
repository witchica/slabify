package com.witchica.slabify.block.base;

import com.witchica.slabify.Slabify;
import com.witchica.slabify.types.BlockTypeBase;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockTypes;
import net.minecraft.world.level.block.state.BlockBehaviour;

public interface BaseSlabifyBlock {
    public Block getParent();
    public BlockTypeBase getType();
    public ResourceLocation getRegisteredName();

    public default Block getSelf() {
        return (Block) this;
    }

    public static BlockBehaviour.Properties copy(Block toCopy) {
        BlockBehaviour.Properties p = BlockBehaviour.Properties.ofFullCopy(toCopy);
        p.requiresCorrectToolForDrops = false;
        return p;
    }
}
