package net.neoforged.neoforge.network.registration;

import com.lowdragmc.lowdraglib2.compat.network.RegistryFriendlyByteBuf;
import com.lowdragmc.lowdraglib2.compat.network.codec.StreamCodec;
import com.lowdragmc.lowdraglib2.compat.network.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.function.BiConsumer;

public class PayloadRegistrar {
    private final String namespace;

    public PayloadRegistrar(String namespace) {
        this.namespace = namespace;
    }

    public <T extends CustomPacketPayload> void playToClient(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> codec, BiConsumer<T, IPayloadContext> handler) {
    }

    public <T extends CustomPacketPayload> void playBidirectional(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> codec, BiConsumer<T, IPayloadContext> handler) {
    }

    public String namespace() {
        return namespace;
    }
}
