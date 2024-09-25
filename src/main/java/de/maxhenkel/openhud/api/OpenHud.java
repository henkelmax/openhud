package de.maxhenkel.openhud.api;

import de.maxhenkel.openhud.impl.LevelWaypointsImpl;
import net.minecraft.server.level.ServerLevel;

public interface OpenHud {

    static LevelWaypoints getWaypointManager(ServerLevel serverLevel){
        return new LevelWaypointsImpl(serverLevel);
    }

}
