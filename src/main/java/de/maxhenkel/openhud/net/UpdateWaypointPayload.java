package de.maxhenkel.openhud.net;

import de.maxhenkel.openhud.Main;
import de.maxhenkel.openhud.waypoints.Waypoint;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class UpdateWaypointPayload extends PayloadWrapper<Waypoint> {

    public static final Type<UpdateWaypointPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Main.MODID, "update_waypoint"));

    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateWaypointPayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public UpdateWaypointPayload decode(RegistryFriendlyByteBuf buffer) {
            Waypoint waypoint = Waypoint.STREAM_CODEC.decode(buffer);
            ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, ResourceLocation.STREAM_CODEC.decode(buffer));
            return new UpdateWaypointPayload(waypoint, dimension);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, UpdateWaypointPayload value) {
            Waypoint.STREAM_CODEC.encode(buffer, value.getPayload());
            ResourceLocation.STREAM_CODEC.encode(buffer, value.getDimension().location());
        }
    };

    public UpdateWaypointPayload(Waypoint payload, ResourceKey<Level> dimension) {
        super(payload, dimension);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
