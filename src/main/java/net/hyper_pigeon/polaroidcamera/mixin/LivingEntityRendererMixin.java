package net.hyper_pigeon.polaroidcamera.mixin;

import net.hyper_pigeon.polaroidcamera.PolaroidCamera;
import net.hyper_pigeon.polaroidcamera.duck.CameraHolder;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {

    @Inject(method = "updateRenderState", at = @At("TAIL"))
    private void captureCameraHold(LivingEntity entity, LivingEntityRenderState state, float tickDelta, CallbackInfo ci) {
        ((CameraHolder) state).setHoldingCamera(entity.getMainHandStack().getItem() == PolaroidCamera.CAMERA_ITEM);
    }
}
