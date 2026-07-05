package com.lowdragmc.lowdraglib2.compat;

import org.jetbrains.annotations.NotNull;

public class FluidTank implements IFluidTank, IFluidHandlerModifiable {
    protected long capacity;
    protected FluidStack fluid = FluidStack.EMPTY;

    public FluidTank(long capacity) {
        this.capacity = capacity;
    }

    public FluidTank setFluid(FluidStack fluid) {
        this.fluid = fluid == null ? FluidStack.EMPTY : fluid;
        return this;
    }

    @Override
    public @NotNull FluidStack getFluid() {
        return fluid;
    }

    @Override
    public long getCapacity() {
        return capacity;
    }

    @Override
    public boolean isFluidValid(FluidStack stack) {
        return true;
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        return tank == 0 ? fluid : FluidStack.EMPTY;
    }

    @Override
    public long getTankCapacity(int tank) {
        return tank == 0 ? capacity : 0;
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return tank == 0 && isFluidValid(stack);
    }

    @Override
    public long fill(FluidStack resource, IFluidHandler.FluidAction action) {
        if (resource == null || resource.isEmpty() || !isFluidValid(resource)) return 0;
        long filled = Math.min(capacity - fluid.getAmount(), resource.getAmount());
        if (action.execute() && filled > 0) {
            if (fluid.isEmpty()) {
                fluid = resource.copyWithAmount(filled);
            } else {
                fluid.grow(filled);
            }
        }
        return filled;
    }

    @Override
    public @NotNull FluidStack drain(long maxDrain, IFluidHandler.FluidAction action) {
        if (fluid.isEmpty() || maxDrain <= 0) return FluidStack.EMPTY;
        long drained = Math.min(maxDrain, fluid.getAmount());
        var result = fluid.copyWithAmount(drained);
        if (action.execute()) {
            fluid.shrink(drained);
            if (fluid.getAmount() <= 0) fluid = FluidStack.EMPTY;
        }
        return result;
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
        if (resource == null || resource.isEmpty() || fluid.isEmpty() || resource.getFluid() != fluid.getFluid()) return FluidStack.EMPTY;
        return drain(resource.getAmount(), action);
    }

    @Override
    public void setFluidInTank(int tank, FluidStack stack) {
        if (tank == 0) setFluid(stack);
    }
}
