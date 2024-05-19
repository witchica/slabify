package com.witchica.slabify.block;

import com.witchica.slabify.Slabify;
import com.witchica.slabify.block.base.BaseSlabifyBlock;
import com.witchica.slabify.types.BlockTypeBase;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.storage.loot.LootParams;

import java.util.List;

public class SlabifyWallBlock extends WallBlock implements BaseSlabifyBlock {
    public final Block owner;
    private final ResourceLocation registeredName;

    public SlabifyWallBlock(Block owner, ResourceLocation registeredName) {
        super(BaseSlabifyBlock.copy(owner).forceSolidOn());
        this.owner = owner;
        this.registeredName = registeredName;
    }

    @Override
    public MutableComponent getName() {
        return owner.getName();
    }

    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.literal("Wall").withStyle(ChatFormatting.ITALIC, ChatFormatting.LIGHT_PURPLE));
        super.appendHoverText(itemStack, tooltipContext, list, tooltipFlag);
    }


    @Override
    protected List<ItemStack> getDrops(BlockState blockState, LootParams.Builder builder) {
        return List.of(new ItemStack(this, 1));
    }

    @Override
    public Block getParent() {
        return owner;
    }

    @Override
    public BlockTypeBase getType() {
        return Slabify.WALL_TYPE;
    }

    @Override
    public ResourceLocation getRegisteredName() {
        return registeredName;
    }
}
