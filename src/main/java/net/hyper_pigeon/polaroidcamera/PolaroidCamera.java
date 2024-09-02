package net.hyper_pigeon.polaroidcamera;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hyper_pigeon.polaroidcamera.items.CameraItem;
import net.hyper_pigeon.polaroidcamera.networking.CreateMapStatePayload;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class PolaroidCamera implements ModInitializer {


    public static final CameraItem CAMERA_ITEM = new CameraItem(new Item.Settings().maxCount(1));

    @Override
    public void onInitialize() {

        Registry.register(Registries.ITEM,Identifier.of("polaroidcamera", "camera"), CAMERA_ITEM);

//        ItemGroupEvents
//                .modifyEntriesEvent(ItemGroups.TOOLS)
//                .register((itemGroup) -> itemGroup.add(PolaroidCamera.CAMERA_ITEM));
        PayloadTypeRegistry.playC2S().register(CreateMapStatePayload.PACKET_ID, CreateMapStatePayload.PACKET_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(CreateMapStatePayload.PACKET_ID, (payload, context) -> {
            var world = context.player().getServerWorld();
            NbtCompound nbtCompound = payload.imageNBT();
            MapState mapState = MapState.fromNbt(nbtCompound,world.getRegistryManager());

            ItemStack stack = new ItemStack(Items.FILLED_MAP);
            MapIdComponent mapIdComponent = world.increaseAndGetMapId();
            context.player().getEntityWorld().putMapState(mapIdComponent,mapState);
            stack.set(DataComponentTypes.MAP_ID, mapIdComponent);

            ItemEntity itemEntity = new ItemEntity(context.player().getServerWorld(), context.player().getPos().x, context.player().getPos().y, context.player().getPos().z, stack);
            context.player().getServerWorld().spawnEntity(itemEntity);

        });
    }


}