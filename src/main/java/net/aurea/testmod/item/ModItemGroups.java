package net.aurea.testmod.item;

import net.aurea.testmod.TestMod;
import net.aurea.testmod.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static final ItemGroup TEST_GROUP = Registry.register(Registries.ITEM_GROUP,
            new Identifier(TestMod.MOD_ID, "bloodiron"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.bloodiron"))
                .icon(() -> new ItemStack(ModItems.BloodIronIngot)).entries((displayContext, entries) -> {
                        entries.add(ModItems.BloodIronNugget);
                        entries.add(ModItems.BloodIronIngot);
                        entries.add(ModItems.BloodClot);

                        entries.add(ModItems.ElyseanLeech);

                        entries.add(ModBlocks.BLOOD_ORE);
                        entries.add(ModBlocks.BLOODIRON_BLOCK);
                        entries.add(ModBlocks.BLOODIRON_GRATE);

                        entries.add(ModBlocks.SOUL_CRUCIBLE);
                    }).build());


    public static void registerItemGroups(){
        TestMod.LOGGER.info("Registering Item Groups for "+ TestMod.MOD_ID);
    }

}
