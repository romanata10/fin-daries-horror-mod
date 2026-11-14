package com.findaries.horror.item;

import com.findaries.horror.block.ModBlocks;
import com.findaries.horror.registration.Registration;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;

import java.util.function.Supplier;

/** Предметы мода (блоки-предметы для всех custom-блоков). */
public class ModItems {
    public static final Supplier<Item> GIANT_DIAMOND_BLOCK_ITEM =
            registerBlockItem("giant_diamond_block", ModBlocks.GIANT_DIAMOND_BLOCK);
    public static final Supplier<Item> GIANT_COBBLESTONE_BLOCK_ITEM =
            registerBlockItem("giant_cobblestone_block", ModBlocks.GIANT_COBBLESTONE_BLOCK);
    public static final Supplier<Item> GIANT_CRAFTING_TABLE_BLOCK_ITEM =
            registerBlockItem("giant_crafting_table_block", ModBlocks.GIANT_CRAFTING_TABLE_BLOCK);
    public static final Supplier<Item> GIANT_POPPY_ITEM =
            registerBlockItem("giant_poppy", ModBlocks.GIANT_POPPY_BLOCK);
    public static final Supplier<Item> GIANT_DANDELION_ITEM =
            registerBlockItem("giant_dandelion", ModBlocks.GIANT_DANDELION_BLOCK);
    public static final Supplier<Item> WONDER_PORTAL_ITEM =
            registerBlockItem("wonder_portal", ModBlocks.WONDER_PORTAL_BLOCK);
    public static final Supplier<Item> COLORFUL_DOTS_PORTAL_ITEM =
            registerBlockItem("colorful_dots_portal", ModBlocks.COLORFUL_DOTS_PORTAL_BLOCK);
    public static final Supplier<Item> WHITE_PORTAL_ITEM =
            registerBlockItem("white_portal", ModBlocks.WHITE_PORTAL_BLOCK);
    public static final Supplier<Item> ELEVATOR_ITEM =
            registerBlockItem("elevator", ModBlocks.ELEVATOR_BLOCK);

    private static Supplier<Item> registerBlockItem(final String name, final Supplier<net.minecraft.world.level.block.Block> block) {
        return Registration.ITEMS.register(name, () -> new BlockItem(block.get(), new Properties()));
    }
}
