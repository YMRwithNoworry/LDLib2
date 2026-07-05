package net.neoforged.fml;

import net.neoforged.bus.api.Event;

public final class ModLoader {
    private ModLoader() {
    }

    public static boolean postEvent(Event event) {
        return event.isCanceled();
    }
}
