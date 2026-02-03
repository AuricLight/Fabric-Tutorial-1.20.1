package net.aurea.testmod.util;

import net.aurea.testmod.TestMod;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class ModDamageTypes {

    public static final RegistryKey<DamageType> BLEEDOUT_DAMAGE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(TestMod.MOD_ID, "bleedout_damage"));

    public static DamageSource of(World world, RegistryKey<DamageType> key) {
        return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key));
    }

}
