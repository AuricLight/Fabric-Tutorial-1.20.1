package net.aurea.testmod.mixin;

import net.aurea.testmod.TestMod;
import net.aurea.testmod.entity.effect.ModStatusEffect;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.Icons;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import javax.naming.Context;


 // Credits to Roseisproot for her explanation of mixins:  https://www.curseforge.com/members/roseisproot/projects  |   https://modrinth.com/user/RoseIsProot

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Shadow
    protected abstract PlayerEntity getCameraPlayer();

    /// CUSTOM HEARTS ///
    @Unique
    private static final Identifier BLEEDING_HEARTS = new Identifier(TestMod.MOD_ID, "textures/gui/bleeding_hearts.png");

    @Inject(method = "drawHeart", at = @At("HEAD"), cancellable = true)
    private void drawCustomHeart(DrawContext context, InGameHud.HeartType type, int x, int y, int v, boolean blinking, boolean halfHeart, CallbackInfo ci) {
            if (!blinking && type == InGameHud.HeartType.NORMAL && MinecraftClient.getInstance().cameraEntity instanceof PlayerEntity player && (player.hasStatusEffect(ModStatusEffect.BLEEDING) )) {
                Identifier textureId = null;
                if (player.hasStatusEffect(ModStatusEffect.BLEEDING)) {
                    int amplifier = player.getStatusEffect(ModStatusEffect.BLEEDING).getAmplifier();
                    if(amplifier>=4){
                        textureId = BLEEDING_HEARTS;
                    } else {
                        return;
                    }

                } else {
                    return;
                }
               context.drawTexture(textureId, x, y, halfHeart ? 9 : 0, v, 9, 9);
               ci.cancel();
            }
        }

    ///  CUSTOM HUNGER BAR ///
    @Unique
    private static final Identifier ICONS = new Identifier("textures/gui/icons.png"); // Vanilla

    @Unique
    private static final Identifier BLEEDING_HUNGER = new Identifier(TestMod.MOD_ID, "textures/gui/bleeding_hunger.png"); //Bleeding effect


    @Redirect(
            method = "renderStatusBars",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V"
            ),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getHungerManager()Lnet/minecraft/entity/player/HungerManager;", ordinal = 1),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getProfiler()Lnet/minecraft/util/profiler/Profiler;", ordinal=3)
            )
    )
    private void drawHungerHunch(DrawContext instance, Identifier texture, int x, int y, int u, int v, int width, int height) { //ICONS, ac, z, aa + 36, 27, 9, 9
        PlayerEntity player = MinecraftClient.getInstance().player;
        Identifier textureId;

        //assert player != null;
        if(player.hasStatusEffect(ModStatusEffect.BLEEDING)){
            textureId = BLEEDING_HUNGER;
        } else {
            textureId = ICONS;
        }

        instance.drawTexture(textureId, x, y, u , v, width, height);
    }

}
