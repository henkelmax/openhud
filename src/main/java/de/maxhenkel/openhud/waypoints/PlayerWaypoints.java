package de.maxhenkel.openhud.waypoints;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import javax.annotation.Nullable;
import java.util.*;

public class PlayerWaypoints {

    public static final Codec<PlayerWaypoints> CODEC = CompoundTag.CODEC.xmap(PlayerWaypoints::fromNbt, PlayerWaypoints::toNbt);

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

    public CompoundTag toNbt() {
        CompoundTag tag = new CompoundTag();
        ListTag listTag = new ListTag();
        waypoints.forEach(waypoint -> listTag.add(waypoint.toNbt()));
        tag.put("waypoints", listTag);
        return tag;
    }

    public static PlayerWaypoints fromNbt(CompoundTag tag) {
        List<Waypoint> waypoints = new ArrayList<>();
        ListTag listTag = tag.getList("waypoints", Tag.TAG_COMPOUND);
        for (int i = 0; i < listTag.size(); i++) {
            waypoints.add(Waypoint.fromNbt(listTag.getCompound(i)));
        }
        return new PlayerWaypoints(waypoints);
    }

}
