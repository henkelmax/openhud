package de.maxhenkel.openhud.api;

import net.minecraft.client.gui.screens.Screen;

import javax.annotation.Nullable;
import java.util.function.Function;

public interface ClientUtils {

    Screen createWaypointScreen(@Nullable Screen parent, Function<Waypoint.Builder, Waypoint.Builder> waypointBuilder);

}
