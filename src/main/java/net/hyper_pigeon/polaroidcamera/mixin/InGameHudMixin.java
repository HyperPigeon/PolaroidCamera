package net.hyper_pigeon.polaroidcamera.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.hyper_pigeon.polaroidcamera.client.render.CameraScreen;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Hud.class)
public abstract class InGameHudMixin {

    // hide hud while camera screen is open
    @Environment(EnvType.CLIENT)
    @Inject(at = {@At("HEAD")}, method = {"extractRenderState"}, cancellable = true)
    private void renderCameraScreenWithoutHud(GuiGraphicsExtractor context, DeltaTracker tickCounter, CallbackInfo ci){
       if(Minecraft.getInstance().gui.screen() instanceof CameraScreen){
           ci.cancel();
       }
    }
}
