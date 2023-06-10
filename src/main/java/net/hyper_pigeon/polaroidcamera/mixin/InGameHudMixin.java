package net.hyper_pigeon.polaroidcamera.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.hyper_pigeon.polaroidcamera.client.render.CameraScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Shadow @Final private MinecraftClient client;

    @Environment(EnvType.CLIENT)
    @Inject(at = {@At("HEAD")},method = {"render"}, cancellable = true)
    private void renderCameraScreenWithoutHud(DrawContext drawContext, float tickDelta, CallbackInfo ci){
       if(this.client.currentScreen instanceof CameraScreen){
           ci.cancel();
       }
    }
}
