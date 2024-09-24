package de.maxhenkel.openhud.net;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public abstract class PayloadWrapper<T> implements CustomPacketPayload {

    protected final T payload;

    public PayloadWrapper(T payload) {
        this.payload = payload;
    }

    public T getPayload() {
        return payload;
    }
}
