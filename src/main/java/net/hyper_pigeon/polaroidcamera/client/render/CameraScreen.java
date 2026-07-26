package net.hyper_pigeon.polaroidcamera.client.render;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.hyper_pigeon.image2map.Image2Map;
import net.hyper_pigeon.image2map.renderer.MapRenderer;
import net.hyper_pigeon.polaroidcamera.client.PolaroidCameraClient;
import net.hyper_pigeon.polaroidcamera.networking.CreateMapStatePayload;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.lwjgl.glfw.GLFW;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class CameraScreen extends Screen {
    public final double defaultFOV;
    public double currentFOV;
    public double currentZoom;
    private final Level world;

    private boolean takePicture = false;
    private boolean pendingCapture = false;

    public CameraScreen(double fov, Level world) {
        super(GameNarrator.NO_TITLE);
        defaultFOV = fov;
        currentFOV = defaultFOV;
        currentZoom = 1;
        this.world = world;
    }

    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
//        super.render(context, mouseX, mouseY, delta);
//        this.client.options.hudHidden = true;

        // First phase: on the frame the picture is requested, stop drawing the viewfinder/zoom
        // bar and defer the capture to the next frame. This guarantees the overlay (and the
        // HUD/held item hidden by the mixins) is absent from the framebuffer we grab, regardless
        // of 26.x's deferred/retained-mode GUI compositing timing.
        if(takePicture) {
            takePicture = false;
            pendingCapture = true;
            return;
        }

        if(pendingCapture) {
            pendingCapture = false;
            Screenshot.takeScreenshot(minecraft.gameRenderer.mainRenderTarget(), nativeImage -> {
                BufferedImage bufferedImage;
                try {
                    bufferedImage = new BufferedImage(nativeImage.getWidth(), nativeImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    bufferedImage.setRGB(0, 0, nativeImage.getWidth(), nativeImage.getHeight(), nativeImage.getPixels(), 0, nativeImage.getWidth());
                    bufferedImage = this.crop(bufferedImage, bufferedImage.getHeight(), bufferedImage.getHeight());

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                finally {
                    nativeImage.close();
                }

                byte scale = 0;
                MapItemSavedData mapState = MapRenderer.render(bufferedImage, Image2Map.DitherMode.FLOYD, MapItemSavedData.createForClient(scale,true,this.minecraft.level.dimension()));

                RegistryAccess manager = world.registryAccess();
                Tag encoded = MapItemSavedData.CODEC.encodeStart(manager.createSerializationContext(NbtOps.INSTANCE), mapState).getOrThrow();
                CreateMapStatePayload createMapStatePayload = new CreateMapStatePayload((CompoundTag) encoded);
                ClientPlayNetworking.send(createMapStatePayload);
            });
            this.onClose();
            return;
        }

        int width = context.guiWidth();
        int height = context.guiHeight();
        drawViewFinder(context, context.guiHeight()/2 - 10, 10, width - context.guiHeight()/2 + 10, height - 10, 2, 30);
        drawZoomBar(context,font, width - 10, height / 2 - height / 6, height / 3);


    }

    // Code copied from a much better camera mod: https://github.com/chrrs/camerapture/blob/1.21.4/common/src/client/java/me/chrr/camerapture/gui/CameraViewFinder.java
    private void drawViewFinder(GuiGraphicsExtractor context, int x1, int y1, int x2, int y2, int thickness, int length) {
        context.fill(x1, y1, x1 + length, y1 + thickness, 0xffffffff);
        context.fill(x1, y1, x1 + thickness, y1 + length, 0xffffffff);

        context.fill(x2 - length, y1, x2, y1 + thickness, 0xffffffff);
        context.fill(x2 - thickness, y1, x2, y1 + length, 0xffffffff);

        context.fill(x1, y2 - thickness, x1 + length, y2, 0xffffffff);
        context.fill(x1, y2 - length, x1 + thickness, y2, 0xffffffff);

        context.fill(x2 - length, y2 - thickness, x2, y2, 0xffffffff);
        context.fill(x2 - thickness, y2 - length, x2, y2, 0xffffffff);
    }

    private void drawZoomBar(GuiGraphicsExtractor context, Font textRenderer, int x, int y, int height) {
        int ticks = height / 10;
        for (int i = 0; i < ticks; i++) {
            int ty = y + (height * i) / (ticks - 1);
            context.fill(x - 6, ty, x, ty + 1, 0xafffffff);
        }

        float zoomProgress = 1f - (float) (this.currentZoom - 1) / (50 - 1);
        int ty = y + (int) ((float) height * zoomProgress);
        context.fill(x - 10, ty - 1, x, ty + 1, 0xffffffff);

//        String zoomLevel = String.format("%.1fx", (float)this.client.options.getFov().getValue());
//        int textWidth = textRenderer.getWidth(zoomLevel);
//        context.drawText(textRenderer, zoomLevel, x - 12 - textWidth, ty - 4, 0xffffffff, false);
    }

    public boolean keyPressed(KeyEvent input){
        int keyCode = input.input();
        if(PolaroidCameraClient.TAKE_PICTURE_KEY.matches(input)){
            takePicture = true;
        }
        if(keyCode == GLFW.GLFW_KEY_W){
            this.minecraft.player.setXRot(this.minecraft.player.getXRot()- 1);
        }
        if(keyCode ==  GLFW.GLFW_KEY_S){
            this.minecraft.player.setXRot(this.minecraft.player.getXRot()+ 1);
        }
        if(keyCode ==  GLFW.GLFW_KEY_D){
            this.minecraft.player.setYRot(this.minecraft.player.getYRot()+1);
        }
        if(keyCode ==  GLFW.GLFW_KEY_A){
            this.minecraft.player.setYRot(this.minecraft.player.getYRot()-1);
        }
        return super.keyPressed(input);
    }


    public boolean isPauseScreen() {
        return false;
    }

    public void onClose() {
        this.minecraft.options.fov().set((int) defaultFOV);
        super.onClose();
    }

    // override extractBackground to prevent the screen dimming effect (26.x retained-mode)
    public void extractBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {

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