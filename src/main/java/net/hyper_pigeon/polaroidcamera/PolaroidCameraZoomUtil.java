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
            screen.currentZoom *= 1.1;
        else if(amount < 0)
            screen.currentZoom *= 0.9;

        screen.currentZoom = MathHelper.clamp(screen.currentZoom, 1, 50);

    }

//    public static double getCameraFOV(CameraScreen screen){
//        return screen.defaultFOV / screen.currentZoom;
//    }



//    public static void onMouseScroll(double amount){
//        if(amount > 0)
//            MC.options.getFov().setValue((int) (MC.options.getFov().getValue()*1.1));
//        else if(amount < 0)
//            MC.options.getFov().setValue((int) (MC.options.getFov().getValue()*0.9));
//
//        MC.options.getFov().setValue(MathHelper.clamp(MC.options.getFov().getValue(), 1, 100));
//    }
}
