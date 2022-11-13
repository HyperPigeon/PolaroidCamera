package net.hyper_pigeon.polaroidcamera.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.hyper_pigeon.image2map.Image2Map;
import net.hyper_pigeon.image2map.renderer.MapRenderer;
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

        TAKE_PICTURE_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.polaroid_camera.take_picture", InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_C, "Polaroid Camera"));

        ClientPlayNetworking.registerGlobalReceiver(PolaroidCameraNetworkingConstants.CREATE_PICTURE,(client, handler, buf, responseSender) -> {
            int mapID = buf.readInt();
            MapState mapState = MapState.fromNbt(Objects.requireNonNull(buf.readNbt()));
            client.execute(() -> {
                boolean isHUDhidden = client.options.hudHidden;
                client.options.hudHidden = true;
                NativeImage nativeImage = ScreenshotRecorder.takeScreenshot(client.getFramebuffer());
                client.options.hudHidden = isHUDhidden;

                try {
                    byte[] imageBytes = nativeImage.getBytes();
                    BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
                    bufferedImage = this.crop(bufferedImage, bufferedImage.getHeight(), bufferedImage.getHeight());

                    MapState mapState1  = MapRenderer.render(bufferedImage, Image2Map.DitherMode.FLOYD, mapID, mapState);

                    PacketByteBuf packetByteBuf = PacketByteBufs.create();
                    NbtCompound nbtCompound = new NbtCompound();

                    packetByteBuf.writeInt(mapID);
                    packetByteBuf.writeNbt(mapState1.writeNbt(nbtCompound));

                    ClientPlayNetworking.send(PolaroidCameraNetworkingConstants.SPAWN_PICTURE,packetByteBuf);


                } catch (IOException e) {
                    e.printStackTrace();
                }


            });
        });
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
