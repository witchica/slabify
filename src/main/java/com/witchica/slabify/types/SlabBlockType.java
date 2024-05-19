package com.witchica.slabify.types;

import com.witchica.slabify.Slabify;
import com.witchica.slabify.block.SlabifySlabBlock;
import com.witchica.slabify.block.base.BaseSlabifyBlock;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;

public class SlabBlockType extends BlockTypeBase {
    public SlabBlockType() {
        super("slab");
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
        if(Slabify.CONFIG.configData.blacklistedSlabBlocks.contains(s)) {
            Slabify.CONFIG.configData.forcedSlabBlock.remove(s);
        }

        if(!Slabify.CONFIG.configData.blacklistedSlabBlocks.contains(s)) {
            Slabify.CONFIG.configData.blacklistedSlabBlocks.add(s);
        }

        Slabify.CONFIG.save();
    }

    @Override
    protected ItemStack getCreativeModeIcon() {
        return new ItemStack(Blocks.BIRCH_SLAB, 1);
    }

    @Override
    public boolean isBlockBlacklisted(ResourceLocation resourceLocation) {
        return Slabify.CONFIG.configData.blacklistedSlabBlocks.contains(resourceLocation) || Slabify.CONFIG.configData.blacklistedSlabBlocks.contains(new ResourceLocation(resourceLocation.getNamespace(), ""));
    }

    @Override
    public boolean isBlockForced(ResourceLocation resourceLocation) {
        return Slabify.CONFIG.configData.forcedSlabBlock.contains(resourceLocation);
    }

    @Override
    public boolean shouldLoadModdedEntries() {
        return Slabify.CONFIG.configData.loadSlabsForModdedBlocks;
    }

    @Override
    public Block create(Block parent, ResourceLocation resourceLocation) {
        return new SlabifySlabBlock(parent, resourceLocation);
    }

    @Override
    protected void addCustomTabContents(FabricItemGroupEntries entries) {
        entries.accept(Slabify.IRON_SAW);
        entries.accept(Slabify.GOLD_SAW);
        entries.accept(Slabify.DIAMOND_SAW);
        entries.accept(Slabify.SAWING_TABLE);
    }
}
