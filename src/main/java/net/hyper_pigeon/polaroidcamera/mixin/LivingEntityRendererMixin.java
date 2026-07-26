package net.hyper_pigeon.polaroidcamera.mixin;

import net.hyper_pigeon.polaroidcamera.PolaroidCamera;
import net.hyper_pigeon.polaroidcamera.duck.CameraHolder;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void captureCameraHold(LivingEntity entity, LivingEntityRenderState state, float tickDelta, CallbackInfo ci) {
        ((CameraHolder) state).setHoldingCamera(entity.getMainHandItem().getItem() == PolaroidCamera.CAMERA_ITEM);
    }
}
