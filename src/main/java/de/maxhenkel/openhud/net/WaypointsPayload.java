package de.maxhenkel.openhud.net;

import de.maxhenkel.openhud.Main;
import de.maxhenkel.openhud.waypoints.PlayerWaypoints;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class WaypointsPayload extends PayloadWrapper<PlayerWaypoints> {

    public static final CustomPacketPayload.Type<WaypointsPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Main.MODID, "waypoints"));

    public static final StreamCodec<RegistryFriendlyByteBuf, WaypointsPayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public WaypointsPayload decode(RegistryFriendlyByteBuf buffer) {
            PlayerWaypoints waypoints = PlayerWaypoints.STREAM_CODEC.decode(buffer);
            ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, ResourceLocation.STREAM_CODEC.decode(buffer));
            return new WaypointsPayload(waypoints, dimension);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, WaypointsPayload value) {
            PlayerWaypoints.STREAM_CODEC.encode(buffer, value.getPayload());
            ResourceLocation.STREAM_CODEC.encode(buffer, value.getDimension().location());
        }
    };

    public WaypointsPayload(PlayerWaypoints payload, ResourceKey<Level> dimension) {
        super(payload, dimension);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
