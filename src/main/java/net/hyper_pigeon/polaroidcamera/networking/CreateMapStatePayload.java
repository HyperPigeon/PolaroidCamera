package net.hyper_pigeon.polaroidcamera.networking;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record CreateMapStatePayload(CompoundTag imageNBT) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CreateMapStatePayload> PACKET_ID = new CustomPacketPayload.Type<>(PolaroidCameraNetworkingConstants.CREATE_MAP_STATE);
    public static final StreamCodec<RegistryFriendlyByteBuf, CreateMapStatePayload> PACKET_CODEC = CustomPacketPayload.codec(CreateMapStatePayload::write,CreateMapStatePayload::new);

        private CreateMapStatePayload(FriendlyByteBuf buf) {
        this(buf.readNbt());
    }

    public CreateMapStatePayload(CompoundTag imageNBT) {
        this.imageNBT = imageNBT;
    }

    private void write(RegistryFriendlyByteBuf registryByteBuf) {
        registryByteBuf.writeNbt(imageNBT);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}
