package de.maxhenkel.openhud.impl;

import de.maxhenkel.openhud.api.Waypoint;
import de.maxhenkel.openhud.net.UpdateWaypointPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.UUID;

public class ClientWaypointImpl implements Waypoint {

    protected de.maxhenkel.openhud.waypoints.Waypoint waypoint;

    public ClientWaypointImpl(de.maxhenkel.openhud.waypoints.Waypoint waypoint) {
        this.waypoint = waypoint;
    }

    public de.maxhenkel.openhud.waypoints.Waypoint getWaypoint() {
        return waypoint;
    }

    @Override
    public UUID getId() {
        return waypoint.getId();
    }

    @Override
    public BlockPos getPosition() {
        return waypoint.getPosition();
    }

    @Override
    public Component getName() {
        return waypoint.getName();
    }

    @Nullable
    @Override
    public ResourceLocation getIcon() {
        return waypoint.getIcon();
    }

    @Override
    public int getColor() {
        return waypoint.getColor();
    }

    @Override
    public boolean isVisible() {
        return waypoint.isVisible();
    }

    @Override
    public boolean isReadOnly() {
        return waypoint.isReadOnly();
    }

    @Override
    public Builder edit() {
        return new ClientEditorBuilderImpl();
    }

    private class ClientEditorBuilderImpl extends ServerWaypointImpl.EditorBuilderImpl {

        @Override
        public Builder readOnly(boolean readOnly) {
            throw new IllegalCallerException("Cannot make waypoints read-only client-side");
        }

        @Override
        public Waypoint save() {
            if (position != null) {
                waypoint.setPosition(position.value());
            }
            if (name != null) {
                waypoint.setName(name.value());
            }
            if (icon != null) {
                waypoint.setIcon(icon.value());
            }
            if (color != null) {
                waypoint.setColor(color.value());
            }
            if (visible != null) {
                waypoint.setVisible(visible.value());
            }
            if (readOnly != null) {
                waypoint.setReadOnly(readOnly.value());
            }

            PacketDistributor.sendToServer(new UpdateWaypointPayload(waypoint));
            return ClientWaypointImpl.this;
        }
    }

}
