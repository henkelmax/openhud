package de.maxhenkel.openhud.api;

import de.maxhenkel.openhud.impl.ClientPlayerWaypointsImpl;
import de.maxhenkel.openhud.impl.ClientUtilsImpl;
import de.maxhenkel.openhud.impl.ServerLevelWaypointsImpl;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public interface OpenHud {

    static LevelWaypoints getServerWaypointManager(ServerLevel serverLevel) {
        return new ServerLevelWaypointsImpl(serverLevel);
    }

    /**
     * @param dimension the dimension to get the waypoint manager for
     * @return waypoint manager of the dimension
     */
    static PlayerWaypoints getClientWaypointManager(ResourceKey<Level> dimension) {
        return ClientPlayerWaypointsImpl.get(dimension);
    }

    /**
     * @return the client waypoint manager of the dimension the player is in
     */
    static PlayerWaypoints getClientWaypointManager() {
        return ClientPlayerWaypointsImpl.get(null);
    }

    static ClientUtils getClientUtils() {
        return ClientUtilsImpl.INSTANCE;
    }

}
