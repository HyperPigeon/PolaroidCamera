package net.hyper_pigeon.polaroidcamera.mixin;

import net.hyper_pigeon.polaroidcamera.PolaroidCameraZoomUtil;
import net.hyper_pigeon.polaroidcamera.client.render.CameraScreen;
import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public class CameraMixin {

    @Inject(method = "calculateFov", at = @At("RETURN"), cancellable = true)
    private void onCameraZoom(CallbackInfoReturnable<Float> cir) {
        if(PolaroidCameraZoomUtil.MC.gui.screen() instanceof CameraScreen) {
            CameraScreen cameraScreen = (CameraScreen) PolaroidCameraZoomUtil.MC.gui.screen();
            float fov = cir.getReturnValue();

            cir.setReturnValue((float) (fov/cameraScreen.currentZoom));
        }
    }

}
