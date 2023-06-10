package net.hyper_pigeon.polaroidcamera;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hyper_pigeon.polaroidcamera.items.CameraItem;
import net.hyper_pigeon.polaroidcamera.networking.PolaroidCameraNetworkingConstants;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.Objects;

public class PolaroidCamera implements ModInitializer {


    public static final CameraItem CAMERA_ITEM = new CameraItem(new Item.Settings().maxCount(1));

    @Override
    public void onInitialize() {

        Registry.register(Registries.ITEM,new Identifier("polaroidcamera", "camera"), CAMERA_ITEM);

        ServerPlayNetworking.registerGlobalReceiver(PolaroidCameraNetworkingConstants.CREATE_MAP_STATE, ((server, player, handler, buf, responseSender) -> {

            server.execute(() -> {
                int id = player.getEntityWorld().getNextMapId();
                NbtCompound nbt = new NbtCompound();
                nbt.putString("dimension", player.getEntityWorld().getRegistryKey().getValue().toString());
                nbt.putInt("xCenter", (int) player.getX());
                nbt.putInt("zCenter", (int) player.getZ());
                nbt.putBoolean("locked", true);
                nbt.putBoolean("unlimitedTracking", false);
                nbt.putBoolean("trackingPosition", false);
                nbt.putByte("scale", (byte) 3);
                MapState state = MapState.fromNbt(nbt);

                //player.getEntityWorld().putMapState(FilledMapItem.getMapName(id), state);

                NbtCompound nbtCompound = new NbtCompound();
                nbtCompound = state.writeNbt(nbtCompound);

                PacketByteBuf packetByteBuf = PacketByteBufs.create();

                packetByteBuf.writeInt(id);
                packetByteBuf.writeNbt(nbtCompound);

                ServerPlayNetworking.send(player,PolaroidCameraNetworkingConstants.CREATE_PICTURE,packetByteBuf);
                });
        }));

        ServerPlayNetworking.registerGlobalReceiver(PolaroidCameraNetworkingConstants.SPAWN_PICTURE, ((server, player, handler, buf, responseSender) -> {

            int mapId = buf.readInt();
            MapState mapState = MapState.fromNbt(Objects.requireNonNull(buf.readNbt()));

            server.execute(() -> {

                ItemStack stack = new ItemStack(Items.FILLED_MAP);
                player.getEntityWorld().putMapState(FilledMapItem.getMapName(mapId),mapState);
                stack.getOrCreateNbt().putInt("map", mapId);

                if(!player.isCreative()) {
                    int slot = player.getInventory().getSlotWithStack((new ItemStack(Items.MAP)));
                    if(slot != -1) {
                        player.getInventory().getStack(slot).decrement(1);

                        ItemEntity itemEntity = new ItemEntity(player.world, player.getPos().x, player.getPos().y, player.getPos().z, stack);
                        player.world.spawnEntity(itemEntity);
                    }
                }
                else {
                    ItemEntity itemEntity = new ItemEntity(player.world, player.getPos().x, player.getPos().y, player.getPos().z, stack);
                    player.world.spawnEntity(itemEntity);
                }

            });


        }));


    }


}