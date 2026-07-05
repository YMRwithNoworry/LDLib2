package com.lowdragmc.lowdraglib2.forge;

import com.lowdragmc.lowdraglib2.CommonListeners;
import com.lowdragmc.lowdraglib2.CommonProxy;
import com.lowdragmc.lowdraglib2.LDLib2;
import com.lowdragmc.lowdraglib2.Platform;
import com.lowdragmc.lowdraglib2.client.ClientCommands;
import com.lowdragmc.lowdraglib2.client.ClientProxy;
import com.lowdragmc.lowdraglib2.forge.client.ForgeLDLRendererModel;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(LDLib2.MOD_ID)
public final class LDLib2Forge {
    public LDLib2Forge() {
        var eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        EventBuses.registerModEventBus(LDLib2.MOD_ID, eventBus);
        LDLib2.init();
        new CommonProxy(eventBus);
        if (Platform.isDevEnv()) {
            CommonListeners.ModCreativeModeTab.register(null);
        }
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> LDLib2Forge::initClient);
    }

    private static void initClient() {
        var eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        new ClientProxy(eventBus);
        eventBus.addListener(LDLib2Forge::clientSetup);
        eventBus.addListener(LDLib2Forge::registerGeometryLoaders);
        MinecraftForge.EVENT_BUS.addListener(LDLib2Forge::registerClientCommands);
    }

    private static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(ClientProxy::registerCommonClientHooks);
    }

    private static void registerGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register("renderer", ForgeLDLRendererModel.Loader.INSTANCE);
    }

    private static void registerClientCommands(RegisterClientCommandsEvent event) {
        LDLib2.LOGGER.info("Registering LDLib2 Forge client commands");
        ClientCommands.createClientCommands().forEach(event.getDispatcher()::register);
    }
}
