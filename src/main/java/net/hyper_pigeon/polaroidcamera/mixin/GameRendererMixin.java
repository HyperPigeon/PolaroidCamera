package net.hyper_pigeon.polaroidcamera.mixin;

import net.hyper_pigeon.polaroidcamera.client.render.CameraScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.joml.Matrix4fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    // Hide the first-person held item while the camera screen is open.
    @Inject(method = "renderItemInHand", at = @At("HEAD"), cancellable = true)
    private void hideHeldItemWhenCameraOpen(CameraRenderState cameraRenderState, float tickDelta, Matrix4fc matrix, CallbackInfo ci) {
        if (Minecraft.getInstance().gui.screen() instanceof CameraScreen) {
            ci.cancel();
        }
    }
}
