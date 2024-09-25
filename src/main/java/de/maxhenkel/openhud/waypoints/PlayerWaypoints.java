package de.maxhenkel.openhud.waypoints;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import javax.annotation.Nullable;
import java.util.*;

public class PlayerWaypoints {

    public static final Codec<PlayerWaypoints> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(Codec.list(Waypoint.CODEC).fieldOf("waypoints").forGetter(PlayerWaypoints::createWaypointsList)).apply(instance, PlayerWaypoints::new);
    });

    private static final StreamCodec<RegistryFriendlyByteBuf, List<Waypoint>> WAYPOINT_LIST_STREAM_CODEC = Waypoint.STREAM_CODEC.apply(ByteBufCodecs.list());

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerWaypoints> STREAM_CODEC = StreamCodec.composite(
            WAYPOINT_LIST_STREAM_CODEC,
            PlayerWaypoints::createWaypointsList,
            PlayerWaypoints::new
    );

    protected List<Waypoint> waypoints;

    public PlayerWaypoints() {
        this.waypoints = new ArrayList<>();
    }

    public PlayerWaypoints(List<Waypoint> waypoints) {
        this.waypoints = new ArrayList<>(waypoints.stream().filter(Objects::nonNull).toList());
    }

    public List<Waypoint> createWaypointsList() {
        return new ArrayList<>(waypoints);
    }

    public List<Waypoint> getWaypoints() {
        return Collections.unmodifiableList(waypoints);
    }

    public Optional<Waypoint> getById(UUID id) {
        return waypoints.stream().filter(waypoint -> waypoint.getId().equals(id)).findFirst();
    }

    /**
     * @param waypoint the waypoint to add or update
     * @return the waypoint before it was updated
     */
    @Nullable
    public Waypoint addOrUpdateWaypoint(Waypoint waypoint) {
        for (int i = 0; i < waypoints.size(); i++) {
            Waypoint existingWaypoint = waypoints.get(i);
            if (existingWaypoint.getId().equals(waypoint.getId())) {
                waypoints.set(i, waypoint);
                return existingWaypoint;
            }
        }
        waypoints.add(waypoint);
        return null;
    }

    @Nullable
    public Waypoint removeWaypoint(Waypoint waypoint) {
        return removeWaypoint(waypoint.getId());
    }

    @Nullable
    public Waypoint removeWaypoint(UUID waypointId) {
        for (int i = 0; i < waypoints.size(); i++) {
            Waypoint existingWaypoint = waypoints.get(i);
            if (existingWaypoint.getId().equals(waypointId)) {
                waypoints.remove(i);
                return existingWaypoint;
            }
        }
        return null;
    }
}
