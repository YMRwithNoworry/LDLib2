package com.lowdragmc.lowdraglib2.forge;

import com.lowdragmc.lowdraglib2.CommonListeners;
import com.lowdragmc.lowdraglib2.CommonProxy;
import com.lowdragmc.lowdraglib2.LDLib2;
import com.lowdragmc.lowdraglib2.Platform;
import com.lowdragmc.lowdraglib2.client.ClientProxy;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
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
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> () -> new ClientProxy(eventBus));
    }
}
