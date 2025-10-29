package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundSetHeldSlotPacket implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundSetHeldSlotPacket> STREAM_CODEC = Packet.codec(
        ClientboundSetHeldSlotPacket::write, ClientboundSetHeldSlotPacket::new
    );
    private final int slot;

    public ClientboundSetHeldSlotPacket(int p_368536_) {
        this.slot = p_368536_;
    }

    private ClientboundSetHeldSlotPacket(FriendlyByteBuf p_364981_) {
        this.slot = p_364981_.readByte();
    }

    private void write(FriendlyByteBuf p_363172_) {
        p_363172_.writeByte(this.slot);
    }

    @Override
    public PacketType<ClientboundSetHeldSlotPacket> type() {
        return GamePacketTypes.CLIENTBOUND_SET_HELD_SLOT;
    }

    public void handle(ClientGamePacketListener p_367345_) {
        p_367345_.handleSetHeldSlot(this);
    }

    public int getSlot() {
        return this.slot;
    }
}