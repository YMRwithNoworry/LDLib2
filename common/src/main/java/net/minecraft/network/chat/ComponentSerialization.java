package net.minecraft.network.chat;

import com.mojang.serialization.Codec;

public final class ComponentSerialization {
    public static final Codec<Component> CODEC = Codec.STRING.xmap(Component::literal, Component::getString);

    private ComponentSerialization() {
    }
}
