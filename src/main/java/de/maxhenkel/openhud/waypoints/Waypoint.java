package de.maxhenkel.openhud.waypoints;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.UUID;

public class Waypoint {

    public static final Codec<Waypoint> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(UUIDUtil.CODEC.fieldOf("id").forGetter(Waypoint::getId),
                BlockPos.CODEC.fieldOf("position").forGetter(Waypoint::getPosition),
                ComponentSerialization.CODEC.fieldOf("name").forGetter(Waypoint::getName),
                Codec.INT.fieldOf("color").forGetter(Waypoint::getColor)).apply(instance, Waypoint::new);
    });

    public static final StreamCodec<RegistryFriendlyByteBuf, Waypoint> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            Waypoint::getId,
            BlockPos.STREAM_CODEC,
            Waypoint::getPosition,
            ComponentSerialization.STREAM_CODEC,
            Waypoint::getName,
            ByteBufCodecs.INT,
            Waypoint::getColor,
            Waypoint::new
    );

    protected final UUID id;
    protected BlockPos position;
    protected Component name;
    protected int color;

    public Waypoint(UUID id, BlockPos position, Component name, int color) {
        this.id = id;
        this.position = position;
        this.name = name;
        this.color = color;
    }

    public UUID getId() {
        return id;
    }

    public BlockPos getPosition() {
        return position;
    }

    public void setPosition(BlockPos position) {
        this.position = position;
    }

    public Component getName() {
        return name;
    }

    public void setName(Component name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
