package com.witchica.slabify;

import com.witchica.slabify.block.SawingTableBlock;
import com.witchica.slabify.block.SlabifySlabBlock;
import com.witchica.slabify.block.SlabifyWallBlock;
import com.witchica.slabify.block.base.BaseSlabifyBlock;
import com.witchica.slabify.config.SlabifyConfiguration;
import com.witchica.slabify.item.NoNameBlockItem;
import com.witchica.slabify.item.SawItem;
import com.witchica.slabify.menu.SawingTableMenu;
import com.witchica.slabify.types.BlockTypeBase;
import com.witchica.slabify.types.SlabBlockType;
import com.witchica.slabify.types.WallBlockType;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.impl.tag.convention.v2.TagRegistration;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.tags.BlockTags;
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
	public static Map<Block, List<BaseSlabifyBlock>> BLOCKS_TO_CHILDREN;

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

	public static Slabify INSTANCE;

	public Slabify() {
		INSTANCE = this;
	}

	public static BlockTypeBase SLAB_TYPE = new SlabBlockType();
	public static BlockTypeBase WALL_TYPE = new WallBlockType();

	public static BlockTypeBase[] BLOCK_TYPES = new BlockTypeBase[] {SLAB_TYPE,  WALL_TYPE};


	public void onPostInitialize() {
		List<ResourceLocation> keys = new ArrayList<>();
		for(ResourceLocation s : BuiltInRegistries.BLOCK.keySet()) {
			keys.add(s);
		}

		for(BlockTypeBase blockType : BLOCK_TYPES) {
			for(ResourceLocation s : keys) {
				Block baseBlock = BuiltInRegistries.BLOCK.get(s);
				blockType.register(s, baseBlock);
			}

			blockType.registerTab();
		}
	}

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world! It's Slabify time!");

		CONFIG = new SlabifyConfiguration();
		BLOCKS_TO_CHILDREN = new HashMap<>();

		Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(MOD_ID, "iron_saw"), IRON_SAW);
		Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(MOD_ID, "gold_saw"), GOLD_SAW);
		Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(MOD_ID, "diamond_saw"), DIAMOND_SAW);

		Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(MOD_ID, "sawing_table"), SAWING_TABLE);
		Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(MOD_ID, "sawing_table"), new BlockItem(SAWING_TABLE, new Item.Properties()));

		Registry.register(BuiltInRegistries.MENU, new ResourceLocation(MOD_ID, "sawing_menu"), SAWING_MENU_TYPE);
	}
}