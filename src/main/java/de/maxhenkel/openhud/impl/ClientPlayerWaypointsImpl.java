package de.maxhenkel.openhud.impl;

import de.maxhenkel.openhud.api.PlayerWaypoints;
import de.maxhenkel.openhud.api.Waypoint;
import de.maxhenkel.openhud.net.DeleteWaypointPayload;
import de.maxhenkel.openhud.net.UpdateWaypointPayload;
import de.maxhenkel.openhud.waypoints.WaypointClientManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class ClientPlayerWaypointsImpl implements PlayerWaypoints {

    private static final Map<ResourceKey<Level>, ClientPlayerWaypointsImpl> INSTANCES = new HashMap<>();

    public static ClientPlayerWaypointsImpl get(ResourceKey<Level> dimension) {
        if (dimension == null) {
            dimension = WaypointClientManager.getFallback();
        }
        return INSTANCES.computeIfAbsent(dimension, ClientPlayerWaypointsImpl::new);
    }

    private final ResourceKey<Level> dimension;

    private ClientPlayerWaypointsImpl(ResourceKey<Level> dimension) {
        this.dimension = dimension;
    }

    @Override
    public Stream<Waypoint> getWaypoints() {
        return WaypointClientManager.getWaypoints(dimension).getWaypoints().stream().map(ClientWaypointImpl::new);
    }

    @Override
    public Optional<Waypoint> getById(UUID id) {
        return WaypointClientManager.getWaypoints(dimension).getById(id).map(ClientWaypointImpl::new);
    }

    @Override
    public Waypoint.Builder newWaypoint() {
        return new CreateServerBuilderImpl();
    }

    @Override
    public boolean removeWaypoint(UUID waypointId) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return false;
        }
        PacketDistributor.sendToServer(new DeleteWaypointPayload(waypointId, level.dimension()));
        return true;
    }

    private static class CreateServerBuilderImpl extends ServerPlayerWaypointsImpl.CreateBuilderImpl {

        @Override
        public Waypoint.Builder readOnly(boolean readOnly) {
            throw new IllegalCallerException("Cannot make waypoints read-only client-side");
        }

        @Override
        public Waypoint save() {
            de.maxhenkel.openhud.waypoints.Waypoint waypoint = construct();
            if (waypoint.isReadOnly()) {
                waypoint.setReadOnly(false);
            }
            ClientWaypointImpl waypointImpl = new ClientWaypointImpl(waypoint);
            ClientLevel level = Minecraft.getInstance().level;
            if (level != null) {
                PacketDistributor.sendToServer(new UpdateWaypointPayload(waypointImpl.getWaypoint(), level.dimension()));
            }
            return waypointImpl;
        }
    }

}
