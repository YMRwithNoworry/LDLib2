package com.lowdragmc.lowdraglib2.misc;

import lombok.Getter;
import lombok.Setter;
import com.lowdragmc.lowdraglib2.compat.FluidStack;
import com.lowdragmc.lowdraglib2.compat.IFluidTank;
import com.lowdragmc.lowdraglib2.compat.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CycleFluidStorage implements IFluidTank, IFluidHandlerModifiable {
    private List<FluidStack> storages;
    @Getter @Setter
    private long capacity;

    public CycleFluidStorage(int capacity, List<FluidStack> storages) {
        setCapacity(capacity);
        updateStacks(storages);
    }

    public void updateStacks(List<FluidStack> storages) {
        this.storages = storages;
    }


    @NotNull
    @Override
    public FluidStack getFluid() {
        return storages == null || storages.isEmpty() ? FluidStack.EMPTY : storages.get(Math.abs((int)(System.currentTimeMillis() / 1000) % storages.size()));
    }

    public long getFluidAmount() {
        return getFluid().getAmount();
    }

    public void setFluid(FluidStack fluid) {
        updateStacks(List.of(fluid));
    }

    @Override
    public void setFluidInTank(int tank, FluidStack stack) {
        setFluid(stack);
    }

    @Override
    public boolean isFluidValid(FluidStack stack) {
        return true;
    }

    @Override
    public long fill(FluidStack resource, IFluidHandler.FluidAction action) {
        return 0;
    }

    @Override
    public FluidStack drain(long maxDrain, IFluidHandler.FluidAction action) {
        return FluidStack.EMPTY;
    }

    @Override
    public boolean supportsFill(int tank) {
        return false;
    }

    @NotNull
    @Override
    public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
        return FluidStack.EMPTY;
    }

    @Override
    public boolean supportsDrain(int tank) {
        return false;
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        return getFluid();
    }

    @Override
    public long getTankCapacity(int tank) {
        return getCapacity();
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return false;
    }
}
