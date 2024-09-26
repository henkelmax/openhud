package de.maxhenkel.openhud.api;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public interface PlayerWaypoints {

    Stream<Waypoint> getWaypoints();

    Optional<Waypoint> getById(UUID id);

    Waypoint.Builder newWaypoint();

    /**
     * Removes a waypoint by ID.
     *
     * @param waypointId the ID of the waypoint
     * @return true if the waypoint was removed (This is always true client-side)
     */
    boolean removeWaypoint(UUID waypointId);

    /**
     * Removes a waypoint.
     *
     * @param waypoint the waypoint
     * @return true if the waypoint was removed (This is always true client-side)
     */
    default boolean removeWaypoint(Waypoint waypoint) {
        return removeWaypoint(waypoint.getId());
    }

}
