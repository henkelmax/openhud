package de.maxhenkel.openhud.net;

import de.maxhenkel.openhud.Main;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class DeleteWaypointPayload extends PayloadWrapper<UUID> {

    public static final Type<DeleteWaypointPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Main.MODID, "delete_waypoint"));

    public static final StreamCodec<RegistryFriendlyByteBuf, DeleteWaypointPayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public DeleteWaypointPayload decode(RegistryFriendlyByteBuf buffer) {
            UUID uuid = UUIDUtil.STREAM_CODEC.decode(buffer);
            ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, ResourceLocation.STREAM_CODEC.decode(buffer));
            return new DeleteWaypointPayload(uuid, dimension);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, DeleteWaypointPayload value) {
            UUIDUtil.STREAM_CODEC.encode(buffer, value.getPayload());
            ResourceLocation.STREAM_CODEC.encode(buffer, value.getDimension().location());
        }
    };

    public DeleteWaypointPayload(UUID payload, ResourceKey<Level> dimension) {
        super(payload, dimension);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
