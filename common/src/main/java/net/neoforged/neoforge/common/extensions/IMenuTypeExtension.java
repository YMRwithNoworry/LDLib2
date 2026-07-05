package net.neoforged.neoforge.common.extensions;

import com.lowdragmc.lowdraglib2.Platform;
import com.lowdragmc.lowdraglib2.compat.network.RegistryFriendlyByteBuf;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.network.connection.ConnectionType;

public final class IMenuTypeExtension {
    private IMenuTypeExtension() {
    }

    public static <T extends AbstractContainerMenu> MenuType<T> create(MenuFactory<T> factory) {
        return MenuRegistry.ofExtended((id, inventory, data) ->
                factory.create(id, inventory, new RegistryFriendlyByteBuf(data, Platform.getFrozenRegistry(), ConnectionType.OTHER)));
    }

    @FunctionalInterface
    public interface MenuFactory<T extends AbstractContainerMenu> {
        T create(int windowId, Inventory inventory, FriendlyByteBuf data);
    }
}
