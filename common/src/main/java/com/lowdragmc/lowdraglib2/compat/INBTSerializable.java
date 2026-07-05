package com.lowdragmc.lowdraglib2.compat;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.Tag;

/**
 * Common serialization contract matching the 1.21 NeoForge provider-aware shape.
 * Platform-specific Forge/Fabric bridges can adapt this where a loader API expects
 * its own NBT interface.
 */
public interface INBTSerializable<T extends Tag> {
    T serializeNBT(HolderLookup.Provider provider);

    void deserializeNBT(HolderLookup.Provider provider, T nbt);
}
