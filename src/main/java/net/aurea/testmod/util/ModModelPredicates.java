package net.aurea.testmod.util;

import net.aurea.testmod.TestMod;
import net.aurea.testmod.item.ModItems;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.util.Identifier;

public class ModModelPredicates {

    public static void registerModModels(){
        registerLeech();
    }

    private static void registerLeech(){
        ModelPredicateProviderRegistry.register(ModItems.ElyseanLeech, new Identifier(TestMod.MOD_ID, "awoken"),
                (stack, world, entity, seed) ->  stack.getOrCreateNbt().getBoolean("Awake") ? 1.0f : 0.0f);

    }
}
