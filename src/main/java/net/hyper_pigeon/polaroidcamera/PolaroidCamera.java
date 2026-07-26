package net.hyper_pigeon.polaroidcamera;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hyper_pigeon.polaroidcamera.items.CameraItem;
import net.hyper_pigeon.polaroidcamera.networking.CreateMapStatePayload;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class PolaroidCamera implements ModInitializer {

    public static final ResourceKey<Item> CAMERA_ITEM_KEY = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("polaroidcamera", "camera"));

    public static final CameraItem CAMERA_ITEM = new CameraItem(new Item.Properties().stacksTo(1).setId(CAMERA_ITEM_KEY));

    public static final CreativeModeTab POLAROID_CAMERA_GROUP = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, Identifier.fromNamespaceAndPath("polaroidcamera", "polaroidcamera_group"), CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .icon(() -> new ItemStack(CAMERA_ITEM))
            .title(Component.translatable("itemGroup.polaroidcamera.polaroidcamera_group"))
            .displayItems((context, entries) -> {
                entries.accept(CAMERA_ITEM);
            })
            .build());

    @Override
    public void onInitialize() {

        Registry.register(BuiltInRegistries.ITEM, CAMERA_ITEM_KEY, CAMERA_ITEM);

        PayloadTypeRegistry.serverboundPlay().register(CreateMapStatePayload.PACKET_ID, CreateMapStatePayload.PACKET_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(CreateMapStatePayload.PACKET_ID, (payload, context) -> {
            var player = context.player();
            var world = player.level();
            CompoundTag nbtCompound = payload.imageNBT();
            MapItemSavedData mapState = MapItemSavedData.CODEC.parse(world.registryAccess().createSerializationContext(NbtOps.INSTANCE), nbtCompound).getOrThrow();

            ItemStack stack = new ItemStack(Items.FILLED_MAP);
            MapId mapIdComponent = world.getFreeMapId();
            world.setMapData(mapIdComponent,mapState);
            stack.set(DataComponents.MAP_ID, mapIdComponent);

            if(!player.isCreative()) {
                int slot = player.getInventory().findSlotMatchingItem((new ItemStack(Items.MAP)));
                if(slot != -1) {
                    player.getInventory().getItem(slot).shrink(1);
                    ItemEntity itemEntity = new ItemEntity(player.level(), player.position().x, player.position().y, player.position().z, stack);
                    player.level().addFreshEntity(itemEntity);
                }
            }
            else {
                ItemEntity itemEntity = new ItemEntity(player.level(), player.position().x, player.position().y, player.position().z, stack);
                player.level().addFreshEntity(itemEntity);
            }
        });
    }


}