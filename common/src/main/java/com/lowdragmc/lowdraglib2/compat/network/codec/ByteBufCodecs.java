package com.lowdragmc.lowdraglib2.compat.network.codec;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class ByteBufCodecs {
    private ByteBufCodecs() {
    }

    public static final StreamCodec<FriendlyByteBuf, Boolean> BOOL = StreamCodec.of(FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean);
    public static final StreamCodec<FriendlyByteBuf, Byte> BYTE = StreamCodec.of((buf, value) -> buf.writeByte(value), FriendlyByteBuf::readByte);
    public static final StreamCodec<FriendlyByteBuf, Short> SHORT = StreamCodec.of((buf, value) -> buf.writeShort(value), FriendlyByteBuf::readShort);
    public static final StreamCodec<FriendlyByteBuf, Integer> INT = StreamCodec.of(FriendlyByteBuf::writeInt, FriendlyByteBuf::readInt);
    public static final StreamCodec<FriendlyByteBuf, Integer> VAR_INT = StreamCodec.of(FriendlyByteBuf::writeVarInt, FriendlyByteBuf::readVarInt);
    public static final StreamCodec<FriendlyByteBuf, Long> LONG = StreamCodec.of(FriendlyByteBuf::writeLong, FriendlyByteBuf::readLong);
    public static final StreamCodec<FriendlyByteBuf, Long> VAR_LONG = StreamCodec.of(FriendlyByteBuf::writeVarLong, FriendlyByteBuf::readVarLong);
    public static final StreamCodec<FriendlyByteBuf, Float> FLOAT = StreamCodec.of(FriendlyByteBuf::writeFloat, FriendlyByteBuf::readFloat);
    public static final StreamCodec<FriendlyByteBuf, Double> DOUBLE = StreamCodec.of(FriendlyByteBuf::writeDouble, FriendlyByteBuf::readDouble);
    public static final StreamCodec<FriendlyByteBuf, String> STRING_UTF8 = StreamCodec.of(FriendlyByteBuf::writeUtf, FriendlyByteBuf::readUtf);
    public static final StreamCodec<FriendlyByteBuf, Tag> TRUSTED_TAG = StreamCodec.of(
            (buf, tag) -> buf.writeNbt(tag instanceof CompoundTag compoundTag ? compoundTag : new CompoundTag()),
            FriendlyByteBuf::readNbt);
    public static final StreamCodec<FriendlyByteBuf, Vector3f> VECTOR3F = StreamCodec.of(
            (buf, value) -> {
                buf.writeFloat(value.x);
                buf.writeFloat(value.y);
                buf.writeFloat(value.z);
            },
            buf -> new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat()));
    public static final StreamCodec<FriendlyByteBuf, Quaternionf> QUATERNIONF = StreamCodec.of(
            (buf, value) -> {
                buf.writeFloat(value.x);
                buf.writeFloat(value.y);
                buf.writeFloat(value.z);
                buf.writeFloat(value.w);
            },
            buf -> new Quaternionf(buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat()));

    public static <V> StreamCodec<FriendlyByteBuf, V> fromCodec(Codec<V> codec) {
        return StreamCodec.of(
                (buf, value) -> {
                    var tag = codec.encodeStart(NbtOps.INSTANCE, value).getOrThrow(false, message -> {});
                    buf.writeNbt(tag instanceof CompoundTag compoundTag ? compoundTag : new CompoundTag());
                },
                buf -> codec.parse(NbtOps.INSTANCE, buf.readNbt()).result().orElseThrow());
    }

    public static <V> StreamCodec<FriendlyByteBuf, V> fromCodecWithRegistries(Codec<V> codec) {
        return fromCodec(codec);
    }

    public static <V> StreamCodec<FriendlyByteBuf, V> registry(net.minecraft.resources.ResourceKey<? extends Registry<V>> registryKey) {
        return StreamCodec.of(
                (buf, value) -> buf.writeResourceLocation(ResourceLocation.tryParse(String.valueOf(value))),
                buf -> {
                    throw new UnsupportedOperationException("Registry codec fallback cannot decode without registry instance");
                });
    }
}
