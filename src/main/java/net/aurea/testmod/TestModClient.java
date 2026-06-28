package net.aurea.testmod;

import net.aurea.testmod.block.ModBlocks;
import net.aurea.testmod.block.entity.ModBlockEntities;
import net.aurea.testmod.block.entity.renderer.SoulCrucibleBlockEntityRenderer;
import net.aurea.testmod.particle.ModParticles;
import net.aurea.testmod.particle.custom.BloodParticle;
import net.aurea.testmod.util.ModModelPredicates;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class TestModClient implements ClientModInitializer{
    @Override
    public void onInitializeClient() {

        // Item textures
        ModModelPredicates.registerModModels();

        // Particles
        ParticleFactoryRegistry.getInstance().register(ModParticles.Blood_particle, BloodParticle.Factory::new);

        // Blocks
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BLOODIRON_GRATE, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SOUL_CRUCIBLE, RenderLayer.getCutout());

        // Block Entity Renderers (BER)
        BlockEntityRendererFactories.register(ModBlockEntities.SOUL_CRUCIBLE_BLOCK_ENTITY, SoulCrucibleBlockEntityRenderer::new);

    }
}
