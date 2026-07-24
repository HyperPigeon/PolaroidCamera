package net.hyper_pigeon.polaroidcamera.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class PolaroidCameraClient implements ClientModInitializer {

    public static KeyBinding TAKE_PICTURE_KEY;

    @Override
    public void onInitializeClient() {

        TAKE_PICTURE_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.polaroidcamera.take_picture", InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_C, KeyBinding.Category.create(Identifier.of("polaroidcamera", "polaroid_camera"))));
    }
}
