package de.maxhenkel.openhud.impl;

import de.maxhenkel.openhud.api.Waypoint;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.UUID;

public class ServerWaypointImpl implements Waypoint {

    protected ServerPlayerWaypointsImpl playerWaypoints;
    protected de.maxhenkel.openhud.waypoints.Waypoint waypoint;

    public ServerWaypointImpl(ServerPlayerWaypointsImpl playerWaypoints, de.maxhenkel.openhud.waypoints.Waypoint waypoint) {
        this.playerWaypoints = playerWaypoints;
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
        return new ServerEditorBuilderImpl();
    }

    public static abstract class EditorBuilderImpl implements Builder {

        protected ValueHolder<BlockPos> position;
        protected ValueHolder<Component> name;
        protected ValueHolder<ResourceLocation> icon;
        protected ValueHolder<Integer> color;
        protected ValueHolder<Boolean> visible;
        protected ValueHolder<Boolean> readOnly;

        @Override
        public Builder id(UUID id) {
            throw new IllegalCallerException("Cannot change ID of existing waypoint");
        }

        @Override
        public Builder position(BlockPos position) {
            this.position = ValueHolder.of(position);
            return this;
        }

        @Override
        public Builder name(Component name) {
            this.name = ValueHolder.of(name);
            return this;
        }

        @Override
        public Builder icon(@Nullable ResourceLocation icon) {
            this.icon = ValueHolder.of(icon);
            return this;
        }

        @Override
        public Builder color(int color) {
            this.color = ValueHolder.of(color);
            return this;
        }

        @Override
        public Builder visible(boolean visible) {
            this.visible = ValueHolder.of(visible);
            return this;
        }

        @Override
        public Builder readOnly(boolean readOnly) {
            this.readOnly = ValueHolder.of(readOnly);
            return this;
        }
    }

    private class ServerEditorBuilderImpl extends EditorBuilderImpl {

        @Override
        public Waypoint save() {
            if (position != null) {
                waypoint.setPosition(position.value());
            }
            if (name != null) {
                Component nameValue = name.value();
                if (nameValue.getString().isBlank()) {
                    throw new IllegalStateException("Waypoint name cannot be empty");
                }
                if (nameValue.getString().length() > de.maxhenkel.openhud.waypoints.Waypoint.MAX_WAYPOINT_NAME_LENGTH) {
                    throw new IllegalStateException("Waypoint name cannot exceed %s characters".formatted(de.maxhenkel.openhud.waypoints.Waypoint.MAX_WAYPOINT_NAME_LENGTH));
                }
                waypoint.setName(nameValue);
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

            playerWaypoints.addOrUpdateWaypoint(ServerWaypointImpl.this);
            return ServerWaypointImpl.this;
        }
    }

    public static record ValueHolder<T>(@Nullable T value) {
        public static <T> ValueHolder<T> of(@Nullable T value) {
            return new ValueHolder<>(value);
        }
    }

}
