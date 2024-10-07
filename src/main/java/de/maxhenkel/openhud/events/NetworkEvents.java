package de.maxhenkel.openhud.events;

import de.maxhenkel.openhud.Main;
import de.maxhenkel.openhud.net.DeleteWaypointPayload;
import de.maxhenkel.openhud.net.UpdateWaypointPayload;
import de.maxhenkel.openhud.net.WaypointsPayload;
import de.maxhenkel.openhud.screen.UpdatableScreen;
import de.maxhenkel.openhud.waypoints.Waypoint;
import de.maxhenkel.openhud.waypoints.WaypointClientManager;
import de.maxhenkel.openhud.waypoints.WaypointServerManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import javax.annotation.Nullable;
import java.util.UUID;

public class NetworkEvents {

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(String.valueOf(Main.PROTOCOL_VERSION));
        registrar.playToClient(WaypointsPayload.TYPE, WaypointsPayload.STREAM_CODEC, (payload, context) -> {
            WaypointClientManager.updateWaypoints(payload.getDimension(), payload.getPayload());
        });
        registrar.playBidirectional(UpdateWaypointPayload.TYPE, UpdateWaypointPayload.STREAM_CODEC, (payload, context) -> {
            if (context.flow().equals(PacketFlow.CLIENTBOUND)) {
                onUpdateClient(context, payload.getDimension(), payload.getPayload());
            } else {
                onUpdateServer(context, payload.getDimension(), payload.getPayload());
            }
        });
        registrar.playBidirectional(DeleteWaypointPayload.TYPE, DeleteWaypointPayload.STREAM_CODEC, (payload, context) -> {
            if (context.flow().equals(PacketFlow.CLIENTBOUND)) {
                onDeleteClient(context, payload.getDimension(), payload.getPayload());
            } else {
                onDeleteServer(context, payload.getDimension(), payload.getPayload());
            }
        });
    }

    private static void onUpdateServer(IPayloadContext context, ResourceKey<Level> dimension, Waypoint waypoint) {
        if (!(context.player() instanceof ServerPlayer player)) {
            return;
        }
        String waypointName = waypoint.getName().getString();
        if (waypointName.isBlank() || waypointName.length() > Waypoint.MAX_WAYPOINT_NAME_LENGTH) {
            Main.LOGGER.warn("Player {} tried to create/edit waypoint with invalid name", player.getName());
            return;
        }
        ServerLevel level = get(player, dimension);
        if (level == null) {
            Main.LOGGER.warn("Player {} tried to create/edit waypoint in invalid dimension {}", player.getName(), dimension);
            return;
        }
        WaypointServerManager waypointServerManager = WaypointServerManager.get(level);
        if (!waypointServerManager.canEditWaypoint(player, waypoint.getId())) {
            Main.LOGGER.warn("Player {} tried to create/edit readonly waypoint {}", player.getName(), waypoint.getId());
            return;
        }
        waypointServerManager.addOrUpdateWaypoint(player, waypoint);
        context.reply(new UpdateWaypointPayload(waypoint, level.dimension()));
    }

    @OnlyIn(Dist.CLIENT)
    private static void onUpdateClient(IPayloadContext context, ResourceKey<Level> dimension, Waypoint waypoint) {
        WaypointClientManager.getWaypoints(dimension).addOrUpdateWaypoint(waypoint);
        checkUpdateScreens();
    }

    private static void onDeleteServer(IPayloadContext context, ResourceKey<Level> dimension, UUID waypointId) {
        if (!(context.player() instanceof ServerPlayer player)) {
            return;
        }
        ServerLevel level = get(player, dimension);
        if (level == null) {
            Main.LOGGER.warn("Player {} tried to delete waypoint in invalid dimension {}", player.getName(), dimension);
            return;
        }
        WaypointServerManager waypointServerManager = WaypointServerManager.get(level);
        if (!waypointServerManager.canEditWaypoint(player, waypointId)) {
            Main.LOGGER.warn("Player {} tried to delete readonly waypoint {}", player.getName(), waypointId);
            return;
        }
        waypointServerManager.removeWaypoint(player, waypointId);
        context.reply(new DeleteWaypointPayload(waypointId, level.dimension()));
    }

    @OnlyIn(Dist.CLIENT)
    private static void onDeleteClient(IPayloadContext context, ResourceKey<Level> dimension, UUID waypointId) {
        WaypointClientManager.getWaypoints(dimension).removeWaypoint(waypointId);
        checkUpdateScreens();
    }

    @Nullable
    private static ServerLevel get(ServerPlayer player, ResourceKey<Level> dimension) {
        return player.serverLevel().getServer().getLevel(dimension);
    }

    @OnlyIn(Dist.CLIENT)
    private static void checkUpdateScreens() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen instanceof UpdatableScreen updatableScreen) {
            updatableScreen.update();
        }
    }

}
