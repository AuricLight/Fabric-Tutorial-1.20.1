package net.aurea.testmod.particle;

import net.aurea.testmod.TestMod;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModParticles {
    public static final DefaultParticleType Blood_particle = FabricParticleTypes.simple();

    public static void registerParticles(){
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(TestMod.MOD_ID, "blood_particle"), Blood_particle);

    }
}
