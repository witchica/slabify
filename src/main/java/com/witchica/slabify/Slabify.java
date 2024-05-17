package com.witchica.slabify;

import com.witchica.slabify.block.SawingTableBlock;
import com.witchica.slabify.block.SlabifySlabBlock;
import com.witchica.slabify.config.SlabifyConfiguration;
import com.witchica.slabify.item.NoNameBlockItem;
import com.witchica.slabify.item.SawItem;
import com.witchica.slabify.menu.SawingTableMenu;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Slabify implements ModInitializer {
	/**
	 * Generic Mod Things
	 */
	public static final String MOD_ID = "slabify";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static SlabifyConfiguration CONFIG;

	/**
	 * Blocks
	 */
	public static List<SlabifySlabBlock> SLABIFY_SLABS;
	public static Map<ResourceLocation, SlabifySlabBlock> IDS_TO_SLABS;
	public static Map<Block, SlabifySlabBlock> BLOCKS_TO_SLABS;
	public static Block SAWING_TABLE = new SawingTableBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CRAFTING_TABLE));

	/**
	 * Items
	 */
	public static Item IRON_SAW = new SawItem(new Item.Properties().component(DataComponents.MAX_DAMAGE, 128).component(DataComponents.MAX_STACK_SIZE, 1));
	public static Item GOLD_SAW = new SawItem(new Item.Properties().component(DataComponents.MAX_DAMAGE, 256).component(DataComponents.MAX_STACK_SIZE, 1));
	public static Item DIAMOND_SAW = new SawItem(new Item.Properties().component(DataComponents.MAX_DAMAGE, 512).component(DataComponents.MAX_STACK_SIZE, 1));

	/**
	 * Menu
	 */
	public static MenuType<SawingTableMenu> SAWING_MENU_TYPE = new MenuType<>(SawingTableMenu::new, FeatureFlags.DEFAULT_FLAGS);

	public static CreativeModeTab SLABIFY_TAB = FabricItemGroup.builder().icon(() -> new ItemStack(Blocks.BIRCH_SLAB)).title(Component.translatable("itemGroup.slabify.slabs")).build();

	public static Slabify INSTANCE;

	public Slabify() {
		INSTANCE = this;
	}

	public boolean isBlockValid(ResourceLocation name, Block block) {
		int maxProperties = block.defaultBlockState().getProperties().contains(BlockStateProperties.WATERLOGGED) ? 1 : 0;

		boolean flag = (block instanceof EntityBlock ||
				block.hasDynamicShape() ||
				name.toString().contains("slab") ||
				name.toString().contains("stair") ||
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
				isBlockBlacklisted(name) ||
				block.defaultBlockState().getProperties().size() > maxProperties);

		if(isBlockForced(name)) {
			if(flag) {
				LOGGER.error("Registering " + name + " as a forced slab will likely cause issues with the game, proceed with caution. If the game crashes, please remove this entry and try again.");
			}

			return true;
		}

		return !flag;
	}

	public boolean isBlockBlacklisted(ResourceLocation name) {
		return CONFIG.configData.blacklistedSlabBlocks.contains(name) || CONFIG.configData.blacklistedSlabBlocks.contains(new ResourceLocation(name.getNamespace(), ""));
	}

	public boolean isBlockForced(ResourceLocation name) {
		return CONFIG.configData.forcedSlabBlock.contains(name);
	}

	public void onPostInitialize() {
		List<ResourceLocation> keys = new ArrayList<>();
		for(ResourceLocation s : BuiltInRegistries.BLOCK.keySet()) {
			keys.add(s);
		}

		List<ResourceLocation> registered = new ArrayList<>();

		for(ResourceLocation s : keys) {
			Block block = BuiltInRegistries.BLOCK.get(s);

			if(!CONFIG.configData.loadSlabsForModdedBlocks && !s.getNamespace().equals("minecraft")) {
				continue;
			}

			if(isBlockValid(s, block)) {
				if(FabricLoader.getInstance().isDevelopmentEnvironment()) {
					System.out.println("Attempting to register " + s.getPath());
				}

				// Wrap with a try/catch so we can update config to block misbehaving blocks

				try {
					SlabifySlabBlock slabBlock = new SlabifySlabBlock(block);

					ResourceLocation resourceLocation = new ResourceLocation(MOD_ID, s.getNamespace() + "_" + s.getPath() + "_slab");

//					if(registered.contains(resourceLocation)) {
//						resourceLocation = new ResourceLocation(MOD_ID, s.getNamespace() + "_" + s.getPath() + "_slab");
//					}

					registered.add(resourceLocation);

					Registry.register(BuiltInRegistries.BLOCK, resourceLocation, slabBlock);
					Registry.register(BuiltInRegistries.ITEM,resourceLocation, new NoNameBlockItem(slabBlock, block, new Item.Properties()));

					IDS_TO_SLABS.put(resourceLocation, slabBlock);
					BLOCKS_TO_SLABS.put(block, slabBlock);

					SLABIFY_SLABS.add(slabBlock);
				} catch(Exception ex) {
					if (isBlockForced(s)) {
						LOGGER.error("Error registering block " + s + ", this block was added to the 'forcedSlabBlocks' configuration section, this will be removed.");
					} else {
						LOGGER.error("Error registering block " + s + " please report this issue on GitHub! The block has been added to the 'blacklistedSlabBlocks' section of the configuration file, next run should not crash now.");
					}

					ex.printStackTrace();

					if(CONFIG.configData.blacklistedSlabBlocks.contains(s)) {
						CONFIG.configData.forcedSlabBlock.remove(s);
					}

					if(!CONFIG.configData.blacklistedSlabBlocks.contains(s)) {
						CONFIG.configData.blacklistedSlabBlocks.add(s);
					}

					CONFIG.save();
				}
			}
		}
	}

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world! It's Slabify time!");

		CONFIG = new SlabifyConfiguration();
		SLABIFY_SLABS = new ArrayList<SlabifySlabBlock>();
		IDS_TO_SLABS = new HashMap<>();
		BLOCKS_TO_SLABS = new HashMap<>();

		Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(MOD_ID, "iron_saw"), IRON_SAW);
		Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(MOD_ID, "gold_saw"), GOLD_SAW);
		Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(MOD_ID, "diamond_saw"), DIAMOND_SAW);

		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, new ResourceLocation(MOD_ID, "generic"), SLABIFY_TAB);

		Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(MOD_ID, "sawing_table"), SAWING_TABLE);
		Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(MOD_ID, "sawing_table"), new BlockItem(SAWING_TABLE, new Item.Properties()));

		Registry.register(BuiltInRegistries.MENU, new ResourceLocation(MOD_ID, "sawing_menu"), SAWING_MENU_TYPE);

		ItemGroupEvents.modifyEntriesEvent(BuiltInRegistries.CREATIVE_MODE_TAB.getResourceKey(SLABIFY_TAB).get()).register(entries -> {
			entries.accept(IRON_SAW);
			entries.accept(GOLD_SAW);
			entries.accept(DIAMOND_SAW);
			entries.accept(SAWING_TABLE);
			SLABIFY_SLABS.forEach(entries::accept);
		});
	}
}