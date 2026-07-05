package net.minecraft.network.codec;

import java.util.function.BiConsumer;
import java.util.function.Function;

@FunctionalInterface
public interface StreamCodec<B, V> {
    V decode(B buffer);

    default void encode(B buffer, V value) {
        throw new UnsupportedOperationException("This StreamCodec does not support encoding");
    }

    default <O> StreamCodec<B, O> map(Function<V, O> decoderMapper, Function<O, V> encoderMapper) {
        StreamCodec<B, V> self = this;
        return of((buffer, value) -> self.encode(buffer, encoderMapper.apply(value)),
                buffer -> decoderMapper.apply(self.decode(buffer)));
    }

    static <B, V> StreamCodec<B, V> of(BiConsumer<B, V> encoder, Function<B, V> decoder) {
        return new StreamCodec<>() {
            @Override
            public V decode(B buffer) {
                return decoder.apply(buffer);
            }

            @Override
            public void encode(B buffer, V value) {
                encoder.accept(buffer, value);
            }
        };
    }

    static <B, V> StreamCodec<B, V> ofMember(BiConsumer<V, B> encoder, Function<B, V> decoder) {
        return of((buffer, value) -> encoder.accept(value, buffer), decoder);
    }
}
