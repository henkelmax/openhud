package de.maxhenkel.openhud.waypoints;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

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

    protected Map<UUID, Waypoint> waypoints;

    public PlayerWaypoints() {
        this.waypoints = new LinkedHashMap<>();
    }

    public PlayerWaypoints(List<Waypoint> waypoints) {
        this.waypoints = new HashMap<>(waypoints.stream().filter(Objects::nonNull).collect(Collectors.toMap(Waypoint::getId, waypoint -> waypoint)));
    }

    public List<Waypoint> createWaypointsList() {
        return new ArrayList<>(waypoints.values());
    }

    public Collection<Waypoint> getWaypoints() {
        return waypoints.values();
    }

    public Optional<Waypoint> getById(UUID id) {
        return Optional.ofNullable(waypoints.get(id));
    }

    /**
     * @param waypoint the waypoint to add or update
     * @return the waypoint before it was updated
     */
    @Nullable
    public Waypoint addOrUpdateWaypoint(Waypoint waypoint) {
        return waypoints.put(waypoint.getId(), waypoint);
    }

    @Nullable
    public Waypoint removeWaypoint(Waypoint waypoint) {
        return removeWaypoint(waypoint.getId());
    }

    @Nullable
    public Waypoint removeWaypoint(UUID waypointId) {
        return waypoints.remove(waypointId);
    }
}
