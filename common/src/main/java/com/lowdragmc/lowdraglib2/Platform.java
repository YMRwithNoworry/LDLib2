package com.lowdragmc.lowdraglib2;

import com.lowdragmc.lowdraglib2.utils.ResourceHelper;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

public class Platform {

    @Getter(lazy = true)
    private static final RegistryAccess BLANK_REGISTRY_ACCESS = getBlankRegistryAccess();

    @ApiStatus.Internal
    public static RegistryAccess SERVER_REGISTRY_ACCESS = null;

    @ApiStatus.Internal
    public static ResourceManager RESOURCE_MANAGER = null;

    // This is a helper method to check if the ServerLevel is safe to access.
    // @return true if the ServerLevel is not safe to access, otherwise false.
    public static boolean isServerNotSafe() {
        if (Platform.isClient()) {
            return Minecraft.getInstance().getConnection() == null;
        } else {
            var server = getMinecraftServer();
            return !serverSafe(server) || server.isCurrentlySaving();
        }
    }

    /**
     * @return true when the server can still accept scheduled work.
     */
    public static boolean serverSafe(MinecraftServer server) {
        return server != null && !server.isStopped() && !server.isShutdown() && server.isRunning();
    }

    /**
     * @return true when the current server can still accept scheduled work.
     */
    public static boolean serverSafe() {
        return serverSafe(getMinecraftServer());
    }

    public static String platformName() {
        return dev.architectury.platform.Platform.isForge() ? "forge" : "fabric";
    }

    public static boolean isForge() {
        return dev.architectury.platform.Platform.isForge();
    }

    public static boolean isDevEnv() {
        return dev.architectury.platform.Platform.isDevelopmentEnvironment();
    }

    public static boolean isDatagen() {
        return System.getProperty("fabric-api.datagen") != null
                || Boolean.getBoolean("forge.logging.mojang.level")
                || Boolean.getBoolean("net.minecraftforge.data.loading");
    }

    public static boolean isModLoaded(String modId) {
        return dev.architectury.platform.Platform.isModLoaded(modId);
    }

    public static boolean isClient() {
        return dev.architectury.platform.Platform.getEnv() == net.fabricmc.api.EnvType.CLIENT;
    }

    public static MinecraftServer getMinecraftServer() {
        return null;
    }

    public ResourceManager getResourceProvider() {
        return ResourceHelper.getResourceManager();
    }

    public static Path getGamePath() {
        return dev.architectury.platform.Platform.getGameFolder();
    }

    private static RegistryAccess getBlankRegistryAccess() {
        try {
            return RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
        } catch (Throwable e) {
            return new RegistryAccess.Frozen() {
                @Override
                public <T> @NotNull Optional<Registry<T>> registry(ResourceKey<? extends Registry<? extends T>> p_206220_) {
                    return Optional.empty();
                }

                @Override
                public @NotNull Stream<RegistryEntry<?>> registries() {
                    return Stream.empty();
                }

                @Override
                public @NotNull RegistryAccess.Frozen freeze() {
                    return this;
                }
            };
        }
    }

    public static RegistryAccess getFrozenRegistry() {
        RegistryAccess serverRegistryAccess = SERVER_REGISTRY_ACCESS;
        if (LDLib2.isServer()) {
            return serverRegistryAccess == null ? getBLANK_REGISTRY_ACCESS() : serverRegistryAccess;
        } else if (LDLib2.isRemote()) {
            if (Minecraft.getInstance().getConnection() != null) {
                return getRegistryFromMultipleSources(Minecraft.getInstance().getConnection().registryAccess(), serverRegistryAccess);
            }
        }
        return serverRegistryAccess == null ? getClientRegistryAccess() : serverRegistryAccess;
    }

    public static RegistryAccess getServerRegistryAccess() {
        return SERVER_REGISTRY_ACCESS == null ? getBLANK_REGISTRY_ACCESS() : SERVER_REGISTRY_ACCESS;
    }

    public static RegistryAccess getClientRegistryAccess() {
        if (LDLib2.isClient()) {
            if (Minecraft.getInstance().getConnection() != null) {
                return Minecraft.getInstance().getConnection().registryAccess();
            }
        }
        return SERVER_REGISTRY_ACCESS == null ? getBLANK_REGISTRY_ACCESS() : SERVER_REGISTRY_ACCESS;
    }

    private static RegistryAccess getRegistryFromMultipleSources(RegistryAccess... accesses) {
        return new RegistryAccess() {
            @Override
            public <E> Optional<Registry<E>> registry(ResourceKey<? extends Registry<? extends E>> registryKey) {
                for (RegistryAccess access : accesses) {
                    Optional<Registry<E>> registry = access.registry(registryKey);
                    if (registry.isPresent()) {
                        return registry;
                    }
                }
                return Optional.empty();
            }

            @Override
            public Stream<RegistryEntry<?>> registries() {
                return Arrays.stream(accesses).flatMap(RegistryAccess::registries);
            }
        };
    }

    public static void executeOnClient(Runnable runnable) {
        if (Platform.isClient()) {
            Minecraft.getInstance().execute(runnable);
        }
    }

    public static void executeOnServer(Runnable runnable) {
        if (LDLib2.isServer()) {
            getMinecraftServer().execute(runnable);
        }
    }
}
