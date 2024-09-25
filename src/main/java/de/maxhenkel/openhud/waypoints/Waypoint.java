package de.maxhenkel.openhud.waypoints;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.maxhenkel.openhud.utils.CodecUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class Waypoint implements Comparable<Waypoint> {

    public static final Codec<Waypoint> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                UUIDUtil.CODEC.fieldOf("id").forGetter(Waypoint::getId),
                BlockPos.CODEC.fieldOf("position").forGetter(Waypoint::getPosition),
                ComponentSerialization.CODEC.fieldOf("name").forGetter(Waypoint::getName),
                ResourceLocation.CODEC.optionalFieldOf("icon").forGetter(Waypoint::getOptionalIcon),
                Codec.INT.fieldOf("color").forGetter(Waypoint::getColor),
                Codec.BOOL.fieldOf("visible").forGetter(Waypoint::isVisible)

        ).apply(instance, Waypoint::new);
    });

    public static final StreamCodec<RegistryFriendlyByteBuf, Waypoint> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            Waypoint::getId,
            BlockPos.STREAM_CODEC,
            Waypoint::getPosition,
            ComponentSerialization.STREAM_CODEC,
            Waypoint::getName,
            CodecUtils.optionalStreamCodec(ResourceLocation.STREAM_CODEC),
            Waypoint::getOptionalIcon,
            ByteBufCodecs.INT,
            Waypoint::getColor,
            ByteBufCodecs.BOOL,
            Waypoint::isVisible,
            Waypoint::new
    );

    protected final UUID id;
    protected BlockPos position;
    protected Component name;
    @Nullable
    protected ResourceLocation icon;
    protected int color;
    protected boolean visible;

    public Waypoint(UUID id, BlockPos position, Component name, ResourceLocation icon, int color, boolean visible) {
        this.id = id;
        this.position = position;
        this.name = name;
        this.icon = icon;
        this.color = color;
        this.visible = visible;
    }

    public Waypoint(UUID id, BlockPos position, Component name, Optional<ResourceLocation> icon, int color, boolean visible) {
        this(id, position, name, icon.orElse(null), color, visible);
    }

    public Waypoint(UUID id, BlockPos position, Component name, int color, boolean visible) {
        this(id, position, name, (ResourceLocation) null, color, visible);
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

    @Nullable
    public ResourceLocation getIcon() {
        return icon;
    }

    private Optional<ResourceLocation> getOptionalIcon() {
        return Optional.ofNullable(icon);
    }

    public void setIcon(@Nullable ResourceLocation icon) {
        this.icon = icon;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public int compareTo(@NotNull Waypoint o) {
        return name.getString().compareToIgnoreCase(o.getName().getString());
    }

    @OnlyIn(Dist.CLIENT)
    public double distanceToCamera() {
        Vec3 cameraPosition = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().multiply(1D, 0D, 1D);
        return cameraPosition.distanceTo(position.getCenter().multiply(1D, 0D, 1D));
    }

}
