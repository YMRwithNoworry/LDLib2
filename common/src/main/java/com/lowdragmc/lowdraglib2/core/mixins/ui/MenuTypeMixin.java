package com.lowdragmc.lowdraglib2.core.mixins.ui;

import com.lowdragmc.lowdraglib2.gui.event.ContainerMenuEvent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MenuType.class)
public abstract class MenuTypeMixin<T extends AbstractContainerMenu> {

    @Inject(method = "create", remap = false,
            at = @At(value = "RETURN"))
    private void ldlib2$create1(int containerId, Inventory playerInventory, CallbackInfoReturnable<T> cir) {
        var menu = cir.getReturnValue();
        if (menu != null) {
            NeoForge.EVENT_BUS.post(new ContainerMenuEvent.Create(playerInventory.player, menu));
        }
    }
}
