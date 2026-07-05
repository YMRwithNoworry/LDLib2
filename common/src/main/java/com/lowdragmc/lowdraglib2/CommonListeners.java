package com.lowdragmc.lowdraglib2;

import com.lowdragmc.lowdraglib2.async.AsyncThreadData;
import com.lowdragmc.lowdraglib2.editor.resource.PackResourceManager;
import com.lowdragmc.lowdraglib2.gui.ui.elements.*;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * @author KilaBash
 * @date 2022/11/27
 * @implNote CommonListeners
 */
@EventBusSubscriber(modid = LDLib2.MOD_ID)
public class CommonListeners {
    private static boolean registered;

    public static void init() {
        if (registered) {
            return;
        }
        registered = true;

        LifecycleEvent.SERVER_BEFORE_START.register(CommonListeners::onServerBeforeStart);
        LifecycleEvent.SERVER_STOPPING.register(CommonListeners::onServerStopping);
        LifecycleEvent.SERVER_STOPPED.register(CommonListeners::onServerStopped);
        LifecycleEvent.SERVER_LEVEL_UNLOAD.register(CommonListeners::onServerLevelUnload);
        CommandRegistrationEvent.EVENT.register((dispatcher, registry, selection) ->
                ServerCommands.createServerCommands().forEach(dispatcher::register));
    }


    public static class ModCreativeModeTab {
        // Deferred register for creative tabs
        public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
                DeferredRegister.create(Registries.CREATIVE_MODE_TAB, LDLib2.MOD_ID);

        // Supplier for your dev-only tab
        public static final Supplier<CreativeModeTab> LDLIB2_DEV_TAB =
                CREATIVE_MODE_TABS.register("ldlib2_dev_tab", () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                        .title(Component.translatable("itemGroup.ldlib2.dev_tab"))
                        .icon(() -> ItemStack.EMPTY)
                        .build());

        // Method to hook the deferred register to the event bus
        public static void register(IEventBus eventBus) {
            CREATIVE_MODE_TABS.register();
        }
    }

    @SubscribeEvent
    public static void onWorldUnLoad(LevelEvent.Unload event) {
        LevelAccessor world = event.getLevel();
        if (!world.isClientSide() && world instanceof ServerLevel serverLevel) {
            AsyncThreadData.getOrCreate(serverLevel).releaseExecutorService();
        }
    }

    @SubscribeEvent
    public static void onServerAboutToStart(ServerAboutToStartEvent event) {
        onServerBeforeStart(event.getServer());
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        onServerStopped(event.getServer());
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        onServerStopping(event.getServer());
    }

    private static void onServerBeforeStart(MinecraftServer server) {
        Platform.MINECRAFT_SERVER = server;
        Platform.SERVER_REGISTRY_ACCESS = server.registryAccess();
    }

    private static void onServerStopped(MinecraftServer server) {
        Platform.SERVER_REGISTRY_ACCESS = null;
        Platform.MINECRAFT_SERVER = null;
    }

    private static void onServerStopping(MinecraftServer server) {
        var levels = server.getAllLevels();
        for (var level : levels) {
            if (!level.isClientSide()) {
                AsyncThreadData.getOrCreate(level).releaseExecutorService();
            }
        }
    }

    private static void onServerLevelUnload(ServerLevel level) {
        AsyncThreadData.getOrCreate(level).releaseExecutorService();
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        var dispatcher = event.getDispatcher();
        ServerCommands.createServerCommands().forEach(dispatcher::register);
    }

    @SubscribeEvent
    public static void onAddReloadListenerEvent(AddReloadListenerEvent event) {
        event.addListener(PackResourceManager.INSTANCE);
    }

    // TODO move example to somewhere else
//    @SubscribeEvent
//    public static void onContainerMenuCreateEvent(ContainerMenuEvent.Create event) throws Exception {
//        // furnace screen
//        if (event.menu instanceof AbstractFurnaceMenu furnaceMenu && furnaceMenu instanceof IModularUIHolderMenu uiHolderMenu) {
//            var player = event.player;
//            var field = AbstractFurnaceMenu.class.getDeclaredField("data");
//            field.setAccessible(true);
//            ContainerData data = (ContainerData) field.get(furnaceMenu);
//            var mui = ModularUI.of(UI.of(
//                    new UIElement().layout(l -> l.width(176).height(166)).addChildren(
//                            new UIElement().addChildren(
//                                    new Label().bind(DataBindingBuilder.componentS2C(() -> {
//                                        return Component.literal("burn time: %.2f / %.2f s"
//                                                .formatted(data.get(2) / 20f, data.get(3) / 20f));
//                                    }).build())
//                            ).layout(layout -> layout.positionType(TaffyPosition.ABSOLUTE)
//                                            .widthPercent(100).paddingAll(5).top(-15))
//                                    .style(style -> style.background(MCSprites.BORDER))
//                    )), player);
//            uiHolderMenu.setModularUI(mui);
//        }
//
//        // ae drive
//        if (event.menu instanceof DriveMenu driveMenu && driveMenu instanceof IModularUIHolderMenu uiHolderMenu) {
//            var player = event.player;
//            var mui = ModularUI.of(UI.of(
//                    new UIElement().layout(l -> l.width(176).height(201)).addChildren(
//                            new UIElement().addChildren(
//                                    new TextField().setNumbersOnlyInt(Integer.MIN_VALUE, Integer.MAX_VALUE)
//                                            .bind(DataBindingBuilder.string(() -> {
//                                                if (driveMenu.getBlockEntity() instanceof DriveBlockEntity entity) {
//                                                    return String.valueOf(entity.getPriority());
//                                                }
//                                                return String.valueOf(-1);
//                                            }, priority -> {
//                                                if (driveMenu.getBlockEntity() instanceof DriveBlockEntity entity) {
//                                                    try {
//                                                        entity.setPriority(Integer.parseInt(priority));
//                                                    } catch (NumberFormatException ignored) {
//                                                    }
//                                                }
//                                            }).build())
//                                    ).layout(layout -> layout.positionType(TaffyPosition.ABSOLUTE)
//                                            .width(50).paddingAll(5).left(173).top(-5))
//                                    .style(style -> style.background(MCSprites.BORDER))
//                    ), StylesheetManager.INSTANCE.getStylesheetSafe(StylesheetManager.MC)), player);
//            uiHolderMenu.setModularUI(mui);
//        }
//    }
}
