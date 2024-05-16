package com.witchica.slabify.menu;

import com.witchica.slabify.Slabify;
import com.witchica.slabify.block.SlabifySlabBlock;
import com.witchica.slabify.item.SawItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.joml.Math;

import java.util.Optional;

public class SawingTableMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess containerLevelAccess;
    private final SimpleContainer craftingSlots = new SimpleContainer(2) {
        @Override
        public void setChanged() {
            super.setChanged();
            slotsChanged(this);
        }
    };
    private final SimpleContainer potentialResults = new SimpleContainer(6) {
        @Override
        public void setChanged() {
            super.setChanged();
            slotsChanged(this);
        }
    };

    public SawingTableMenu(int syncId, Inventory inventory, ContainerLevelAccess containerLevelAccess) {
        super(Slabify.SAWING_MENU_TYPE, syncId);
        this.containerLevelAccess = containerLevelAccess;

        this.addSlot(new Slot(craftingSlots, 0, 8, 35) {
            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return itemStack.getItem() instanceof BlockItem;
            }
        });
        this.addSlot(new Slot(craftingSlots, 1, 44, 35) {
            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return itemStack.getItem() instanceof SawItem;
            }
        });

        for(int i = 0; i < 2; i++) {
            for(int j = 0; j < 3; j++) {
                this.addSlot(new Slot(potentialResults, j + i * 3, 116 + (j * 18), 26 + (i * 18)) {
                    @Override
                    public boolean mayPlace(ItemStack itemStack) {
                        return false;
                    }

                    @Override
                    public void onTake(Player player, ItemStack itemStack) {
                        super.onTake(player, itemStack);
                        craftingResultTaken(this.index, player, itemStack);
                    }

                    @Override
                    public void onQuickCraft(ItemStack itemStack, ItemStack itemStack2) {
                        super.onQuickCraft(itemStack, itemStack2);
                    }

                    @Override
                    protected void onQuickCraft(ItemStack itemStack, int i) {
                        super.onQuickCraft(itemStack, i);
                    }

                    @Override
                    public Optional<ItemStack> tryRemove(int i, int j, Player player) {
                        return super.tryRemove(getItem().getCount(), getItem().getCount(), player);
                    }
                });
            }
        }

        //The player inventory
        for (int m = 0; m < 3; ++m) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(inventory, l + m * 9 + 9, 8 + l * 18, 84 + m * 18));
            }
        }
        //The player Hotbar
        for (int m = 0; m < 9; ++m) {
            this.addSlot(new Slot(inventory, m, 8 + m * 18, 142));
        }
    }

    public SawingTableMenu(int syncId, Inventory inventory) {
        this(syncId, inventory, ContainerLevelAccess.NULL);
    }

    private void craftingResultTaken(int index, Player player, ItemStack itemStack) {
        if(player instanceof ServerPlayer) {
            ItemStack inputBlock = craftingSlots.getItem(0);
            inputBlock.setCount(inputBlock.getCount()-1);
            craftingSlots.setItem(0, inputBlock);

            ItemStack saw = craftingSlots.getItem(1);
            saw.setDamageValue(saw.getDamageValue() + (itemStack.getCount() / 2));

            if(saw.getDamageValue() >= saw.getMaxDamage()) {
                craftingSlots.setItem(1, ItemStack.EMPTY);
                player.playNotifySound(SoundEvents.ITEM_BREAK, SoundSource.BLOCKS, 1f, 1f);
            }
        }
    }

    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);

        if(container == craftingSlots) {
            containerLevelAccess.execute((level, blockPos) -> {
                if(level.isClientSide) {
                    return;
                }

                ItemStack potentialBlock = craftingSlots.getItem(0);
                ItemStack saw = craftingSlots.getItem(1);
                if(potentialBlock.isEmpty() || saw.isEmpty() || !(potentialBlock.getItem() instanceof BlockItem)) {
                    potentialResults.clearContent();
                    return;
                }

                BlockItem blockItem = (BlockItem) potentialBlock.getItem();

                if(Slabify.BLOCKS_TO_SLABS.containsKey(blockItem.getBlock())) {
                    SlabifySlabBlock block = Slabify.BLOCKS_TO_SLABS.get(blockItem.getBlock());
                    int amount = 2;
                    potentialResults.setItem(0, new ItemStack(block, amount));
                }
            });
        }
    }

    // 0 is block
    // 1 is saw
    // 2, 3, 4, 5, 6, 7 are results
    // 8 - 34 inventory
    // 35- 43 hotbar


    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(i);
        if (slot != null && slot.hasItem()) {
            ItemStack itemStack2 = slot.getItem();
            itemStack = itemStack2.copy();
            if (i == 0) {
                this.containerLevelAccess.execute((level, blockPos) -> itemStack2.getItem().onCraftedBy(itemStack2, (Level)level, player));
                if (!this.moveItemStackTo(itemStack2, 8, 34, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemStack2, itemStack);
            } else if (i >= 8 && i < 45 ? !this.moveItemStackTo(itemStack2, 0, 2, false) && (i < 35 ? !this.moveItemStackTo(itemStack2, 35, 43, false) : !this.moveItemStackTo(itemStack2, 8, 34, false)) : !this.moveItemStackTo(itemStack2, 8, 34, false)) {
                return ItemStack.EMPTY;
            }
            if (itemStack2.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, itemStack);
            if (i == 0) {
                player.drop(itemStack2, false);
            }
        }
        return itemStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.containerLevelAccess.execute((level, blockPos) -> this.clearContainer(player, this.craftingSlots));
    }

}
