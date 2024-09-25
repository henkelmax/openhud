package de.maxhenkel.openhud.utils;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.*;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public class CodecUtils {

    public static <T> Optional<Tag> toNBT(Codec<T> codec, T object) {
        return codec.encodeStart(NbtOps.INSTANCE, object).result();
    }

    public static <T> Optional<T> fromNBT(Codec<T> codec, Tag nbt) {
        return codec.decode(NbtOps.INSTANCE, nbt).result().map(Pair::getFirst);
    }

    public static <T extends ByteBuf, U> StreamCodec<T, Optional<U>> optionalStreamCodec(StreamCodec<T, U> streamCodec) {
        return new StreamCodec<>() {
            @Override
            public Optional<U> decode(T byteBuf) {
                if (byteBuf.readBoolean()) {
                    return Optional.of(streamCodec.decode(byteBuf));
                }
                return Optional.empty();
            }

            @Override
            public void encode(T byteBuf, Optional<U> t) {
                if (t.isPresent()) {
                    byteBuf.writeBoolean(true);
                    streamCodec.encode(byteBuf, t.get());
                } else {
                    byteBuf.writeBoolean(false);
                }
            }
        };
    }

}
