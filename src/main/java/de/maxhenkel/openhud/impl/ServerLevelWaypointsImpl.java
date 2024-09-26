package de.maxhenkel.openhud.impl;

import de.maxhenkel.openhud.api.LevelWaypoints;
import de.maxhenkel.openhud.api.PlayerWaypoints;
import de.maxhenkel.openhud.waypoints.WaypointServerManager;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ServerLevelWaypointsImpl implements LevelWaypoints {

    protected final ServerLevel serverLevel;
    @Nullable
    protected WaypointServerManager waypointManager;

    public ServerLevelWaypointsImpl(ServerLevel serverLevel) {
        this.serverLevel = serverLevel;
    }

    public WaypointServerManager getWaypointManager() {
        if (waypointManager == null) {
            waypointManager = WaypointServerManager.get(serverLevel);
        }
        return waypointManager;
    }

    public ServerLevel getServerLevel() {
        return serverLevel;
    }

    @NotNull
    @Override
    public PlayerWaypoints getWaypoints(UUID player) {
        return new ServerPlayerWaypointsImpl(this, player);
    }
}
