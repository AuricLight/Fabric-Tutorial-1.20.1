package net.aurea.testmod.item;

import net.aurea.testmod.TestMod;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    //Declare the item
    public static final Item BloodIronIngot = registerItem("bloodiron_ingot", new Item(new FabricItemSettings()));
    public static final Item BloodIronNugget = registerItem("bloodiron_nugget", new Item(new FabricItemSettings()));

    // Add items to vanilla tabs
    private static void addItemsToIngredientItemGroup(FabricItemGroupEntries entries ) {
        entries.add(BloodIronIngot);
        entries.add(BloodIronNugget);
    }


    private static Item registerItem(String name, Item item){
        return Registry.register(Registries.ITEM, new Identifier(TestMod.MOD_ID, name), item);
    }

    public static void registerModItems(){
        TestMod.LOGGER.info("Registering Mod Items for "+ TestMod.MOD_ID);

        //Add items to vanilla tabs
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(ModItems::addItemsToIngredientItemGroup);
    }

}
