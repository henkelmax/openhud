package de.maxhenkel.openhud.api;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.UUID;

public interface Waypoint {

    UUID getId();

    BlockPos getPosition();

    Component getName();

    @Nullable
    ResourceLocation getIcon();

    int getColor();

    boolean isVisible();

    boolean isReadOnly();

    Builder edit();

    interface Builder {
        Builder setId(UUID id);

        Builder setPosition(BlockPos position);

        Builder setName(Component name);

        Builder setIcon(@Nullable ResourceLocation icon);

        Builder setColor(int color);

        Builder setVisible(boolean visible);

        Builder setReadOnly(boolean readOnly);

        Waypoint save();
    }

}
