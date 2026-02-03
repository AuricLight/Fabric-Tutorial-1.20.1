package net.aurea.testmod.entity.effect;

import net.aurea.testmod.TestMod;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class ModStatusEffect {

    public static final StatusEffect BLEEDING = registerStatusEffect("bleeding",
            new BleedingStatusEffect(StatusEffectCategory.HARMFUL, 0x660000)
                    .addAttributeModifier(EntityAttributes.GENERIC_ARMOR,
                            "720A4993-8B36-8D39-5EA7-D1B33F7CA6F9", // Uhhhh made up UUID
                            -0.25f,
                            EntityAttributeModifier.Operation.MULTIPLY_BASE)) ;


    private static StatusEffect registerStatusEffect(String name, StatusEffect statusEffect) {
        return Registry.register(Registries.STATUS_EFFECT, new Identifier(TestMod.MOD_ID, name), statusEffect);
    }


    public static void registerModStatusEffects(){
        TestMod.LOGGER.info("Registering Mod Status Effects for "+ TestMod.MOD_ID);
    }

}
