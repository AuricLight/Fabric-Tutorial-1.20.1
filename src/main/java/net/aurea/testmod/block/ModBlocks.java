package net.aurea.testmod.block;

import net.aurea.testmod.TestMod;
import net.aurea.testmod.block.custom.CopperCauldronBlock;
import net.aurea.testmod.block.custom.GrateBlock;
import net.aurea.testmod.block.custom.SoulCrucibleBlock;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public class ModBlocks {

    // NORMAL BLOCKS
    public static final Block BLOODIRON_BLOCK = registerBlock("bloodiron_block",
            new Block(AbstractBlock.Settings.
                    copy(Blocks.IRON_BLOCK)
                    .strength(3.5F, 1.5F)
                    .mapColor(MapColor.DULL_PINK)));

    // ORES
    public static final Block BLOOD_ORE = registerBlock("blood_ore",
            new ExperienceDroppingBlock(AbstractBlock.Settings.
                    copy(Blocks.NETHER_GOLD_ORE)
                    .sounds(BlockSoundGroup.SHROOMLIGHT),
                UniformIntProvider.create(0,1)));


    // Crafting stations
    public static final Block SOUL_CRUCIBLE = registerBlock("soul_crucible",
            new SoulCrucibleBlock(AbstractBlock.Settings
                    .copy(Blocks.COPPER_BLOCK)
                    .nonOpaque()
                    .mapColor(MapColor.DARK_AQUA)));

    // DECORATION
    public static final Block BLOODIRON_GRATE = registerBlock("bloodiron_grate",
            new GrateBlock(AbstractBlock.Settings.
                    copy(Blocks.IRON_BARS)
                    .strength(2.4F, 1.0F)
                    .mapColor(MapColor.DULL_PINK)));
    // CAULDRONS
        // UNOXIDIZED - UNWAXED
    public static final Block COPPER_CAULDRON = registerBlock("copper_cauldron",
            new CopperCauldronBlock(AbstractBlock.Settings.
                    copy(Blocks.CUT_COPPER)
                    .strength(3.5F, 1.5F)));
    public static final Block COPPER_CAULDRON_WATER = registerBlock("copper_cauldron_water",
            new LeveledCauldronBlock(AbstractBlock.Settings.copy(COPPER_CAULDRON), LeveledCauldronBlock.RAIN_PREDICATE, CauldronBehavior.WATER_CAULDRON_BEHAVIOR)
    );
    public static final Block COPPER_CAULDRON_LAVA = registerBlock("copper_cauldron_lava",
            new LavaCauldronBlock(AbstractBlock.Settings.copy(COPPER_CAULDRON).luminance(state -> 15))
    );
    public static final Block COPPER_CAULDRON_SNOW = registerBlock("copper_cauldron_snow",
            new PowderSnowCauldronBlock(AbstractBlock.Settings.copy(COPPER_CAULDRON), LeveledCauldronBlock.SNOW_PREDICATE, CauldronBehavior.POWDER_SNOW_CAULDRON_BEHAVIOR)
    );




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
