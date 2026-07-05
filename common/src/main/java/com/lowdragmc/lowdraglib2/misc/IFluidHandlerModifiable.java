package com.lowdragmc.lowdraglib2.misc;

import com.lowdragmc.lowdraglib2.compat.FluidStack;
import com.lowdragmc.lowdraglib2.compat.IFluidHandler;

/**
 * Extensions to NeoForge's {@link IFluidHandler}
 */
public interface IFluidHandlerModifiable extends IFluidHandler {

    void setFluidInTank(int tank, FluidStack stack);

    default boolean supportsFill(int tank) {
        return true;
    }

    default boolean supportsDrain(int tank) {
        return true;
    }
}
