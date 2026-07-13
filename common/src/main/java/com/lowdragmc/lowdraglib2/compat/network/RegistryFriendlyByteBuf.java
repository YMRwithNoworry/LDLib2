package com.lowdragmc.lowdraglib2.compat.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Compatibility bridge for Minecraft 1.20.1 where RegistryFriendlyByteBuf does not exist yet.
 */
public class RegistryFriendlyByteBuf extends FriendlyByteBuf {
    private final HolderLookup.Provider registryAccess;

    public RegistryFriendlyByteBuf(ByteBuf source, HolderLookup.Provider registryAccess) {
        super(source);
        this.registryAccess = registryAccess;
    }

    public RegistryFriendlyByteBuf(ByteBuf source, RegistryAccess registryAccess) {
        this(source, (HolderLookup.Provider) registryAccess);
    }

    public HolderLookup.Provider registryAccess() {
        return registryAccess;
    }
}
