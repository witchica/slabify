package com.witchica.slabify.types;

import com.witchica.slabify.Slabify;
import com.witchica.slabify.block.SlabifyWallBlock;
import com.witchica.slabify.block.base.BaseSlabifyBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;

public class WallBlockType extends BlockTypeBase {
    public WallBlockType() {
        super("wall");
    }

    @Override
    public void addBlockToLists(BaseSlabifyBlock created) {
        super.addBlockToLists(created);

        if(!Slabify.BLOCKS_TO_CHILDREN.containsKey(created.getParent())) {
            Slabify.BLOCKS_TO_CHILDREN.put(created.getParent(), new ArrayList<>());
        }

        Slabify.BLOCKS_TO_CHILDREN.get(created.getParent()).add(created);
    }

    @Override
    public void removeForcedAndBlacklist(ResourceLocation s) {
        if(Slabify.CONFIG.configData.blacklistedWallBlocks.contains(s)) {
            Slabify.CONFIG.configData.forcedWallBlocks.remove(s);
        }

        if(!Slabify.CONFIG.configData.blacklistedWallBlocks.contains(s)) {
            Slabify.CONFIG.configData.blacklistedWallBlocks.add(s);
        }

        Slabify.CONFIG.save();
    }

    @Override
    protected ItemStack getCreativeModeIcon() {
        return new ItemStack(Blocks.COBBLESTONE_WALL, 1);
    }

    @Override
    public boolean isBlockBlacklisted(ResourceLocation resourceLocation) {
        return Slabify.CONFIG.configData.blacklistedWallBlocks.contains(resourceLocation) || Slabify.CONFIG.configData.blacklistedWallBlocks.contains(new ResourceLocation(resourceLocation.getNamespace(), ""));
    }

    @Override
    public boolean isBlockForced(ResourceLocation resourceLocation) {
        return Slabify.CONFIG.configData.forcedWallBlocks.contains(resourceLocation);
    }

    @Override
    public boolean shouldLoadModdedEntries() {
        return Slabify.CONFIG.configData.loadWallsForModdedBlocks;
    }

    @Override
    public Block create(Block parent, ResourceLocation resourceLocation) {
        return new SlabifyWallBlock(parent, resourceLocation);
    }
}
