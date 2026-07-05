package com.lowdragmc.lowdraglib2.compat.network.chat;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;

public final class ComponentSerialization {
    public static final Codec<Component> CODEC = Codec.STRING.xmap(Component::literal, Component::getString);

    private ComponentSerialization() {
    }
}
