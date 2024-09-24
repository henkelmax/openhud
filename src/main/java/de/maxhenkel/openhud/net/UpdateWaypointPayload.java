package de.maxhenkel.openhud.net;

import de.maxhenkel.openhud.Main;
import de.maxhenkel.openhud.waypoints.Waypoint;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public class UpdateWaypointPayload extends PayloadWrapper<Waypoint> {

    public static final Type<UpdateWaypointPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Main.MODID, "update_waypoint"));

    public UpdateWaypointPayload(Waypoint payload) {
        super(payload);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
