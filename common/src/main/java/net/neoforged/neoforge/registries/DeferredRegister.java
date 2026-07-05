package net.neoforged.neoforge.registries;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;

import java.util.function.Supplier;

public class DeferredRegister<T> {
    private final dev.architectury.registry.registries.DeferredRegister<T> delegate;
    private boolean registered;

    private DeferredRegister(String namespace, ResourceKey<Registry<T>> registryKey) {
        this.delegate = dev.architectury.registry.registries.DeferredRegister.create(namespace, registryKey);
    }

    public static <T> DeferredRegister<T> create(Object registry, String namespace) {
        return new DeferredRegister<>(namespace, registryKey(registry));
    }

    public <I extends T> DeferredHolder<T, I> register(String name, Supplier<I> supplier) {
        return new DeferredHolder<>(delegate.register(name, supplier));
    }

    public void register(IEventBus eventBus) {
        register();
    }

    public void register() {
        if (!registered) {
            delegate.register();
            registered = true;
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> ResourceKey<Registry<T>> registryKey(Object registry) {
        if (registry instanceof ResourceKey<?> key) {
            return (ResourceKey<Registry<T>>) key;
        }
        if (registry instanceof Registry<?> vanillaRegistry) {
            return (ResourceKey<Registry<T>>) vanillaRegistry.key();
        }
        throw new IllegalArgumentException("Unsupported registry key source: " + registry);
    }
}
