package com.witchica.slabify.block;

import com.witchica.slabify.Slabify;
import com.witchica.slabify.block.base.BaseSlabifyBlock;
import com.witchica.slabify.types.BlockTypeBase;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SlabifySliceBlock extends Block implements BaseSlabifyBlock, SimpleWaterloggedBlock {
    private final Block parent;
    private final ResourceLocation registeredName;

    public static final IntegerProperty HEIGHT = IntegerProperty.create("height", 1, 16);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public SlabifySliceBlock(Block parent, ResourceLocation resourceLocation) {
        super(BaseSlabifyBlock.copy(parent).noOcclusion());
        this.parent = parent;
        this.registeredName = resourceLocation;
        registerDefaultState(defaultBlockState().setValue(HEIGHT, 1).setValue(WATERLOGGED, false));
    }

    @Override
    protected VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return box(0, 0, 0, 16, blockState.getValue(HEIGHT), 16);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HEIGHT, WATERLOGGED);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.literal("Slice").withStyle(ChatFormatting.ITALIC, ChatFormatting.LIGHT_PURPLE));
        super.appendHoverText(itemStack, tooltipContext, list, tooltipFlag);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
        if(blockState.getValue(HEIGHT) > 1 && blockHitResult.getDirection() == Direction.UP && player.isShiftKeyDown() && !level.isClientSide) {
            level.setBlock(blockPos, blockState.setValue(HEIGHT, blockState.getValue(HEIGHT) - 1), Block.UPDATE_ALL);

            SoundType sound = parent.defaultBlockState().getSoundType();

            player.playNotifySound(sound.getPlaceSound(), SoundSource.BLOCKS, sound.getVolume(), 0.775f);

            if(!player.isCreative()) {
                level.addFreshEntity(new ItemEntity(level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), new ItemStack(blockState.getBlock())));
            }
            return InteractionResult.SUCCESS;
        }

        return super.useWithoutItem(blockState, level, blockPos, player, blockHitResult);
    }

    @Override
    protected boolean canBeReplaced(BlockState blockState, BlockPlaceContext blockPlaceContext) {
        int i = (Integer)blockState.getValue(HEIGHT);
        if (blockPlaceContext.getItemInHand().is(this.asItem()) && i < 16) {
            if (blockPlaceContext.replacingClickedOnBlock()) {
                return blockPlaceContext.getClickedFace() == Direction.UP;
            } else {
                return true;
            }
        }

        return false;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        BlockState blockState = blockPlaceContext.getLevel().getBlockState(blockPlaceContext.getClickedPos());
        if (blockState.is(this)) {
            int i = (Integer)blockState.getValue(HEIGHT);
            return (BlockState)blockState.setValue(HEIGHT, Math.min(16, i + 1));
        } else {
            return super.getStateForPlacement(blockPlaceContext);
        }
    }

    @Override
    protected List<ItemStack> getDrops(BlockState blockState, LootParams.Builder builder) {
        return List.of(new ItemStack(this, blockState.getValue(HEIGHT)));
    }

    @Override
    public Block getParent() {
        return parent;
    }

    @Override
    public BlockTypeBase getType() {
        return Slabify.SLICE_TYPE;
    }

    @Override
    public ResourceLocation getRegisteredName() {
        return registeredName;
    }
}
