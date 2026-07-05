package com.lowdragmc.lowdraglib2.fabric;

import com.lowdragmc.lowdraglib2.client.ClientProxy;
import net.fabricmc.api.ClientModInitializer;

public final class LDLib2FabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        new ClientProxy();
    }
}
