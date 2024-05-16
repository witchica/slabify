package com.witchica.slabify.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class NoNameBlockItem extends BlockItem {
    private final Block parent;

    public NoNameBlockItem(Block block, Block parent, Properties properties) {
        super(block, properties);
        this.parent = parent;
    }

    @Override
    public Component getName(ItemStack itemStack) {
        return parent.getName();
    }
}
