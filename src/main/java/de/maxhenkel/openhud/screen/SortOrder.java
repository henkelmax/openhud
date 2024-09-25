package de.maxhenkel.openhud.screen;

import de.maxhenkel.openhud.waypoints.Waypoint;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;

public enum SortOrder {

    CREATION_ASC(Component.translatable("message.openhud.order.creation_asc"), null),
    CREATION_DESC(Component.translatable("message.openhud.order.creation_desc"), null),
    NAME_ASC(Component.translatable("message.openhud.order.name_asc"), (w1, w2) -> w1.getName().getString().compareToIgnoreCase(w2.getName().getString())),
    NAME_DESC(Component.translatable("message.openhud.order.name_desc"), (w1, w2) -> w2.getName().getString().compareToIgnoreCase(w1.getName().getString())),
    DISTANCE_ASC(Component.translatable("message.openhud.order.distance_asc"), Comparator.comparingDouble(Waypoint::distanceToCamera)),
    DISTANCE_DESC(Component.translatable("message.openhud.order.distance_desc"), (w1, w2) -> Double.compare(w2.distanceToCamera(), w1.distanceToCamera()));

    private final Component name;
    @Nullable
    private final Comparator<Waypoint> comparator;

    SortOrder(Component name, @Nullable Comparator<Waypoint> comparator) {
        this.name = name;
        this.comparator = comparator;
    }

    public Component getName() {
        return name;
    }

    @Nullable
    public Comparator<Waypoint> getComparator() {
        return comparator;
    }

    /**
     * This method may sort the list in place or return a new list.
     *
     * @param waypoints the list to sort
     * @return the sorted list
     */
    public List<Waypoint> sort(List<Waypoint> waypoints) {
        if (comparator != null) {
            waypoints.sort(comparator);
            return waypoints;
        }
        if (this == CREATION_ASC) {
            return waypoints;
        }
        if (this == CREATION_DESC) {
            return waypoints.reversed();
        }
        return waypoints;
    }

}
