package de.maxhenkel.openhud.waypoints;

import com.mojang.serialization.Codec;
import de.maxhenkel.openhud.screen.ColorPicker;
import de.maxhenkel.openhud.utils.CodecUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
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
import java.util.Random;
import java.util.UUID;

public class Waypoint implements Comparable<Waypoint> {

    public static final int MAX_WAYPOINT_NAME_LENGTH = 32;

    public static final Codec<Waypoint> CODEC = CompoundTag.CODEC.xmap(Waypoint::fromNbt, Waypoint::toNbt);

    public static final StreamCodec<RegistryFriendlyByteBuf, Waypoint> STREAM_CODEC = StreamCodec.of((buffer, value) -> {
        UUIDUtil.STREAM_CODEC.encode(buffer, value.getId());
        BlockPos.STREAM_CODEC.encode(buffer, value.getPosition());
        ComponentSerialization.STREAM_CODEC.encode(buffer, value.getName());
        CodecUtils.optionalStreamCodec(ResourceLocation.STREAM_CODEC).encode(buffer, value.getOptionalIcon());
        ByteBufCodecs.INT.encode(buffer, value.getColor());
        ByteBufCodecs.BOOL.encode(buffer, value.isVisible());
        ByteBufCodecs.BOOL.encode(buffer, value.isReadOnly());
    }, buffer -> new Waypoint(
            UUIDUtil.STREAM_CODEC.decode(buffer),
            BlockPos.STREAM_CODEC.decode(buffer),
            ComponentSerialization.STREAM_CODEC.decode(buffer),
            CodecUtils.optionalStreamCodec(ResourceLocation.STREAM_CODEC).decode(buffer),
            ByteBufCodecs.INT.decode(buffer),
            ByteBufCodecs.BOOL.decode(buffer),
            ByteBufCodecs.BOOL.decode(buffer)
    ));

    protected final UUID id;
    protected BlockPos position;
    protected Component name;
    @Nullable
    protected ResourceLocation icon;
    protected int color;
    protected boolean visible;
    protected boolean readOnly;

    public Waypoint(UUID id, BlockPos position, Component name, @Nullable ResourceLocation icon, int color, boolean visible, boolean readOnly) {
        this.id = id;
        this.position = position;
        this.name = name;
        this.icon = icon;
        this.color = color;
        this.visible = visible;
        this.readOnly = readOnly;
    }

    public Waypoint() {
        this(UUID.randomUUID(), Component.empty());
    }

    public Waypoint(UUID id, Component name) {
        this(id, BlockPos.ZERO, name, (ResourceLocation) null, randomColor(), true, false);
    }

    public Waypoint(UUID id, BlockPos position, Component name, Optional<ResourceLocation> icon, int color, boolean visible, boolean readOnly) {
        this(id, position, name, icon.orElse(null), color, visible, readOnly);
    }

    public Waypoint(UUID id, BlockPos position, Component name, int color, boolean visible, boolean readOnly) {
        this(id, position, name, (ResourceLocation) null, color, visible, readOnly);
    }

    public Waypoint(UUID id, BlockPos position, Component name, int color, boolean visible) {
        this(id, position, name, (ResourceLocation) null, color, visible, false);
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

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
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

    public CompoundTag toNbt() {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("id", id);
        tag.put("position", NbtUtils.writeBlockPos(position));
        CodecUtils.toNBT(ComponentSerialization.CODEC, name).ifPresent(value -> tag.put("name", value));
        if (icon != null) {
            tag.putString("icon", icon.toString());
        }
        tag.putInt("color", color);
        tag.putBoolean("visible", visible);
        tag.putBoolean("read_only", readOnly);
        return tag;
    }

    public static Waypoint fromNbt(CompoundTag tag) {
        UUID id = tag.getUUID("id");
        BlockPos position = NbtUtils.readBlockPos(tag, "position").orElse(BlockPos.ZERO);
        Component name = CodecUtils.fromNBT(ComponentSerialization.CODEC, tag.get("name")).orElse(Component.empty());
        ResourceLocation icon = null;
        if (tag.contains("icon", Tag.TAG_STRING)) {
            icon = ResourceLocation.tryParse(tag.getString("icon"));
        }
        int color = tag.getInt("color");
        boolean visible = tag.getBoolean("visible");
        boolean readOnly = tag.getBoolean("read_only");
        return new Waypoint(id, position, name, icon, color, visible, readOnly);
    }

    public static int randomColor() {
        return ColorPicker.getColor(new Random().nextFloat());
    }

}
