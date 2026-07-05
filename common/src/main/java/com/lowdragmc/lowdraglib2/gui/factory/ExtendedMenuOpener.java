package com.lowdragmc.lowdraglib2.gui.factory;

import com.lowdragmc.lowdraglib2.LDLib2;
import com.lowdragmc.lowdraglib2.Platform;
import com.lowdragmc.lowdraglib2.compat.network.RegistryFriendlyByteBuf;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.connection.ConnectionType;

import java.util.function.BiConsumer;

public final class ExtendedMenuOpener {
    private ExtendedMenuOpener() {
    }

    public static boolean open(ServerPlayer player, MenuProvider provider, BiConsumer<AbstractContainerMenu, RegistryFriendlyByteBuf> writer) {
        LDLib2.LOGGER.info("Opening extended LDLib2 menu {} for {}", provider.getDisplayName().getString(), player.getGameProfile().getName());
        MenuRegistry.openExtendedMenu(player, provider,
                buffer -> writer.accept(null, new RegistryFriendlyByteBuf(buffer, Platform.getFrozenRegistry(), ConnectionType.OTHER)));
        return true;
    }
}
