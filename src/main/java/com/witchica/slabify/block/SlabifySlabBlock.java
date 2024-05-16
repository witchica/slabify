package com.witchica.slabify.block;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.storage.loot.LootParams;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SlabifySlabBlock extends SlabBlock {
    public final Block owner;

    public SlabifySlabBlock(Block owner) {
        super(BlockBehaviour.Properties.ofFullCopy(owner));
        this.owner = owner;


    }

    @Override
    public MutableComponent getName() {
        return owner.getName();
    }

    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.literal("Slab").withStyle(ChatFormatting.ITALIC, ChatFormatting.LIGHT_PURPLE));
        super.appendHoverText(itemStack, tooltipContext, list, tooltipFlag);
    }


    @Override
    protected List<ItemStack> getDrops(BlockState blockState, LootParams.Builder builder) {
        return List.of(new ItemStack(this, blockState.getValue(SlabBlock.TYPE) == SlabType.DOUBLE ? 2 : 1));
    }
}
