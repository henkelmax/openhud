package de.maxhenkel.openhud.utils;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import net.minecraft.nbt.*;

import java.util.Optional;

public class CodecUtils {

    public static <T> CompoundTag toNBTCompound(Codec<T> codec, T object) {
        Tag tag = codec.encodeStart(NbtOps.INSTANCE, object).result().orElseGet(CompoundTag::new);
        if (tag instanceof CompoundTag compoundTag) {
            return compoundTag;
        } else {
            throw new IllegalArgumentException("Expected a compound tag");
        }
    }

    public static <T> Optional<T> fromNBT(Codec<T> codec, Tag nbt) {
        return codec.decode(NbtOps.INSTANCE, nbt).result().map(Pair::getFirst);
    }

}
