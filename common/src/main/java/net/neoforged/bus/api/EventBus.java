package net.neoforged.bus.api;

public class EventBus implements IEventBus {
    public boolean post(Event event) {
        return event.isCanceled();
    }
}
