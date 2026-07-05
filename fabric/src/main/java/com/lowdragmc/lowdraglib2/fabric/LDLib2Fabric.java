package com.lowdragmc.lowdraglib2.fabric;

import com.lowdragmc.lowdraglib2.CommonProxy;
import com.lowdragmc.lowdraglib2.LDLib2;
import net.fabricmc.api.ModInitializer;

public final class LDLib2Fabric implements ModInitializer {
    @Override
    public void onInitialize() {
        LDLib2.init();
        new CommonProxy();
    }
}
