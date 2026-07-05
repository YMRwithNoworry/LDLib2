package com.lowdragmc.lowdraglib2.compat;

public interface IFluidHandlerModifiable extends IFluidHandler {
    void setFluidInTank(int tank, FluidStack stack);

    default boolean supportsFill(int tank) {
        return true;
    }

    default boolean supportsDrain(int tank) {
        return true;
    }
}
