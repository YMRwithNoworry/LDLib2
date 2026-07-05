package net.neoforged.neoforge.client.event;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Supplier;

public class RegisterMenuScreensEvent {
    public <M extends AbstractContainerMenu> void register(MenuType<? extends M> menuType, ScreenFactory<M> factory) {
    }

    @FunctionalInterface
    public interface ScreenFactory<M extends AbstractContainerMenu> {
        Object create(M menu, net.minecraft.world.entity.player.Inventory inventory, net.minecraft.network.chat.Component title);
    }
}
