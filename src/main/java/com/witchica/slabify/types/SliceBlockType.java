package com.witchica.slabify.types;

import com.witchica.slabify.Slabify;
import com.witchica.slabify.block.SlabifySlabBlock;
import com.witchica.slabify.block.SlabifySliceBlock;
import com.witchica.slabify.block.base.BaseSlabifyBlock;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;

public class SliceBlockType extends BlockTypeBase {
    public SliceBlockType() {
        super("slice");
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
        if(Slabify.CONFIG.configData.blacklistedSliceBlocks.contains(s)) {
            Slabify.CONFIG.configData.forcedSliceBlocks.remove(s);
        }

        if(!Slabify.CONFIG.configData.blacklistedSliceBlocks.contains(s)) {
            Slabify.CONFIG.configData.blacklistedSliceBlocks.add(s);
        }

        Slabify.CONFIG.save();
    }

    @Override
    protected ItemStack getCreativeModeIcon() {
        if(entries() != null && !entries().isEmpty()) {
            return new ItemStack(entries().getFirst().getSelf(), 1);
        } else {
            return new ItemStack(Blocks.SNOW, 1);
        }
    }

    @Override
    public boolean isBlockBlacklisted(ResourceLocation resourceLocation) {
        return Slabify.CONFIG.configData.blacklistedSliceBlocks.contains(resourceLocation) || Slabify.CONFIG.configData.blacklistedSliceBlocks.contains(new ResourceLocation(resourceLocation.getNamespace(), ""));
    }

    @Override
    public boolean isBlockForced(ResourceLocation resourceLocation) {
        return Slabify.CONFIG.configData.forcedSliceBlocks.contains(resourceLocation);
    }

    @Override
    public boolean shouldLoadModdedEntries() {
        return Slabify.CONFIG.configData.loadSlicesForModdedBlocks;
    }

    @Override
    public Block create(Block parent, ResourceLocation resourceLocation) {
        return new SlabifySliceBlock(parent, resourceLocation);
    }

    @Override
    public int getCraftedAmount() {
        return 16;
    }
}
