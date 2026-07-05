package net.neoforged.fml;

import net.neoforged.neoforgespi.language.ModFileScanData;

import java.util.Collections;
import java.util.List;

public final class ModList {
    private static final ModList INSTANCE = new ModList();

    private ModList() {
    }

    public static ModList get() {
        return INSTANCE;
    }

    public boolean isLoaded(String modId) {
        return dev.architectury.platform.Platform.isModLoaded(modId);
    }

    public List<ModFileScanData> getAllScanData() {
        return Collections.emptyList();
    }
}
