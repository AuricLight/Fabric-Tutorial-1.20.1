package net.aurea.testmod;

import net.aurea.testmod.particle.ModParticles;
import net.aurea.testmod.particle.custom.BloodParticle;
import net.aurea.testmod.util.ModModelPredicates;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

public class TestModClient implements ClientModInitializer{
    @Override
    public void onInitializeClient() {

        ModModelPredicates.registerModModels();

        ParticleFactoryRegistry.getInstance().register(ModParticles.Blood_particle, BloodParticle.Factory::new);
    }
}
