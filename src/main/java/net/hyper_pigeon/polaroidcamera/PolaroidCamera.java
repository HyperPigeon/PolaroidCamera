package net.hyper_pigeon.polaroidcamera;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hyper_pigeon.image2map.Image2Map;
import net.hyper_pigeon.image2map.renderer.MapRenderer;
import net.hyper_pigeon.polaroidcamera.items.CameraItem;
import net.hyper_pigeon.polaroidcamera.networking.PolaroidCameraNetworkingConstants;
import net.hyper_pigeon.polaroidcamera.persistent_state.ImagePersistentState;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

public class PolaroidCamera implements ModInitializer {


    public static final CameraItem CAMERA_ITEM = new CameraItem(new Item.Settings().maxCount(1).group(ItemGroup.MISC));

    @Override
    public void onInitialize() {

        Registry.register(Registry.ITEM,new Identifier("polaroidcamera", "camera"), CAMERA_ITEM);


        ServerPlayNetworking.registerGlobalReceiver(PolaroidCameraNetworkingConstants.SEND_IMAGE_BYTES, ((server, player, handler, buf, responseSender) ->
        {
            String identifier = buf.readString();
            byte[] bytes = buf.readByteArray();
            ImagePersistentState imagePersistentState = ImagePersistentState.get(player.getWorld());
            server.execute(() -> {
                if (!imagePersistentState.containsID(identifier)){
                    imagePersistentState.addByteArray(identifier,bytes);
                }
                else {
                    imagePersistentState.appendByteArray(identifier,bytes);
                }
            });
        }));

        ServerPlayNetworking.registerGlobalReceiver(PolaroidCameraNetworkingConstants.SEND_SCREENSHOT_IMAGE, (server,player, handler, buf, responseSender) -> {

            UUID imageId = buf.readUuid();
            int width = (int) buf.readDouble();
            int height = (int) buf.readFloat();
            String identifier = buf.readString(32767);


            server.execute(() -> {
                if(player.getInventory().contains(new ItemStack(Items.MAP)) || player.isCreative()) {
                    try {
                        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(ImagePersistentState.get(player.getWorld()).getByteArray(identifier)));
                        bufferedImage = this.crop(bufferedImage, bufferedImage.getHeight(), bufferedImage.getHeight());

                        ItemStack mapItemStack = MapRenderer.render(bufferedImage, Image2Map.DitherMode.FLOYD,(ServerWorld) player.getEntityWorld(),
                                player.getX(), player.getZ(), player);
                        ItemEntity itemEntity = new ItemEntity(player.world, player.getPos().x, player.getPos().y, player.getPos().z, mapItemStack);
                        player.world.spawnEntity(itemEntity);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(!player.isCreative()) {
                        int slot = player.getInventory().getSlotWithStack((new ItemStack(Items.MAP)));
                        player.getInventory().removeStack(slot);
                    }
                }
                ImagePersistentState.get(player.getWorld()).removeByteArray(identifier);

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
