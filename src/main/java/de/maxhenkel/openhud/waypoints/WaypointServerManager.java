package de.maxhenkel.openhud.waypoints;

import de.maxhenkel.openhud.Main;
import de.maxhenkel.openhud.net.DeleteWaypointPayload;
import de.maxhenkel.openhud.net.UpdateWaypointPayload;
import de.maxhenkel.openhud.net.WaypointsPayload;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@EventBusSubscriber
public class WaypointServerManager extends SavedData {

    private final Map<UUID, PlayerWaypoints> waypoints;

    public WaypointServerManager(Map<UUID, PlayerWaypoints> waypoints) {
        this.waypoints = waypoints;
    }

    public WaypointServerManager() {
        this.waypoints = new HashMap<>();
    }

    private Optional<PlayerWaypoints> getOptionalWaypoints(UUID player) {
        return Optional.ofNullable(waypoints.get(player));
    }

    private Optional<PlayerWaypoints> getOptionalWaypoints(ServerPlayer player) {
        return getOptionalWaypoints(player.getUUID());
    }

    @Nonnull
    public PlayerWaypoints getWaypoints(UUID playerId) {
        return waypoints.computeIfAbsent(playerId, uuid -> new PlayerWaypoints());
    }

    @NotNull
    public PlayerWaypoints getWaypoints(ServerPlayer player) {
        return getWaypoints(player.getUUID());
    }

    @Nullable
    public Waypoint addOrUpdateWaypoint(ServerPlayer player, Waypoint waypoint) {
        Waypoint oldWaypoint = getWaypoints(player).addOrUpdateWaypoint(waypoint);
        PacketDistributor.sendToPlayer(player, new UpdateWaypointPayload(waypoint, player.serverLevel().dimension()));
        setDirty();
        return oldWaypoint;
    }

    @Nullable
    public Waypoint addOrUpdateWaypointWithoutSendingToClient(UUID player, Waypoint waypoint) {
        Waypoint oldWaypoint = getWaypoints(player).addOrUpdateWaypoint(waypoint);
        setDirty();
        return oldWaypoint;
    }

    @Nullable
    public Waypoint removeWaypoint(ServerPlayer player, UUID waypointId) {
        Waypoint removed = getWaypoints(player).removeWaypoint(waypointId);
        PacketDistributor.sendToPlayer(player, new DeleteWaypointPayload(waypointId, player.serverLevel().dimension()));
        setDirty();
        return removed;
    }

    public boolean canEditWaypoint(ServerPlayer player, UUID waypointId) {
        PlayerWaypoints playerWaypoints = getWaypoints(player);
        return playerWaypoints.getById(waypointId).map(w -> !w.isReadOnly()).orElse(true);
    }

    @Override
    public CompoundTag save(CompoundTag compound, HolderLookup.Provider registries) {
        CompoundTag waypointsTag = new CompoundTag();
        for (Map.Entry<UUID, PlayerWaypoints> entry : waypoints.entrySet()) {
            waypointsTag.put(entry.getKey().toString(), entry.getValue().toNbt());
        }
        compound.put("waypoints", waypointsTag);

        return compound;
    }

    public static WaypointServerManager load(CompoundTag compound, HolderLookup.Provider provider) {
        WaypointServerManager manager = new WaypointServerManager();
        CompoundTag waypointsTag = compound.getCompound("waypoints");

        for (String key : waypointsTag.getAllKeys()) {
            UUID uuid;
            try {
                uuid = UUID.fromString(key);
            } catch (IllegalArgumentException e) {
                Main.LOGGER.warn("Could not parse UUID from waypoint key: %s".formatted(key));
                continue;
            }
            manager.waypoints.put(uuid, PlayerWaypoints.fromNbt(waypointsTag.getCompound(key)));
        }
        return manager;
    }

    public static WaypointServerManager get(ServerLevel serverLevel) {
        return serverLevel.getDataStorage().computeIfAbsent(new SavedData.Factory<>(WaypointServerManager::new, WaypointServerManager::load), "openhud_waypoints");
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        updateWaypoints(player);
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        updateWaypoints(player);
    }

    private static void updateWaypoints(ServerPlayer player) {
        WaypointServerManager manager = get(player.serverLevel());
        manager.getOptionalWaypoints(player).ifPresent(waypoints -> {
            player.connection.send(new WaypointsPayload(waypoints, player.serverLevel().dimension()));
        });
    }

}
