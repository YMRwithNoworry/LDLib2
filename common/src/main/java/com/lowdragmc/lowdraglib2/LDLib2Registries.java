package com.lowdragmc.lowdraglib2;

import com.lowdragmc.lowdraglib2.client.renderer.IRenderer;
import com.lowdragmc.lowdraglib2.configurator.accessors.IConfiguratorAccessor;
import com.lowdragmc.lowdraglib2.editor.resource.BuiltinResourceProvider;
import com.lowdragmc.lowdraglib2.editor.resource.FileResourceProvider;
import com.lowdragmc.lowdraglib2.editor.resource.ResourceProviderType;
import com.lowdragmc.lowdraglib2.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.registry.AutoRegistry;
import com.lowdragmc.lowdraglib2.registry.LDLRegistry;
import com.lowdragmc.lowdraglib2.registry.annotation.LDLRegisterClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class LDLib2Registries {
    public final static AutoRegistry.LDLibRegister<UIElement, Supplier<UIElement>> UI_ELEMENTS = AutoRegistry.LDLibRegister
            .create(LDLib2.id("ui_element"), UIElement.class, AutoRegistry::noArgsCreator);

    public final static LDLRegistry.String<ResourceProviderType> RESOURCE_PROVIDER_TYPES = new LDLRegistry.String<>(LDLib2.id("resource_provider_types"));

    @OnlyIn(Dist.CLIENT)
    public static AutoRegistry.LDLibRegisterClient<IConfiguratorAccessor, IConfiguratorAccessor<?>> CONFIGURATOR_ACCESSORS;

    @OnlyIn(Dist.CLIENT)
    public static AutoRegistry.LDLibRegisterClient<IGuiTexture, Supplier<IGuiTexture>> GUI_TEXTURES;

    @OnlyIn(Dist.CLIENT)
    public static AutoRegistry.LDLibRegisterClient<IRenderer, Supplier<IRenderer>> RENDERERS;

    static {
        if (LDLib2.isClient()) {
            CONFIGURATOR_ACCESSORS = AutoRegistry.LDLibRegisterClient
                    .create(LDLib2.id("configurator_accessor"), IConfiguratorAccessor.class, AutoRegistry::noArgsInstance);
            GUI_TEXTURES = AutoRegistry.LDLibRegisterClient
                    .create(LDLib2.id("gui_texture"), IGuiTexture.class, AutoRegistry::noArgsCreator);
            GUI_TEXTURES.setMissingKey("missing");
            RENDERERS = AutoRegistry.LDLibRegisterClient
                    .create(LDLib2.id("renderer"), IRenderer.class, AutoRegistry::noArgsCreator);
        }
    }

    public static void init() {
        if (LDLib2.isClient()) {
            GUI_TEXTURES.register("empty", AutoRegistry.Holder.of(
                    IGuiTexture.EmptyTexture.class.getAnnotation(LDLRegisterClient.class),
                    IGuiTexture.EmptyTexture.class,
                    () -> IGuiTexture.EMPTY));
            GUI_TEXTURES.register("missing", AutoRegistry.Holder.of(
                    IGuiTexture.MissingTexture.class.getAnnotation(LDLRegisterClient.class),
                    IGuiTexture.MissingTexture.class,
                    () -> IGuiTexture.MISSING_TEXTURE));
            RENDERERS.register("empty", AutoRegistry.Holder.of(
                    IRenderer.EmptyRenderer.class.getAnnotation(LDLRegisterClient.class),
                    IRenderer.EmptyRenderer.class,
                    () -> IRenderer.EMPTY));
            LDLib2.LOGGER.info("LDLib2 editor registries loaded: ui_elements={}, gui_textures={}, renderers={}, configurator_accessors={}",
                    UI_ELEMENTS.values().size(), GUI_TEXTURES.values().size(), RENDERERS.values().size(), CONFIGURATOR_ACCESSORS.values().size());
        }

        RESOURCE_PROVIDER_TYPES.register(BuiltinResourceProvider.TYPE.getTypeName(), BuiltinResourceProvider.TYPE);
        RESOURCE_PROVIDER_TYPES.register(FileResourceProvider.TYPE.getTypeName(), FileResourceProvider.TYPE);
    }
}
