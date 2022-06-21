package net.hyper_pigeon.polaroidcamera.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.hyper_pigeon.polaroidcamera.PolaroidCameraZoomUtil;
import net.hyper_pigeon.polaroidcamera.client.render.CameraScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(GameRenderer.class)
public class GameRendererMixin {


    @Inject(method = "getFov(Lnet/minecraft/client/render/Camera;FZ)D", at = @At("RETURN"), cancellable = true)
    private void onGetFov(Camera camera, float tickDelta, boolean changingFov,
                          CallbackInfoReturnable<Double> cir) {
        if(PolaroidCameraZoomUtil.MC.currentScreen instanceof CameraScreen) {
            CameraScreen cameraScreen = (CameraScreen) PolaroidCameraZoomUtil.MC.currentScreen;
            double fov = cir.getReturnValue();

            cir.setReturnValue(fov/cameraScreen.currentZoom);
        }
    }

}
