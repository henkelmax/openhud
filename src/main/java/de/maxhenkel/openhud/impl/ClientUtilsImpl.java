package de.maxhenkel.openhud.impl;

import de.maxhenkel.openhud.api.ClientUtils;
import de.maxhenkel.openhud.api.Waypoint;
import de.maxhenkel.openhud.screen.WaypointScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.function.Function;

public class ClientUtilsImpl implements ClientUtils {

    public static final ClientUtilsImpl INSTANCE = new ClientUtilsImpl();

    private ClientUtilsImpl() {

    }

    @Override
    public Screen createWaypointScreen(@Nullable Screen parent, Function<Waypoint.Builder, Waypoint.Builder> waypointBuilder) {
        ScreenEditorBuilderImpl b = new ScreenEditorBuilderImpl();
        Waypoint.Builder result = waypointBuilder.apply(b);
        if (result != b) {
            throw new IllegalStateException("Cannot create a waypoint screen with a builder that is not the provided screen builder");
        }
        de.maxhenkel.openhud.waypoints.Waypoint waypoint = new de.maxhenkel.openhud.waypoints.Waypoint();

        if (b.position != null) {
            waypoint.setPosition(b.position.value());
        }
        if (b.name != null) {
            Component nameValue = b.name.value();
            if (nameValue.getString().isBlank()) {
                throw new IllegalStateException("Waypoint name cannot be empty");
            }
            if (nameValue.getString().length() > de.maxhenkel.openhud.waypoints.Waypoint.MAX_WAYPOINT_NAME_LENGTH) {
                throw new IllegalStateException("Waypoint name cannot exceed %s characters".formatted(de.maxhenkel.openhud.waypoints.Waypoint.MAX_WAYPOINT_NAME_LENGTH));
            }
            waypoint.setName(nameValue);
        }
        if (b.icon != null) {
            waypoint.setIcon(b.icon.value());
        }
        if (b.color != null) {
            waypoint.setColor(b.color.value());
        }
        if (b.visible != null) {
            waypoint.setVisible(b.visible.value());
        }
        return new WaypointScreen(parent, waypoint);
    }

    private static class ScreenEditorBuilderImpl extends ServerWaypointImpl.EditorBuilderImpl {
        @Override
        public Waypoint save() {
            throw new IllegalStateException("Cannot save waypoint screen builder");
        }
    }

}
