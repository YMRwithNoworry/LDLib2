package com.lowdragmc.lowdraglib2.compat;

import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

public interface IFluidHandler {
    enum FluidAction {
        EXECUTE,
        SIMULATE;

        public boolean execute() {
            return this == EXECUTE;
        }

        public boolean simulate() {
            return this == SIMULATE;
        }
    }

    int getTanks();

    @NotNull
    FluidStack getFluidInTank(int tank);

    long getTankCapacity(int tank);

    boolean isFluidValid(int tank, @NotNull FluidStack stack);

    long fill(FluidStack resource, FluidAction action);

    @NotNull
    FluidStack drain(FluidStack resource, FluidAction action);

    @NotNull
    FluidStack drain(long maxDrain, FluidAction action);
}
