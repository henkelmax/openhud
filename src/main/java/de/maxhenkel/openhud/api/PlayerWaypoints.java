package de.maxhenkel.openhud.api;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public interface PlayerWaypoints {

    Stream<Waypoint> getWaypoints();

    Optional<Waypoint> getById(UUID id);

    Waypoint.Builder newWaypoint();

    boolean removeWaypoint(UUID waypointId);

    default boolean removeWaypoint(Waypoint waypoint) {
        return removeWaypoint(waypoint.getId());
    }

}
