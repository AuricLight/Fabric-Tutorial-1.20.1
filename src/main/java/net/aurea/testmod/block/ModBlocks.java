package net.aurea.testmod.block;

import net.aurea.testmod.TestMod;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ModBlocks {

    public static final Block BLOODIRON_BLOCK = registerBlock("bloodiron_block",
            new Block(AbstractBlock.Settings.
                    copy(Blocks.IRON_BLOCK)));
    public static final Block BLOOD_ORE = registerBlock("blood_ore",
            new Block(AbstractBlock.Settings.
                    copy(Blocks.NETHER_GOLD_ORE)
                    .sounds(BlockSoundGroup.SHROOMLIGHT)));
    public static final Block BLOODIRON_GRATE = registerBlock("bloodiron_grate",
            new Block(AbstractBlock.Settings.
                    copy(Blocks.IRON_BLOCK)
                    .strength(2.4F, 1.0F)));

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, new Identifier(TestMod.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block){
        return Registry.register(Registries.ITEM, new Identifier(TestMod.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings()));
    }

    public static void registerModBlocks(){
        TestMod.LOGGER.info("Registering ModBlocks for " + TestMod.MOD_ID);
    }
}
