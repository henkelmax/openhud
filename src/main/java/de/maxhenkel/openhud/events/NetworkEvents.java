package de.maxhenkel.openhud.events;

import de.maxhenkel.openhud.Main;
import de.maxhenkel.openhud.net.DeleteWaypointPayload;
import de.maxhenkel.openhud.net.PayloadWrapper;
import de.maxhenkel.openhud.net.UpdateWaypointPayload;
import de.maxhenkel.openhud.net.WaypointsPayload;
import de.maxhenkel.openhud.screen.UpdatableScreen;
import de.maxhenkel.openhud.waypoints.PlayerWaypoints;
import de.maxhenkel.openhud.waypoints.Waypoint;
import de.maxhenkel.openhud.waypoints.WaypointClientManager;
import de.maxhenkel.openhud.waypoints.WaypointServerManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.UUID;

public class NetworkEvents {

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(String.valueOf(Main.PROTOCOL_VERSION));
        registrar.playToClient(WaypointsPayload.TYPE, PlayerWaypoints.STREAM_CODEC.map(WaypointsPayload::new, PayloadWrapper::getPayload), (payload, context) -> {
            WaypointClientManager.updateWaypoints(payload.getPayload());
        });
        registrar.playBidirectional(UpdateWaypointPayload.TYPE, Waypoint.STREAM_CODEC.map(UpdateWaypointPayload::new, PayloadWrapper::getPayload), (payload, context) -> {
            if (context.flow().equals(PacketFlow.CLIENTBOUND)) {
                onUpdateClient(context, payload.getPayload());
            } else {
                onUpdateServer(context, payload.getPayload());
            }
        });
        registrar.playBidirectional(DeleteWaypointPayload.TYPE, UUIDUtil.STREAM_CODEC.map(DeleteWaypointPayload::new, PayloadWrapper::getPayload), (payload, context) -> {
            if (context.flow().equals(PacketFlow.CLIENTBOUND)) {
                onDeleteClient(context, payload.getPayload());
            } else {
                onDeleteServer(context, payload.getPayload());
            }
        });
    }

    private static void onUpdateServer(IPayloadContext context, Waypoint waypoint) {
        if (!(context.player() instanceof ServerPlayer player)) {
            return;
        }
        WaypointServerManager waypointServerManager = WaypointServerManager.get(player.serverLevel());
        if (!waypointServerManager.canEditWaypoint(player, waypoint.getId())) {
            Main.LOGGER.warn("Player {} tried to edit readonly waypoint {}", player.getName(), waypoint.getId());
            return;
        }
        WaypointServerManager.get(player.serverLevel()).addOrUpdateWaypoint(player, waypoint);
        context.reply(new UpdateWaypointPayload(waypoint));
    }

    @OnlyIn(Dist.CLIENT)
    private static void onUpdateClient(IPayloadContext context, Waypoint waypoint) {
        WaypointClientManager.getWaypoints().addOrUpdateWaypoint(waypoint);
        checkUpdateScreens();
    }

    private static void onDeleteServer(IPayloadContext context, UUID waypointId) {
        if (!(context.player() instanceof ServerPlayer player)) {
            return;
        }
        WaypointServerManager waypointServerManager = WaypointServerManager.get(player.serverLevel());
        if (!waypointServerManager.canEditWaypoint(player, waypointId)) {
            Main.LOGGER.warn("Player {} tried to delete readonly waypoint {}", player.getName(), waypointId);
            return;
        }
        waypointServerManager.removeWaypoint(player, waypointId);
        context.reply(new DeleteWaypointPayload(waypointId));
    }

    @OnlyIn(Dist.CLIENT)
    private static void onDeleteClient(IPayloadContext context, UUID waypointId) {
        WaypointClientManager.getWaypoints().removeWaypoint(waypointId);
        checkUpdateScreens();
    }

    @OnlyIn(Dist.CLIENT)
    private static void checkUpdateScreens() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen instanceof UpdatableScreen updatableScreen) {
            updatableScreen.update();
        }
    }

}
