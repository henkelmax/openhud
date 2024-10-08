package de.maxhenkel.openhud.impl;

import de.maxhenkel.openhud.api.PlayerWaypoints;
import de.maxhenkel.openhud.api.Waypoint;
import de.maxhenkel.openhud.waypoints.WaypointServerManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class ServerPlayerWaypointsImpl implements PlayerWaypoints {

    protected ServerLevelWaypointsImpl levelWaypoints;
    @Nullable
    protected de.maxhenkel.openhud.waypoints.PlayerWaypoints playerWaypoints;
    protected UUID playerId;

    public ServerPlayerWaypointsImpl(ServerLevelWaypointsImpl levelWaypoints, UUID playerId) {
        this.levelWaypoints = levelWaypoints;
        this.playerId = playerId;
    }

    public de.maxhenkel.openhud.waypoints.PlayerWaypoints getPlayerWaypoints() {
        if (playerWaypoints == null) {
            playerWaypoints = levelWaypoints.getWaypointManager().getWaypoints(playerId);
        }
        return playerWaypoints;
    }

    public ServerLevelWaypointsImpl getLevelWaypoints() {
        return levelWaypoints;
    }

    @Nullable
    public ServerPlayer findPlayer() {
        return levelWaypoints.getServerLevel().getServer().getPlayerList().getPlayer(playerId);
    }

    public void addOrUpdateWaypoint(Waypoint waypoint) {
        if (!(waypoint instanceof ServerWaypointImpl waypointImpl)) {
            return;
        }
        WaypointServerManager waypointManager = levelWaypoints.getWaypointManager();

        ServerPlayer player = findPlayer();
        if (player == null) {
            waypointManager.addOrUpdateWaypointWithoutSendingToClient(playerId, waypointImpl.getWaypoint());
        } else {
            waypointManager.addOrUpdateWaypoint(player, waypointImpl.getWaypoint());
        }
    }

    @Override
    public Stream<Waypoint> getWaypoints() {
        return getPlayerWaypoints().getWaypoints().stream().map(waypoint -> new ServerWaypointImpl(this, waypoint));
    }

    @Override
    public Optional<Waypoint> getById(UUID id) {
        return getPlayerWaypoints().getById(id).map(waypoint -> new ServerWaypointImpl(this, waypoint));
    }

    @Override
    public Waypoint.Builder newWaypoint() {
        return new CreateServerBuilderImpl();
    }

    @Override
    public boolean removeWaypoint(UUID waypointId) {
        return getPlayerWaypoints().removeWaypoint(waypointId) != null;
    }

    public static abstract class CreateBuilderImpl implements Waypoint.Builder {

        protected UUID id;
        protected BlockPos position;
        protected Component name;
        protected ResourceLocation icon;
        protected Integer color;
        protected Boolean visible;
        protected Boolean readOnly;

        @Override
        public Waypoint.Builder id(UUID id) {
            this.id = id;
            return this;
        }

        @Override
        public Waypoint.Builder position(BlockPos position) {
            this.position = position;
            return this;
        }

        @Override
        public Waypoint.Builder name(Component name) {
            this.name = name;
            return this;
        }

        @Override
        public Waypoint.Builder icon(@Nullable ResourceLocation icon) {
            this.icon = icon;
            return this;
        }

        @Override
        public Waypoint.Builder color(int color) {
            this.color = color;
            return this;
        }

        @Override
        public Waypoint.Builder visible(boolean visible) {
            this.visible = visible;
            return this;
        }

        @Override
        public Waypoint.Builder readOnly(boolean readOnly) {
            this.readOnly = readOnly;
            return this;
        }

        protected de.maxhenkel.openhud.waypoints.Waypoint construct() {
            if (name == null || name.getString().isBlank()) {
                throw new IllegalStateException("Waypoint name cannot be empty");
            }
            if (name.getString().length() > de.maxhenkel.openhud.waypoints.Waypoint.MAX_WAYPOINT_NAME_LENGTH) {
                throw new IllegalStateException("Waypoint name cannot exceed %s characters".formatted(de.maxhenkel.openhud.waypoints.Waypoint.MAX_WAYPOINT_NAME_LENGTH));
            }
            de.maxhenkel.openhud.waypoints.Waypoint waypoint = new de.maxhenkel.openhud.waypoints.Waypoint(id == null ? UUID.randomUUID() : id, name);

            if (position != null) {
                waypoint.setPosition(position);
            }
            if (icon != null) {
                waypoint.setIcon(icon);
            }
            if (color != null) {
                waypoint.setColor(color);
            }
            if (visible != null) {
                waypoint.setVisible(visible);
            }
            if (readOnly != null) {
                waypoint.setReadOnly(readOnly);
            }
            return waypoint;
        }

    }

    private class CreateServerBuilderImpl extends CreateBuilderImpl {
        @Override
        public Waypoint save() {
            ServerWaypointImpl waypointImpl = new ServerWaypointImpl(ServerPlayerWaypointsImpl.this, construct());
            addOrUpdateWaypoint(waypointImpl);
            return waypointImpl;
        }
    }

}
