package de.maxhenkel.openhud.net;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public abstract class PayloadWrapper<T> implements CustomPacketPayload {

    protected final T payload;
    private final ResourceKey<Level> dimension;

    public PayloadWrapper(T payload, ResourceKey<Level> dimension) {
        this.payload = payload;
        this.dimension = dimension;
    }

    public T getPayload() {
        return payload;
    }

    public ResourceKey<Level> getDimension() {
        return dimension;
    }
}
