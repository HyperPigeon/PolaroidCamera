package net.hyper_pigeon.polaroidcamera.networking;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record CreateMapStatePayload(NbtCompound imageNBT) implements CustomPayload {
    public static final CustomPayload.Id<CreateMapStatePayload> PACKET_ID = new CustomPayload.Id<>(PolaroidCameraNetworkingConstants.CREATE_MAP_STATE);
    public static final PacketCodec<RegistryByteBuf, CreateMapStatePayload> PACKET_CODEC = CustomPayload.codecOf(CreateMapStatePayload::write,CreateMapStatePayload::new);

        private CreateMapStatePayload(PacketByteBuf buf) {
        this(buf.readNbt());
    }

    public CreateMapStatePayload(NbtCompound imageNBT) {
        this.imageNBT = imageNBT;
    }

    private void write(RegistryByteBuf registryByteBuf) {
        registryByteBuf.writeNbt(imageNBT);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
