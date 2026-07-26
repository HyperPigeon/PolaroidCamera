package net.hyper_pigeon.polaroidcamera.mixin;

import net.hyper_pigeon.polaroidcamera.PolaroidCameraZoomUtil;
import net.hyper_pigeon.polaroidcamera.client.render.CameraScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseMixin {

    @Inject(at = {@At("RETURN")}, method = {"onScroll(JDD)V"})
    private void onOnMouseScroll(long handle, double xoffset, double yoffset,
                                 CallbackInfo ci) {
        Minecraft minecraftClient = Minecraft.getInstance();
        if(minecraftClient.gui.screen() instanceof CameraScreen){
            PolaroidCameraZoomUtil.onMouseScroll((CameraScreen) minecraftClient.gui.screen(), yoffset);
        }

    }
}
