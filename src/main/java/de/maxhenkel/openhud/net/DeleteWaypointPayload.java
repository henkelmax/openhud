package de.maxhenkel.openhud.net;

import de.maxhenkel.openhud.Main;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public class DeleteWaypointPayload extends PayloadWrapper<UUID> {

    public static final Type<DeleteWaypointPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Main.MODID, "delete_waypoint"));

    public DeleteWaypointPayload(UUID payload) {
        super(payload);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
