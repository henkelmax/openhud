package de.maxhenkel.openhud.api;

import net.minecraft.server.level.ServerLevel;

public interface HudManager {

    LevelWaypoints getWaypointManager(ServerLevel serverLevel);

}
