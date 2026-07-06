package com.lowdragmc.lowdraglib2.client;

import com.lowdragmc.lowdraglib2.CommonProxy;
import com.lowdragmc.lowdraglib2.LDLib2;
import com.lowdragmc.lowdraglib2.Platform;
import com.lowdragmc.lowdraglib2.client.model.forge.LDLRendererModel;
import com.lowdragmc.lowdraglib2.client.renderer.ATESRRendererProvider;
import com.lowdragmc.lowdraglib2.client.renderer.IRenderer;
import com.lowdragmc.lowdraglib2.client.shader.LDLibShaders;
import com.lowdragmc.lowdraglib2.editor.resource.IRendererResource;
import com.lowdragmc.lowdraglib2.editor.resource.PackResourceManager;
import com.lowdragmc.lowdraglib2.gui.factory.LDMenuTypes;
import com.lowdragmc.lowdraglib2.gui.holder.ModularUIContainerScreen;
import com.lowdragmc.lowdraglib2.gui.ui.style.StylesheetManager;
import com.lowdragmc.lowdraglib2.gui.ui.utils.ModularUIClientElementComponent;
import com.lowdragmc.lowdraglib2.gui.ui.utils.ModularUITooltipComponent;
import dev.architectury.registry.ReloadListenerRegistry;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.architectury.registry.menu.MenuRegistry;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.server.packs.PackType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class ClientProxy {
    private static boolean commonClientRegistered;
    private static Field particleProvidersField;

    public ClientProxy() {
        registerCommonClientHooks();
    }

    public ClientProxy(Object eventBus) {
        addListener(eventBus, RegisterMenuScreensEvent.class, (Consumer<RegisterMenuScreensEvent>) this::onRegisterMenuScreensEvent);
        addListener(eventBus, RegisterClientTooltipComponentFactoriesEvent.class, (Consumer<RegisterClientTooltipComponentFactoriesEvent>) this::onRegisterClientTooltipComponentFactoriesEvent);
        addListener(eventBus, EntityRenderersEvent.RegisterRenderers.class, (Consumer<EntityRenderersEvent.RegisterRenderers>) this::registerRenderers);
        addListener(eventBus, FMLClientSetupEvent.class, (Consumer<FMLClientSetupEvent>) this::clientSetup);
        addListener(eventBus, ModelEvent.RegisterGeometryLoaders.class, (Consumer<ModelEvent.RegisterGeometryLoaders>) this::modelRegistry);
        addListener(eventBus, RegisterShadersEvent.class, (Consumer<RegisterShadersEvent>) this::shaderRegistry);
        addListener(eventBus, RegisterClientReloadListenersEvent.class, (Consumer<RegisterClientReloadListenersEvent>) this::onRegisterClientReloadListenersEvent);
        addListener(eventBus, ModelEvent.RegisterAdditional.class, (Consumer<ModelEvent.RegisterAdditional>) this::registerModels);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void addListener(Object eventBus, Class<?> eventType, Consumer<?> listener) {
        try {
            for (var method : eventBus.getClass().getMethods()) {
                var parameters = method.getParameterTypes();
                if (!method.getName().equals("addListener") || parameters.length != 4 ||
                        !parameters[0].isEnum() || parameters[1] != boolean.class ||
                        parameters[2] != Class.class || parameters[3] != Consumer.class) {
                    continue;
                }
                var normalPriority = Enum.valueOf((Class<Enum>) parameters[0], "NORMAL");
                method.invoke(eventBus, normalPriority, false, eventType, listener);
                return;
            }
            var method = eventBus.getClass().getMethod("addListener", Class.class, Consumer.class);
            method.invoke(eventBus, eventType, listener);
        } catch (ReflectiveOperationException ignored) {
            if (eventBus instanceof net.neoforged.bus.api.IEventBus bus) {
                bus.addListener((Consumer) listener);
            }
        }
    }

    public static void registerCommonClientHooks() {
        if (commonClientRegistered) {
            return;
        }
        commonClientRegistered = true;
        if (!Platform.isForge()) {
            LDLib2.LOGGER.info("Registering LDLib2 common menu screens");
            MenuRegistry.registerScreenFactory(LDMenuTypes.PLAYER_UI.get(), ModularUIContainerScreen::new);
            MenuRegistry.registerScreenFactory(LDMenuTypes.HELD_ITEM_UI.get(), ModularUIContainerScreen::new);
            MenuRegistry.registerScreenFactory(LDMenuTypes.BLOCK_UI.get(), ModularUIContainerScreen::new);
        }
        BlockEntityRendererRegistry.register(CommonProxy.RENDERER_BE_TYPE.get(), ATESRRendererProvider::new);
        ReloadListenerRegistry.register(PackType.CLIENT_RESOURCES, PackResourceManager.INSTANCE, LDLib2.id("pack_resources"));
        ReloadListenerRegistry.register(PackType.CLIENT_RESOURCES, StylesheetManager.INSTANCE, LDLib2.id("stylesheets"));
    }

    @SubscribeEvent
    public void onRegisterMenuScreensEvent(final RegisterMenuScreensEvent event) {
        event.register(LDMenuTypes.PLAYER_UI.get(), ModularUIContainerScreen::new);
        event.register(LDMenuTypes.HELD_ITEM_UI.get(), ModularUIContainerScreen::new);
        event.register(LDMenuTypes.BLOCK_UI.get(), ModularUIContainerScreen::new);
    }

    @SubscribeEvent
    public void onRegisterClientTooltipComponentFactoriesEvent(final RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(ModularUITooltipComponent.class, ModularUIClientElementComponent::new);
    }

    @SubscribeEvent
    public void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(CommonProxy.RENDERER_BE_TYPE.get(), ATESRRendererProvider::new);
    }

    @SubscribeEvent
    public void clientSetup(final FMLClientSetupEvent e) {
        e.enqueueWork(() -> {
            LDLibShaders.init();
        });
    }

    @SubscribeEvent
    public void modelRegistry(final ModelEvent.RegisterGeometryLoaders e) {
        e.register(LDLib2.id("renderer"), LDLRendererModel.Loader.INSTANCE);
    }

    @SubscribeEvent
    public void shaderRegistry(RegisterShadersEvent event) {
        LDLibShaders.registerShaders(event);
    }

    @SubscribeEvent
    public void onRegisterClientReloadListenersEvent(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(PackResourceManager.INSTANCE);
        event.registerReloadListener(StylesheetManager.INSTANCE);
    }

    @SubscribeEvent
    public void registerModels(ModelEvent.RegisterAdditional event) {
        // load all models under the ldlib folder
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

    public static ParticleProvider getProvider(ParticleType<?> type) {
        var particleEngine = Minecraft.getInstance().particleEngine;
        try {
            var cachedProvider = getParticleProvider(particleEngine, particleProvidersField, type);
            if (cachedProvider != null) {
                return cachedProvider;
            }
            for (var field : particleEngine.getClass().getDeclaredFields()) {
                var provider = getParticleProvider(particleEngine, field, type);
                if (provider != null) {
                    particleProvidersField = field;
                    return provider;
                }
            }
        } catch (ReflectiveOperationException | RuntimeException ignored) {
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static ParticleProvider getParticleProvider(ParticleEngine particleEngine, Field field, ParticleType<?> type) throws IllegalAccessException {
        if (field == null) {
            return null;
        }
        if (Int2ObjectMap.class.isAssignableFrom(field.getType())) {
            field.setAccessible(true);
            var provider = ((Int2ObjectMap<?>) field.get(particleEngine))
                    .get(BuiltInRegistries.PARTICLE_TYPE.getId(type));
            return provider instanceof ParticleProvider<?> particleProvider ? particleProvider : null;
        }
        if (Map.class.isAssignableFrom(field.getType())) {
            field.setAccessible(true);
            var provider = ((Map<?, ?>) field.get(particleEngine))
                    .get(BuiltInRegistries.PARTICLE_TYPE.getKey(type));
            if (provider instanceof ParticleProvider<?> particleProvider) {
                return particleProvider;
            }
        }
        return null;
    }

}
