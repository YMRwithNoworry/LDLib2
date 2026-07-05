package net.neoforged.neoforge.common.util;

import net.minecraft.core.RegistryAccess;
import com.lowdragmc.lowdraglib2.compat.network.RegistryFriendlyByteBuf;

import java.util.function.Consumer;

public final class FriendlyByteBufUtil {
    private FriendlyByteBufUtil() {
    }

    public static byte[] writeCustomData(Consumer<RegistryFriendlyByteBuf> dataWriter, RegistryAccess registryAccess) {
        return com.lowdragmc.lowdraglib2.utils.ByteBufUtil.writeCustomData(dataWriter, registryAccess);
    }
}
