package com.lowdragmc.lowdraglib2;

import com.lowdragmc.lowdraglib2.client.renderer.block.RendererBlock;
import com.lowdragmc.lowdraglib2.client.renderer.block.RendererBlockEntity;
import com.lowdragmc.lowdraglib2.gui.factory.LDMenuTypes;
import com.lowdragmc.lowdraglib2.gui.ui.style.PropertyRegistry;
import com.lowdragmc.lowdraglib2.networking.LDLNetworking;
import com.lowdragmc.lowdraglib2.networking.rpc.RPCPacketDistributor;
import com.lowdragmc.lowdraglib2.nodegraphtookit.api.type.TypeHandles;
import com.lowdragmc.lowdraglib2.plugin.ILDLibPlugin;
import com.lowdragmc.lowdraglib2.plugin.LDLibPlugin;
import com.lowdragmc.lowdraglib2.syncdata.AccessorRegistries;
import com.lowdragmc.lowdraglib2.utils.ReflectionUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Consumer;

public class CommonProxy {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, LDLib2.MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, LDLib2.MOD_ID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, LDLib2.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RendererBlockEntity>> RENDERER_BE_TYPE;

    static {
        BLOCKS.register("renderer_block", () -> RendererBlock.BLOCK);
        ITEMS.register("renderer_block", () -> new BlockItem(RendererBlock.BLOCK, new Item.Properties()));
        RENDERER_BE_TYPE = BLOCK_ENTITY_TYPES.register("renderer_block", () -> BlockEntityType.Builder.of(RendererBlockEntity::new, RendererBlock.BLOCK).build(null));
    }

    public CommonProxy() {
        CommonProxy.init(null);
        loadPlugins();
    }

    public CommonProxy(Object eventBus) {
        // used for forge events (ClientProxy + CommonProxy)
        addListener(eventBus, (Consumer<net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent>) LDLNetworking::registerPayloads);
        // init common features
        CommonProxy.init(eventBus);
        // load ldlib2 plugin
        loadPlugins();
    }

    private static void loadPlugins() {
        ReflectionUtils.findAnnotationClasses(LDLibPlugin.class, data -> true, clazz -> {
            try {
                if (clazz.getConstructor().newInstance() instanceof ILDLibPlugin plugin) {
                    plugin.onLoad();
                }
            } catch (Throwable throwable) {
                LDLib2.LOGGER.error("Failed to load plugin {}", clazz.getName(), throwable);
            }
        }, () -> {});
    }

    public static void init(Object eventBus) {
        LDLib2Registries.init();
        CommonListeners.init();
        AccessorRegistries.init();
        RPCPacketDistributor.init();
        LDLNetworking.init();
        PropertyRegistry.init();
        LDMenuTypes.init(eventBus);
        TypeHandles.init();

        registerDeferred(BLOCKS, eventBus);
        registerDeferred(ITEMS, eventBus);
        registerDeferred(BLOCK_ENTITY_TYPES, eventBus);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void addListener(Object eventBus, Consumer<?> listener) {
        if (eventBus instanceof net.neoforged.bus.api.IEventBus bus) {
            bus.addListener((Consumer) listener);
            return;
        }
        try {
            var method = eventBus.getClass().getMethod("addListener", Consumer.class);
            method.invoke(eventBus, listener);
        } catch (ReflectiveOperationException ignored) {
        }
    }

    private static void registerDeferred(DeferredRegister<?> register, Object eventBus) {
        register.register();
    }

}
