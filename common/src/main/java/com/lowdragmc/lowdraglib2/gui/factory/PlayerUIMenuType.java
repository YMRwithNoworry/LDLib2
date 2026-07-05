package com.lowdragmc.lowdraglib2.gui.factory;

import com.lowdragmc.lowdraglib2.LDLib2;
import com.lowdragmc.lowdraglib2.gui.holder.ModularUIContainerMenu;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.FriendlyByteBuf;
import com.lowdragmc.lowdraglib2.compat.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class PlayerUIMenuType {
    private final static Map<ResourceLocation, Function<Player, PlayerUIHolder>> UI_HOLDERS = new ConcurrentHashMap<>();

    public static void register(ResourceLocation id, Function<Player, PlayerUIHolder> holder) {
        UI_HOLDERS.put(id, holder);
        LDLib2.LOGGER.debug("Registered LDLib2 player UI holder {}", id);
    }

    public static void unregister(ResourceLocation id) {
        UI_HOLDERS.remove(id);
    }

    /**
     * Opens a UI for the specified player if the given identifier is registered.
     * This method checks if a corresponding UI holder exists for the provided id,
     * creates the holder instance using the associated provider, and opens the menu for the player.
     *
     * @param player the {@link Player} for whom the UI should be opened
     * @param id the {@link ResourceLocation} identifier of the UI to be opened
     * @return {@code true} if the UI was successfully opened, {@code false} if the id is not registered
     *         or the holder instance could not be created
     */
    public static boolean openUI(Player player, ResourceLocation id) {
        var holderFactory = UI_HOLDERS.get(id);
        if (holderFactory == null) {
            LDLib2.LOGGER.warn("No LDLib2 player UI holder registered for {}", id);
            return false;
        }
        var holder = holderFactory.apply(player);
        if (holder == null) return false;
        var provider = new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.translatable(id.toLanguageKey());
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
                return new ModularUIContainerMenu(LDMenuTypes.PLAYER_UI.get(), containerId, playerInventory, holder);
            }


            public void writeClientSideData(AbstractContainerMenu menu, RegistryFriendlyByteBuf buffer) {
                buffer.writeResourceLocation(id);
            }
        };
        return player instanceof net.minecraft.server.level.ServerPlayer serverPlayer &&
                ExtendedMenuOpener.open(serverPlayer, provider, provider::writeClientSideData);
    }

    public static ModularUIContainerMenu create(int windowId, Inventory inv, FriendlyByteBuf data) {
        var id = data.readResourceLocation();
        LDLib2.LOGGER.info("Creating LDLib2 player UI menu {} for {}", id, inv.player.getGameProfile().getName());
        var holderFactory = UI_HOLDERS.get(id);
        if (holderFactory == null) {
            throw new IllegalArgumentException("No player ui holder registered for id " + id);
        }
        var holder = holderFactory.apply(inv.player);
        if (holder == null) throw new IllegalArgumentException("No player ui holder found for id " + id);
        return new ModularUIContainerMenu(LDMenuTypes.PLAYER_UI.get(), windowId, inv, holder);
    }


    @FunctionalInterface
    public interface PlayerUIHolder extends IContainerUIHolder {
        @Override
        default boolean isStillValid(Player player) {
            return true;
        }
    }
}
