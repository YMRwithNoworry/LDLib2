package com.lowdragmc.lowdraglib2.networking;

import com.lowdragmc.lowdraglib2.LDLib2;
import com.lowdragmc.lowdraglib2.Platform;
import com.lowdragmc.lowdraglib2.compat.network.CustomPacketPayload;
import com.lowdragmc.lowdraglib2.compat.network.RegistryFriendlyByteBuf;
import com.lowdragmc.lowdraglib2.compat.network.codec.StreamCodec;
import com.lowdragmc.lowdraglib2.networking.both.PacketModularUISync;
import com.lowdragmc.lowdraglib2.networking.both.PacketRPCBlockEntity;
import com.lowdragmc.lowdraglib2.networking.both.PacketRPCPacket;
import com.lowdragmc.lowdraglib2.networking.both.PacketUIRPCEvent;
import com.lowdragmc.lowdraglib2.networking.both.PacketUIRPCEventReturn;
import com.lowdragmc.lowdraglib2.networking.s2c.SPacketAutoSyncBlockEntity;
import dev.architectury.networking.NetworkChannel;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.network.connection.ConnectionType;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * Author: KilaBash
 * Date: 2022/04/27
 * Description:
 */
public class LDLNetworking {
    private static final NetworkChannel CHANNEL = NetworkChannel.create(LDLib2.id("network"));
    private static final Set<CustomPacketPayload.Type<?>> REGISTERED_TYPES = new LinkedHashSet<>();
    private static boolean initialized;

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        registerPayloads(new RegisterPayloadHandlersEvent());
    }

    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(LDLib2.MOD_ID);

        registrar.playToClient(SPacketAutoSyncBlockEntity.TYPE, SPacketAutoSyncBlockEntity.CODEC, SPacketAutoSyncBlockEntity::execute);

        registrar.playBidirectional(PacketUIRPCEvent.TYPE, PacketUIRPCEvent.CODEC, PacketUIRPCEvent::execute);
        registrar.playBidirectional(PacketUIRPCEventReturn.TYPE, PacketUIRPCEventReturn.CODEC, PacketUIRPCEventReturn::execute);

        registrar.playBidirectional(PacketRPCBlockEntity.TYPE, PacketRPCBlockEntity.CODEC, PacketRPCBlockEntity::execute);
        registrar.playBidirectional(PacketModularUISync.TYPE, PacketModularUISync.CODEC, PacketModularUISync::execute);

        registrar.playBidirectional(PacketRPCPacket.TYPE, PacketRPCPacket.CODEC, PacketRPCPacket::execute);
    }

    public static <T extends CustomPacketPayload> void register(CustomPacketPayload.Type<T> type,
                                                               StreamCodec<RegistryFriendlyByteBuf, T> codec,
                                                               BiConsumer<T, IPayloadContext> handler) {
        if (!REGISTERED_TYPES.add(type)) {
            return;
        }
        CHANNEL.register((Class<T>) payloadClass(type),
                (payload, buffer) -> codec.encode(registryBuffer(buffer), payload),
                buffer -> codec.decode(registryBuffer(buffer)),
                (payload, contextSupplier) -> {
                    NetworkManager.PacketContext packetContext = contextSupplier.get();
                    packetContext.queue(() -> handler.accept(payload, new ArchitecturyPayloadContext(packetContext)));
                });
    }

    public static void sendToServer(CustomPacketPayload payload) {
        CHANNEL.sendToServer(payload);
    }

    public static void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        CHANNEL.sendToPlayer(player, payload);
    }

    public static void sendToAllPlayers(CustomPacketPayload payload) {
        var server = Platform.getMinecraftServer();
        if (!Platform.serverSafe(server)) {
            return;
        }
        CHANNEL.sendToPlayers(server.getPlayerList().getPlayers(), payload);
    }

    public static void sendToPlayersTrackingChunk(ServerLevel level, ChunkPos chunkPos, CustomPacketPayload payload) {
        if (level == null || !Platform.serverSafe(level.getServer())) {
            return;
        }
        var players = level.getChunkSource().chunkMap.getPlayers(chunkPos, false);
        CHANNEL.sendToPlayers(players, payload);
    }

    private static RegistryFriendlyByteBuf registryBuffer(FriendlyByteBuf buffer) {
        return new RegistryFriendlyByteBuf(buffer, Platform.getFrozenRegistry(), ConnectionType.OTHER);
    }

    private static RegistryFriendlyByteBuf registryBuffer(io.netty.buffer.ByteBuf buffer) {
        return new RegistryFriendlyByteBuf(buffer, Platform.getFrozenRegistry(), ConnectionType.OTHER);
    }

    private static Class<? extends CustomPacketPayload> payloadClass(CustomPacketPayload.Type<?> type) {
        if (type == SPacketAutoSyncBlockEntity.TYPE) return SPacketAutoSyncBlockEntity.class;
        if (type == PacketUIRPCEvent.TYPE) return PacketUIRPCEvent.class;
        if (type == PacketUIRPCEventReturn.TYPE) return PacketUIRPCEventReturn.class;
        if (type == PacketRPCBlockEntity.TYPE) return PacketRPCBlockEntity.class;
        if (type == PacketModularUISync.TYPE) return PacketModularUISync.class;
        if (type == PacketRPCPacket.TYPE) return PacketRPCPacket.class;
        throw new IllegalArgumentException("Unknown LDLib2 payload type: " + type.id());
    }

    private record ArchitecturyPayloadContext(NetworkManager.PacketContext context) implements IPayloadContext {
        @Override
        public Player player() {
            return context.getPlayer();
        }
    }
}
