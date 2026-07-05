package net.neoforged.neoforge.registries;

import net.neoforged.bus.api.IEventBus;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class DeferredRegister<T> {
    private final Map<String, Supplier<? extends T>> entries = new LinkedHashMap<>();

    private DeferredRegister() {
    }

    public static <T> DeferredRegister<T> create(Object registry, String namespace) {
        return new DeferredRegister<>();
    }

    public <I extends T> DeferredHolder<T, I> register(String name, Supplier<I> supplier) {
        entries.put(name, supplier);
        return new DeferredHolder<>(supplier);
    }

    public void register(IEventBus eventBus) {
    }
}
