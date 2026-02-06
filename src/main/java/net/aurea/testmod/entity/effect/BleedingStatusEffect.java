package net.aurea.testmod.entity.effect;

import net.aurea.testmod.particle.ModParticles;
import net.aurea.testmod.util.ModDamageTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class BleedingStatusEffect extends StatusEffect {

    public BleedingStatusEffect(StatusEffectCategory category, int color){
        super(category, color);
    }

    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        // In our case, we just make it return true so that it applies the status effect every tick.
        return true;
    }


    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {

        // If the amplifier is lower than 4, it will remove the saturation from players
        if (entity.isPlayer()) {
            ((PlayerEntity) entity).getHungerManager().setSaturationLevel(0);
        }

        // If amplifier is higher or equal to 4, start taking damage (unless undead)
        if (!entity.isUndead() && amplifier >= 4){
            entity.damage(ModDamageTypes.of(entity.getWorld(), ModDamageTypes.BLEEDOUT_DAMAGE), ((float) amplifier )/ 2);
        }
        World world = entity.getWorld();

        if(world instanceof ServerWorld server){
            server.spawnParticles(ModParticles.Blood_particle,
                    entity.getParticleX(0.5),
                    entity.getRandomBodyY() - 0.5,
                    entity.getParticleZ(0.5), amplifier,
                    0.1, 0.1, 0.1, 0.01);
        }

    }



}
