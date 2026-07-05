package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.neoforged.neoforge.network.connection.ConnectionType;

/**
 * Compatibility bridge for Minecraft 1.20.1 where RegistryFriendlyByteBuf does not exist yet.
 */
public class RegistryFriendlyByteBuf extends FriendlyByteBuf {
    private final HolderLookup.Provider registryAccess;

    public RegistryFriendlyByteBuf(ByteBuf source, HolderLookup.Provider registryAccess, ConnectionType connectionType) {
        super(source);
        this.registryAccess = registryAccess;
    }

    public RegistryFriendlyByteBuf(ByteBuf source, RegistryAccess registryAccess, ConnectionType connectionType) {
        this(source, (HolderLookup.Provider) registryAccess, connectionType);
    }

    public HolderLookup.Provider registryAccess() {
        return registryAccess;
    }
}
