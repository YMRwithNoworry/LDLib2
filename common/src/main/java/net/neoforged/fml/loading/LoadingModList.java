package net.neoforged.fml.loading;

public final class LoadingModList {
    private static final LoadingModList INSTANCE = new LoadingModList();

    private LoadingModList() {
    }

    public static LoadingModList get() {
        return INSTANCE;
    }

    public Object getModFileById(String modId) {
        return dev.architectury.platform.Platform.isModLoaded(modId) ? modId : null;
    }
}
