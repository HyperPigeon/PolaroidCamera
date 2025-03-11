package net.hyper_pigeon.polaroidcamera;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hyper_pigeon.polaroidcamera.items.CameraItem;
import net.hyper_pigeon.polaroidcamera.networking.CreateMapStatePayload;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.*;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class PolaroidCamera implements ModInitializer {


    public static final CameraItem CAMERA_ITEM = new CameraItem(new Item.Settings().maxCount(1));

    public static final ItemGroup POLAROID_CAMERA_GROUP = Registry.register(Registries.ITEM_GROUP, Identifier.of("polaroidcamera", "polaroidcamera_group"), FabricItemGroup.builder()
            .icon(() -> new ItemStack(CAMERA_ITEM))
            .displayName(Text.translatable("itemGroup.polaroidcamera.polaroidcamera_group"))
            .entries((context, entries) -> {
                entries.add(CAMERA_ITEM);
            })
            .build());

    @Override
    public void onInitialize() {

        Registry.register(Registries.ITEM,Identifier.of("polaroidcamera", "camera"), CAMERA_ITEM);

        PayloadTypeRegistry.playC2S().register(CreateMapStatePayload.PACKET_ID, CreateMapStatePayload.PACKET_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(CreateMapStatePayload.PACKET_ID, (payload, context) -> {
            var player = context.player();
            var world = player.getServerWorld();
            NbtCompound nbtCompound = payload.imageNBT();
            MapState mapState = MapState.fromNbt(nbtCompound,world.getRegistryManager());

            ItemStack stack = new ItemStack(Items.FILLED_MAP);
            MapIdComponent mapIdComponent = world.increaseAndGetMapId();
            player.getEntityWorld().putMapState(mapIdComponent,mapState);
            stack.set(DataComponentTypes.MAP_ID, mapIdComponent);

            if(!player.isCreative()) {
                int slot = player.getInventory().getSlotWithStack((new ItemStack(Items.MAP)));
                if(slot != -1) {
                    player.getInventory().getStack(slot).decrement(1);
                    ItemEntity itemEntity = new ItemEntity(player.getServerWorld(), player.getPos().x, player.getPos().y, player.getPos().z, stack);
                    player.getServerWorld().spawnEntity(itemEntity);
                }
            }
            else {
                ItemEntity itemEntity = new ItemEntity(player.getServerWorld(), player.getPos().x, player.getPos().y, player.getPos().z, stack);
                player.getServerWorld().spawnEntity(itemEntity);
            }







        });
    }


}