package de.maxhenkel.openhud.net;

import de.maxhenkel.openhud.Main;
import de.maxhenkel.openhud.waypoints.PlayerWaypoints;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public class WaypointsPayload extends PayloadWrapper<PlayerWaypoints> {

    public static final CustomPacketPayload.Type<WaypointsPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Main.MODID, "waypoints"));

    public WaypointsPayload(PlayerWaypoints payload) {
        super(payload);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
