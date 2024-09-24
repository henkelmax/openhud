package de.maxhenkel.openhud.events;

import de.maxhenkel.openhud.Main;
import de.maxhenkel.openhud.net.DeleteWaypointPayload;
import de.maxhenkel.openhud.net.PayloadWrapper;
import de.maxhenkel.openhud.net.UpdateWaypointPayload;
import de.maxhenkel.openhud.net.WaypointsPayload;
import de.maxhenkel.openhud.waypoints.PlayerWaypoints;
import de.maxhenkel.openhud.waypoints.Waypoint;
import de.maxhenkel.openhud.waypoints.WaypointClientManager;
import de.maxhenkel.openhud.waypoints.WaypointServerManager;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class NetworkEvents {

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(String.valueOf(Main.PROTOCOL_VERSION));
        registrar.playToClient(WaypointsPayload.TYPE, PlayerWaypoints.STREAM_CODEC.map(WaypointsPayload::new, PayloadWrapper::getPayload), (payload, context) -> {
            WaypointClientManager.updateWaypoints(payload.getPayload());
        });
        registrar.playBidirectional(UpdateWaypointPayload.TYPE, Waypoint.STREAM_CODEC.map(UpdateWaypointPayload::new, PayloadWrapper::getPayload), (payload, context) -> {
            if (context.flow().equals(PacketFlow.CLIENTBOUND)) {
                WaypointClientManager.getWaypoints().addOrUpdateWaypoint(payload.getPayload());
            } else {
                if (!(context.player() instanceof ServerPlayer player)) {
                    return;
                }
                WaypointServerManager.get(player.serverLevel()).addOrUpdateWaypoint(player, payload.getPayload()); //TODO Check permissions
            }
        });
        registrar.playBidirectional(DeleteWaypointPayload.TYPE, UUIDUtil.STREAM_CODEC.map(DeleteWaypointPayload::new, PayloadWrapper::getPayload), (payload, context) -> {
            if (context.flow().equals(PacketFlow.CLIENTBOUND)) {
                WaypointClientManager.getWaypoints().removeWaypoint(payload.getPayload());
            } else {
                if (!(context.player() instanceof ServerPlayer player)) {
                    return;
                }
                WaypointServerManager.get(player.serverLevel()).removeWaypoint(player, payload.getPayload());
            }
        });
    }

}
