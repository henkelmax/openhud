package de.maxhenkel.openhud.render;

import de.maxhenkel.openhud.waypoints.Waypoint;

public class WaypointUtils {

    public static float getWaypointAngle(Waypoint waypoint, double cameraX, double cameraZ) {
        double waypointX = waypoint.getPosition().getX() + 0.5D;
        double waypointZ = waypoint.getPosition().getZ() + 0.5D;
        // Calculate dx and dz
        double dx = waypointX - cameraX;
        double dz = waypointZ - cameraZ;
        // Calculate the angle in radians
        double angleInRadians = Math.atan2(dx, dz);

        // Normalize the angle to range [0, 360)
        return (float) ((-Math.toDegrees(angleInRadians) + 360D) % 360D);
    }

}
