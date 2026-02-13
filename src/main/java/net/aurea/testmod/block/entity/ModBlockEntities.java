package net.aurea.testmod.block.entity;

import net.aurea.testmod.TestMod;
import net.aurea.testmod.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static final BlockEntityType<SoulCrucibleBlockEntity> SOUL_CRUCIBLE_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(TestMod.MOD_ID, "soul_crucible"),
                    FabricBlockEntityTypeBuilder.create(SoulCrucibleBlockEntity :: new,
                            ModBlocks.SOUL_CRUCIBLE).build());

    public static void registerBlockEntities() {
        TestMod.LOGGER.info("Registering Block Entities for " + TestMod.MOD_ID);
    }
}
