package net.hyper_pigeon.polaroidcamera.client.render;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.hyper_pigeon.image2map.Image2Map;
import net.hyper_pigeon.image2map.renderer.MapRenderer;
import net.hyper_pigeon.polaroidcamera.client.PolaroidCameraClient;
import net.hyper_pigeon.polaroidcamera.networking.CreateMapStatePayload;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.world.World;
import org.lwjgl.glfw.GLFW;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class CameraScreen extends Screen {
    public final double defaultFOV;
    public double currentFOV;
    public double currentZoom;
    private final World world;

    private boolean takePicture = false;

    public CameraScreen(double fov, World world) {
        super(NarratorManager.EMPTY);
        defaultFOV = fov;
        currentFOV = defaultFOV;
        currentZoom = 1;
        this.world = world;
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
//        super.render(context, mouseX, mouseY, delta);
//        this.client.options.hudHidden = true;

        if(takePicture) {
            takePicture = false;
            NativeImage nativeImage = ScreenshotRecorder.takeScreenshot(client.getFramebuffer());
            byte[] imageBytes;
            BufferedImage bufferedImage;
            try {
                imageBytes = nativeImage.getBytes();

                bufferedImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
                bufferedImage = this.crop(bufferedImage, bufferedImage.getHeight(), bufferedImage.getHeight());

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            byte scale = 0;
            MapState mapState = MapRenderer.render(bufferedImage, Image2Map.DitherMode.FLOYD, MapState.of(scale,true,this.client.world.getRegistryKey()));

            NbtCompound nbtCompound = new NbtCompound();
            DynamicRegistryManager manager = world.getRegistryManager();
            mapState.writeNbt(nbtCompound,manager);
            CreateMapStatePayload createMapStatePayload = new CreateMapStatePayload(nbtCompound);
            ClientPlayNetworking.send(createMapStatePayload);
            this.close();
        }
        else {
            int width = context.getScaledWindowWidth();
            int height = context.getScaledWindowHeight();
            drawViewFinder(context, context.getScaledWindowHeight()/2 - 10, 10, width - context.getScaledWindowHeight()/2 + 10, height - 10, 2, 30);
        }


    }

    // Code copied from a much better camera mod: https://github.com/chrrs/camerapture/blob/1.21.4/common/src/client/java/me/chrr/camerapture/gui/CameraViewFinder.java
    private static void drawViewFinder(DrawContext context, int x1, int y1, int x2, int y2, int thickness, int length) {
        context.fill(x1, y1, x1 + length, y1 + thickness, 0xffffffff);
        context.fill(x1, y1, x1 + thickness, y1 + length, 0xffffffff);

        context.fill(x2 - length, y1, x2, y1 + thickness, 0xffffffff);
        context.fill(x2 - thickness, y1, x2, y1 + length, 0xffffffff);

        context.fill(x1, y2 - thickness, x1 + length, y2, 0xffffffff);
        context.fill(x1, y2 - length, x1 + thickness, y2, 0xffffffff);

        context.fill(x2 - length, y2 - thickness, x2, y2, 0xffffffff);
        context.fill(x2 - thickness, y2 - length, x2, y2, 0xffffffff);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers){
        if(PolaroidCameraClient.TAKE_PICTURE_KEY.matchesKey(keyCode,scanCode)){
            takePicture = true;
        }
        if(keyCode == GLFW.GLFW_KEY_W){
            this.client.player.setPitch(this.client.player.getPitch()- 1);
        }
        if(keyCode ==  GLFW.GLFW_KEY_S){
            this.client.player.setPitch(this.client.player.getPitch()+ 1);
        }
        if(keyCode ==  GLFW.GLFW_KEY_D){
            this.client.player.setYaw(this.client.player.getYaw()+1);
        }
        if(keyCode ==  GLFW.GLFW_KEY_A){
            this.client.player.setYaw(this.client.player.getYaw()-1);
        }
        return super.keyPressed(keyCode,scanCode,modifiers);
    }


    public boolean shouldPause() {
        return false;
    }

    public void close() {
        this.client.options.hudHidden = false;
        this.client.options.getFov().setValue((int) defaultFOV);
        super.close();
    }

    public void renderInGameBackground(DrawContext context) {

    }

    public BufferedImage crop(BufferedImage bufferedImage,int targetWidth, int targetHeight) throws IOException {
        int height = bufferedImage.getHeight();
        int width = bufferedImage.getWidth();

        // Coordinates of the image's middle
        int xc = (width - targetWidth) / 2;
        int yc = (height - targetHeight) / 2;

        // Crop
        BufferedImage croppedImage = bufferedImage.getSubimage(
                xc,
                yc,
                targetWidth, // width
                targetHeight // height
        );
        return croppedImage;
    }

}