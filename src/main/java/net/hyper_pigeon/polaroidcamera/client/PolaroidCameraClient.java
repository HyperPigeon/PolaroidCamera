package net.hyper_pigeon.polaroidcamera.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class PolaroidCameraClient implements ClientModInitializer {

    public static KeyMapping TAKE_PICTURE_KEY;

    @Override
    public void onInitializeClient() {

        TAKE_PICTURE_KEY = KeyMappingHelper.registerKeyMapping(new KeyMapping("key.polaroidcamera.take_picture", InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_C, KeyMapping.Category.register(Identifier.fromNamespaceAndPath("polaroidcamera", "polaroid_camera"))));
    }
}
