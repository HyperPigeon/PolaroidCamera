package net.hyper_pigeon.polaroidcamera.client.render;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.hyper_pigeon.polaroidcamera.networking.PolaroidCameraNetworkingConstants;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

public class CameraScreen extends Screen {
    public final double defaultFOV;
    public double currentFOV;
    public double currentZoom;
    private World world;

    public CameraScreen(double fov, World world) {
        super(NarratorManager.EMPTY);
        defaultFOV = fov;
        currentFOV = defaultFOV;
        currentZoom = 1;
        this.world = world;
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.client.options.hudHidden = true;
        super.render(matrices, mouseX, mouseY, delta);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers){
        //int imageId = world.getNextMapId();
        UUID imageId = UUID.randomUUID();
        if(keyCode == GLFW.GLFW_KEY_C){

            boolean isHUDhidden = client.options.hudHidden;
            client.options.hudHidden = true;
            NativeImage nativeImage = ScreenshotRecorder.takeScreenshot(client.getFramebuffer());

            client.options.hudHidden = isHUDhidden;
            NativeImageBackedTexture nativeImageBackedTexture = new NativeImageBackedTexture(nativeImage);

            Identifier identifier = client.getTextureManager().registerDynamicTexture("camera/" + imageId,nativeImageBackedTexture);
            nativeImageBackedTexture.upload();
            PacketByteBuf buf = PacketByteBufs.create();



            //using jank way to write width and height (and framebuffer) since using array results in crash
            buf.writeDouble(nativeImage.getWidth());
            buf.writeFloat(nativeImage.getHeight());
            buf.writeUuid(imageId);
            buf.writeString(String.valueOf(identifier));

            try {
                byte[] imageBytes = nativeImage.getBytes();

                for(int i = 0; i < imageBytes.length; i+=17500) {
                    PacketByteBuf packetByteBuf = PacketByteBufs.create();
                    if(i+17500 >= imageBytes.length){
                        packetByteBuf.writeString(String.valueOf(identifier));
                        packetByteBuf.writeVarInt(imageBytes.length-i);
                        packetByteBuf.writeBytes(Arrays.copyOfRange(imageBytes,i,imageBytes.length));
                    }
                    packetByteBuf.writeString(String.valueOf(identifier));
                    packetByteBuf.writeVarInt(17500);
                    packetByteBuf.writeBytes(Arrays.copyOfRange(imageBytes,i,i+17500));
                    ClientPlayNetworking.send(PolaroidCameraNetworkingConstants.SEND_IMAGE_BYTES,packetByteBuf);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }



            ClientPlayNetworking.send(PolaroidCameraNetworkingConstants.SEND_SCREENSHOT_IMAGE,buf);

//            for (PacketByteBuf byteStoragePacket : byteStoragePackets) {
//                ClientPlayNetworking.send(CameraNetworkingConstants.SEND_IMAGE_BYTES, byteStoragePacket);
//            }
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
        this.client.options.getFovEffectScale().setValue(defaultFOV);
        super.close();
    }
}