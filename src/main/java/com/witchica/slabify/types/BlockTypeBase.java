package com.witchica.slabify.types;

import com.witchica.slabify.Slabify;
import com.witchica.slabify.block.base.BaseSlabifyBlock;
import com.witchica.slabify.item.NoNameBlockItem;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.ArrayList;
import java.util.List;

import static com.mojang.text2speech.Narrator.LOGGER;

public abstract class BlockTypeBase {
    private final String name;
    private CreativeModeTab tab;

    private List<BaseSlabifyBlock> registered;

    public BlockTypeBase(String name) {
        this.name = name;
        this.tab = FabricItemGroup.builder().icon(() -> getCreativeModeIcon()).title(Component.translatable("itemGroup.slabify." + name())).build();
        this.registered = new ArrayList<>();
    }

    public String name() {
        return name;
    }

    public List<BaseSlabifyBlock> entries() {
        return registered;
    }

    public void addBlockToLists(BaseSlabifyBlock created) {
        registered.add(created);
    }
    public abstract void removeForcedAndBlacklist(ResourceLocation location);

    public void registerTab() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, new ResourceLocation("slabify", "tab_" + name()), tab);

        ItemGroupEvents.modifyEntriesEvent(BuiltInRegistries.CREATIVE_MODE_TAB.getResourceKey(tab).get()).register(entries -> {
            addCustomTabContents(entries);
            entries().forEach(entry -> entries.accept(new ItemStack(entry.getSelf(), 1)));
        });
    }

    protected void addCustomTabContents(FabricItemGroupEntries entries) {

    }

    public CreativeModeTab tab() {
        return tab;
    }
    protected abstract ItemStack getCreativeModeIcon();

    public void register(ResourceLocation parentName, Block parent) {
        if(isBlockValid(parentName, parent)) {
            if(FabricLoader.getInstance().isDevelopmentEnvironment()) {
                System.out.println("Attempting to register " + parentName.getPath() + " as " + name().toLowerCase());
            }

            try {
                ResourceLocation resourceLocation = new ResourceLocation(Slabify.MOD_ID, parentName.getNamespace() + "_" + parentName.getPath() + "_" + name().toLowerCase());
                Block createdBlock = create(parent, resourceLocation);

                Registry.register(BuiltInRegistries.BLOCK, resourceLocation, createdBlock);
                Registry.register(BuiltInRegistries.ITEM,resourceLocation, new NoNameBlockItem(createdBlock, parent, new Item.Properties()));

                addBlockToLists((BaseSlabifyBlock) createdBlock);
            } catch(Exception ex) {
                if (isBlockForced(parentName)) {
                    LOGGER.error("Error registering block " + parentName + ", this block was added to the forced " + name() + " blocks  configuration section, this will be removed.");
                } else {
                    LOGGER.error("Error registering block " + parentName + " please report this issue on GitHub! The block has been added to the blacklisted " + name() + " blocks section of the configuration file, next run should not crash now.");
                }

                ex.printStackTrace();

                removeForcedAndBlacklist(parentName);
            }
        }
    }
    public boolean isBlockValid(ResourceLocation resourceLocation, Block block) {
        int maxProperties = block.defaultBlockState().getProperties().contains(BlockStateProperties.WATERLOGGED) ? 1 : 0;

        if(!resourceLocation.getNamespace().equals("minecraft") && !shouldLoadModdedEntries()) {
            return false;
        }

        boolean flag = (block instanceof EntityBlock ||
                block.hasDynamicShape() ||
                resourceLocation.toString().contains("slab") ||
                resourceLocation.toString().contains("stair") ||
                //!block.defaultBlockState().canOcclude() ||
                block instanceof BonemealableBlock ||
                block instanceof BaseFireBlock ||
                block instanceof BaseCoralPlantTypeBlock ||
                block instanceof BushBlock ||
                block instanceof FlowerPotBlock ||
                block instanceof BaseTorchBlock ||
                block instanceof BarrierBlock ||
                block instanceof MangroveRootsBlock ||
                block instanceof AirBlock ||
                block instanceof StructureVoidBlock ||
                block instanceof WebBlock ||
                block instanceof FrogspawnBlock ||
                block instanceof HangingRootsBlock ||
                block instanceof SporeBlossomBlock ||
                block instanceof CarpetBlock ||
                isBlockBlacklisted(resourceLocation) ||
                block.defaultBlockState().getProperties().size() > maxProperties);

        if(isBlockForced(resourceLocation)) {
            if(flag) {
                LOGGER.error("Registering " + name + " as a forced " + name().toLowerCase() + " will likely cause issues with the game, proceed with caution. If the game crashes, please remove this entry and try again.");
            }

            return true;
        }

        return !flag;
    }
    public abstract boolean isBlockBlacklisted(ResourceLocation resourceLocation);
    public abstract boolean isBlockForced(ResourceLocation resourceLocation);
    public abstract boolean shouldLoadModdedEntries();
    public abstract int getCraftedAmount();

    public abstract Block create(Block parent, ResourceLocation resourceLocation);
}
