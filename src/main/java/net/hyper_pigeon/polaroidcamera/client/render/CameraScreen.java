package net.hyper_pigeon.polaroidcamera.client.render;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.hyper_pigeon.polaroidcamera.client.PolaroidCameraClient;
import net.hyper_pigeon.polaroidcamera.networking.PolaroidCameraNetworkingConstants;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.world.World;
import org.lwjgl.glfw.GLFW;

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
        if(keyCode == PolaroidCameraClient.TAKE_PICTURE_KEY.getDefaultKey().getCode()){
            PacketByteBuf buf = PacketByteBufs.create();
            ClientPlayNetworking.send(PolaroidCameraNetworkingConstants.CREATE_MAP_STATE, buf);
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