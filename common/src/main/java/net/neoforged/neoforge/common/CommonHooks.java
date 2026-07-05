package net.neoforged.neoforge.common;

import com.mojang.serialization.DynamicOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.RegistryOps;

public final class CommonHooks {
    private CommonHooks() {
    }

    public static <T> HolderLookup.Provider extractLookupProvider(RegistryOps<T> registryOps) {
        DynamicOps<T> ops = registryOps;
        if (ops instanceof HolderLookup.Provider provider) {
            return provider;
        }
        throw new IllegalStateException("Registry lookup provider is not available from RegistryOps on Minecraft 1.20.1");
    }
}
