package net.hyper_pigeon.polaroidcamera.mixin;

import net.hyper_pigeon.polaroidcamera.PolaroidCameraZoomUtil;
import net.hyper_pigeon.polaroidcamera.client.render.CameraScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {

    @Inject(at = {@At("RETURN")}, method = {"onMouseScroll(JDD)V"})
    private void onOnMouseScroll(long long_1, double double_1, double double_2,
                                 CallbackInfo ci) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if(minecraftClient.currentScreen instanceof CameraScreen){
            PolaroidCameraZoomUtil.onMouseScroll((CameraScreen) minecraftClient.currentScreen,double_2);
        }

    }
}
