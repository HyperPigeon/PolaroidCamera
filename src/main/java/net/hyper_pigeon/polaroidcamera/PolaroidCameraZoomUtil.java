package net.hyper_pigeon.polaroidcamera;

import net.hyper_pigeon.polaroidcamera.client.render.CameraScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;

public class PolaroidCameraZoomUtil {
    public static final MinecraftClient MC = MinecraftClient.getInstance();

    //https://github.com/Wurst-Imperium/WI-Zoom/blob/99e03b633bd79160372e2d191e64e0e3759ea1d8/src/main/java/net/wurstclient/zoom/WiZoom.java#L46
    public static void onMouseScroll(CameraScreen screen, double amount)
    {
        if(amount > 0)
            screen.currentFOV *= 1.1;
        else if(amount < 0)
            screen.currentFOV *= 0.9;

        screen.currentFOV = MathHelper.clamp(screen.currentFOV, 1, screen.defaultFOV);
        MC.options.fov = screen.currentFOV;
    }



    public static void onMouseScroll(double amount){
        if(amount > 0)
            MC.options.fov *= 1.1;
        else if(amount < 0)
            MC.options.fov *= 0.9;

        MC.options.fov = MathHelper.clamp(MC.options.fov, 1, 100);
    }
}
