package de.maxhenkel.openhud.api;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.function.Function;

public interface ClientUtils {

    default Screen createWaypointScreen(@Nullable Screen parent, Function<Waypoint.Builder, Waypoint.Builder> waypointBuilder) {
        return createWaypointScreen(parent, null, waypointBuilder);
    }

    Screen createWaypointScreen(@Nullable Screen parent, @Nullable ResourceKey<Level> dimension, Function<Waypoint.Builder, Waypoint.Builder> waypointBuilder);

}
