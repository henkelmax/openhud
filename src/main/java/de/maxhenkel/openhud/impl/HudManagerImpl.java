package de.maxhenkel.openhud.impl;

import de.maxhenkel.openhud.api.LevelWaypoints;
import net.minecraft.server.level.ServerLevel;

public class HudManagerImpl implements de.maxhenkel.openhud.api.HudManager {

    public static final HudManagerImpl INSTANCE = new HudManagerImpl();

    private HudManagerImpl() {

    }

    @Override
    public LevelWaypoints getWaypointManager(ServerLevel serverLevel) {
        return new LevelWaypointsImpl(serverLevel);
    }

}
