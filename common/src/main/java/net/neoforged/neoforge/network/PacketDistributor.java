package net.neoforged.neoforge.network;

import com.lowdragmc.lowdraglib2.compat.network.CustomPacketPayload;
import com.lowdragmc.lowdraglib2.networking.LDLNetworking;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

public final class PacketDistributor {
    private PacketDistributor() {
    }

    public static void sendToServer(CustomPacketPayload payload) {
        LDLNetworking.sendToServer(payload);
    }

    public static void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        LDLNetworking.sendToPlayer(player, payload);
    }

    public static void sendToAllPlayers(CustomPacketPayload payload) {
        LDLNetworking.sendToAllPlayers(payload);
    }

    public static void sendToPlayersTrackingChunk(ServerLevel level, ChunkPos chunkPos, CustomPacketPayload payload) {
        LDLNetworking.sendToPlayersTrackingChunk(level, chunkPos, payload);
    }
}
