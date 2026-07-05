package net.neoforged.neoforge.client.event;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

import java.util.function.Function;

public class RegisterClientTooltipComponentFactoriesEvent {
    public <T extends TooltipComponent> void register(Class<T> type, Function<T, ? extends ClientTooltipComponent> factory) {
    }
}
