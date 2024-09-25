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

        Builder id(UUID id);

        Builder position(BlockPos position);

        Builder name(Component name);

        Builder icon(@Nullable ResourceLocation icon);

        Builder color(int color);

        Builder visible(boolean visible);

        Builder readOnly(boolean readOnly);

        Waypoint save();

    }

}
