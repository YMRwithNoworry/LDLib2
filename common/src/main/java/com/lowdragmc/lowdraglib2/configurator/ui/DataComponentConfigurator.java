package com.lowdragmc.lowdraglib2.configurator.ui;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Minecraft 1.20.1 does not have the 1.21 DataComponent API.
 * This placeholder keeps older item/fluid configurators usable while component editing is disabled.
 */
public class DataComponentConfigurator extends ConfiguratorGroup {
    public DataComponentConfigurator(Object prototype, Supplier<?> supplier, Consumer<?> consumer, boolean forceUpdate) {
        super("configurator.data_component");
    }

    public DataComponentConfigurator setPrototype(Object prototype) {
        return this;
    }
}
