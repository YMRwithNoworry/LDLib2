package net.neoforged.neoforge.event;

import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.Event;

public final class EventHooks {
    private EventHooks() {
    }

    public static Event fireEntityTickPre(Entity entity) {
        return new Event();
    }

    public static void fireEntityTickPost(Entity entity) {
    }
}
