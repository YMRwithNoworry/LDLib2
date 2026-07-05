package com.lowdragmc.lowdraglib2.fabric;

import com.lowdragmc.lowdraglib2.LDLib2;
import com.lowdragmc.lowdraglib2.client.ClientProxy;
import com.lowdragmc.lowdraglib2.client.ClientEditorCommands;
import com.lowdragmc.lowdraglib2.client.shader.LDLibShaders;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.api.ClientModInitializer;

public final class LDLib2FabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        new ClientProxy();
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> LDLibShaders.init());
        CoreShaderRegistrationCallback.EVENT.register(context ->
                LDLibShaders.registerShaderDefinitions(context::register));
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            LDLib2.LOGGER.info("Registering LDLib2 Fabric client commands");
            dispatcher.register(ClientCommandManager.literal("ldlib2_ui_editor")
                    .executes(context -> ClientEditorCommands.openUIEditor()));
        });
    }
}
