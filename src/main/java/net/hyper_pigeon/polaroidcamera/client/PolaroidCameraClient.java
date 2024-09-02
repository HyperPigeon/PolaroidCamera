package net.hyper_pigeon.polaroidcamera.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.hyper_pigeon.image2map.Image2Map;
import net.hyper_pigeon.image2map.renderer.MapRenderer;
import net.hyper_pigeon.polaroidcamera.networking.CreateMapStatePayload;
import net.hyper_pigeon.polaroidcamera.networking.PolaroidCameraNetworkingConstants;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import org.lwjgl.glfw.GLFW;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Objects;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class PolaroidCameraClient implements ClientModInitializer {

    public static KeyBinding TAKE_PICTURE_KEY;

    @Override
    public void onInitializeClient() {

        TAKE_PICTURE_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.polaroidcamera.take_picture", InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_C, "Polaroid Camera"));
    }
}
