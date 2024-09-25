package de.maxhenkel.openhud.api;

import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nonnull;
import java.util.UUID;

public interface LevelWaypoints {

    @Nonnull
    PlayerWaypoints getWaypoints(UUID player);

    @Nonnull
    default PlayerWaypoints getWaypoints(ServerPlayer player) {
        return getWaypoints(player.getUUID());
    }

}
