package com.lowdragmc.lowdraglib2.forge;

import com.lowdragmc.lowdraglib2.CommonListeners;
import com.lowdragmc.lowdraglib2.CommonProxy;
import com.lowdragmc.lowdraglib2.LDLib2;
import com.lowdragmc.lowdraglib2.Platform;
import com.lowdragmc.lowdraglib2.client.ClientCommands;
import com.lowdragmc.lowdraglib2.client.ClientProxy;
import com.lowdragmc.lowdraglib2.client.renderer.ATESRRendererProvider;
import com.lowdragmc.lowdraglib2.client.renderer.IRenderer;
import com.lowdragmc.lowdraglib2.client.shader.LDLibShaders;
import com.lowdragmc.lowdraglib2.editor.resource.IRendererResource;
import com.lowdragmc.lowdraglib2.editor.resource.PackResourceManager;
import com.lowdragmc.lowdraglib2.forge.client.ForgeLDLRendererModel;
import com.lowdragmc.lowdraglib2.gui.factory.LDMenuTypes;
import com.lowdragmc.lowdraglib2.gui.holder.ModularUIContainerScreen;
import com.lowdragmc.lowdraglib2.gui.ui.style.StylesheetManager;
import com.lowdragmc.lowdraglib2.gui.ui.utils.ModularUIClientElementComponent;
import com.lowdragmc.lowdraglib2.gui.ui.utils.ModularUITooltipComponent;
import dev.architectury.platform.forge.EventBuses;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;
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
        eventBus.addListener(LDLib2Forge::registerRenderers);
        eventBus.addListener(LDLib2Forge::registerShaders);
        eventBus.addListener(LDLib2Forge::registerTooltipComponents);
        eventBus.addListener(LDLib2Forge::registerClientReloadListeners);
        eventBus.addListener(LDLib2Forge::registerGeometryLoaders);
        eventBus.addListener(LDLib2Forge::registerAdditionalModels);
        MinecraftForge.EVENT_BUS.addListener(LDLib2Forge::registerClientCommands);
    }

    private static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ClientProxy.registerCommonClientHooks();
            registerMenuScreens();
        });
    }

    private static void registerGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register("renderer", ForgeLDLRendererModel.Loader.INSTANCE);
    }

    private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(CommonProxy.RENDERER_BE_TYPE.get(), ATESRRendererProvider::new);
    }

    private static void registerShaders(RegisterShadersEvent event) {
        LDLib2.LOGGER.info("Registering LDLib2 Forge shaders");
        LDLibShaders.registerShaders(event.getResourceProvider(), event::registerShader);
    }

    private static void registerTooltipComponents(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(ModularUITooltipComponent.class, ModularUIClientElementComponent::new);
    }

    private static void registerClientReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(PackResourceManager.INSTANCE);
        event.registerReloadListener(StylesheetManager.INSTANCE);
    }

    private static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
        for (var entry : Minecraft.getInstance().getResourceManager().listResources("models",
                id -> id.getNamespace().equals(LDLib2.MOD_ID) && id.getPath().endsWith(".json")).entrySet()) {
            if (entry.getValue().sourcePackId().equals(LDLib2.MOD_ID)) {
                var modelLocation = new ResourceLocation(
                        entry.getKey().getNamespace(),
                        entry.getKey().getPath()
                                .replace("models/", "")
                                .replace(".json", ""));
                event.register(new ModelResourceLocation(modelLocation, "standalone"));
            }
        }
        IRendererResource.INSTANCE.onAdditionalModel(event::register);
        for (IRenderer renderer : IRenderer.EVENT_REGISTERS) {
            renderer.onAdditionalModel(event::register);
        }
    }

    private static void registerMenuScreens() {
        LDLib2.LOGGER.info("Registering LDLib2 Forge menu screens");
        MenuScreens.register(LDMenuTypes.PLAYER_UI.get(), ModularUIContainerScreen::new);
        MenuScreens.register(LDMenuTypes.HELD_ITEM_UI.get(), ModularUIContainerScreen::new);
        MenuScreens.register(LDMenuTypes.BLOCK_UI.get(), ModularUIContainerScreen::new);
    }

    private static void registerClientCommands(RegisterClientCommandsEvent event) {
        LDLib2.LOGGER.info("Registering LDLib2 Forge client commands");
        ClientCommands.createClientCommands().forEach(event.getDispatcher()::register);
    }
}
