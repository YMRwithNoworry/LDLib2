package net.neoforged.fml.loading;

import dev.architectury.platform.Platform;

import java.nio.file.Path;

public final class FMLLoader {
    private FMLLoader() {
    }

    public static Path getGamePath() {
        return Platform.getGameFolder();
    }

    public static boolean isProduction() {
        return !Platform.isDevelopmentEnvironment();
    }
}
