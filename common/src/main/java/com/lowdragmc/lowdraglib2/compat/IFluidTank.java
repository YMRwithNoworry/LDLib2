package com.lowdragmc.lowdraglib2.compat;

import org.jetbrains.annotations.NotNull;

public interface IFluidTank {
    @NotNull
    FluidStack getFluid();

    long getCapacity();

    boolean isFluidValid(FluidStack stack);

    long fill(FluidStack resource, IFluidHandler.FluidAction action);

    @NotNull
    FluidStack drain(long maxDrain, IFluidHandler.FluidAction action);

    @NotNull
    FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action);
}
