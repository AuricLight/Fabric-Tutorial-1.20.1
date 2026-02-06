package net.aurea.testmod.particle.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

import java.util.Random;


public class BloodParticle extends SpriteBillboardParticle {

    protected BloodParticle (ClientWorld clientWorld, double x, double y, double z,
                             SpriteProvider spriteSheet, double dir_x, double dir_y, double dir_z){
        super(clientWorld, x, y, z, dir_x, dir_y, dir_z);

        this.velocityMultiplier= 0.6F;

        this.x=dir_x;
        this.y=dir_y;
        this.z=dir_z;

        this.gravityStrength = 1f;
        this.scale *= new Random().nextFloat(1f, 1.4f);

        this.maxAge = 20;
        this.setSpriteForAge(spriteSheet);

        this.red=0.5f;
        this.green=0f;
        this.blue=0f;

    }

    //Credit to roseisproot: https://www.curseforge.com/members/roseisproot/projects  |   https://modrinth.com/user/RoseIsProot

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }


    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider sprites;

        public Factory(SpriteProvider spriteSet){
            this.sprites = spriteSet;
        }

        public Particle createParticle(DefaultParticleType particleType, ClientWorld level, double x, double y, double z,
                                       double dx, double dy, double dz){
            return new BloodParticle(level, x, y, z, this.sprites, dx, dy, dz);
        }
    }


    @Override
    public void tick(){
        super.tick();
        fadeOut();
    }

    private void fadeOut(){
        this.scale *= (-1/((float)maxAge*3) * age + 1);
    }


}
