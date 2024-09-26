package de.maxhenkel.openhud.api;

import de.maxhenkel.openhud.impl.ClientPlayerWaypointsImpl;
import de.maxhenkel.openhud.impl.ServerLevelWaypointsImpl;
import net.minecraft.server.level.ServerLevel;

public interface OpenHud {

    static LevelWaypoints getServerWaypointManager(ServerLevel serverLevel) {
        return new ServerLevelWaypointsImpl(serverLevel);
    }

    static PlayerWaypoints getClientWaypointManager() {
        return new ClientPlayerWaypointsImpl();
    }

}
