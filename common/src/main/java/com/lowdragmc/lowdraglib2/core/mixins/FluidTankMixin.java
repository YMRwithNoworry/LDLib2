package com.lowdragmc.lowdraglib2.core.mixins;

import com.lowdragmc.lowdraglib2.misc.IFluidHandlerModifiable;
import com.lowdragmc.lowdraglib2.compat.FluidTank;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Keeps the legacy {@code misc.IFluidHandlerModifiable} view available to consumers of the 1.21 API.
 * The 1.20.1 compatibility FluidTank already implements the current handler directly, so this mixin
 * must not shadow the removed NeoForge implementation methods.
 */
@Mixin(value = FluidTank.class, remap = false)
public abstract class FluidTankMixin implements IFluidHandlerModifiable {
    @Override
    public void setFluidInTank(int tank, com.lowdragmc.lowdraglib2.compat.FluidStack fluid) {
        if (tank == 0) {
            ((FluidTank) (Object) this).setFluid(fluid);
        }
    }
}
