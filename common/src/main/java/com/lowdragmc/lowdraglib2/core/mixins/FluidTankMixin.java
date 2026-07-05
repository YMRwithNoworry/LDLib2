package com.lowdragmc.lowdraglib2.core.mixins;

import com.lowdragmc.lowdraglib2.misc.IFluidHandlerModifiable;
import com.lowdragmc.lowdraglib2.compat.FluidStack;
import com.lowdragmc.lowdraglib2.compat.IFluidTank;
import com.lowdragmc.lowdraglib2.compat.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = FluidTank.class, remap = false)
public abstract class FluidTankMixin implements IFluidHandlerModifiable, IFluidTank {
    @Shadow
    protected FluidStack fluid;

    @Shadow
    protected abstract void onContentsChanged();
    @Shadow
    public abstract void setFluid(FluidStack fluid);

    @Shadow
    public abstract long fill(FluidStack resource, FluidAction action);

    @Shadow
    public abstract @NotNull FluidStack drain(int maxDrain, FluidAction action);

    @Override
    public void setFluidInTank(int tank, FluidStack fluid) {
        setFluid(fluid);
        this.onContentsChanged();
    }
}
