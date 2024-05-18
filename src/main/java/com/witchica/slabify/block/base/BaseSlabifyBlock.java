package com.witchica.slabify.block.base;

import com.witchica.slabify.Slabify;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public interface BaseSlabifyBlock {
    public Block getParent();
    public Slabify.BlockType getType();

    public default Block getSelf() {
        return (Block) this;
    }
}
