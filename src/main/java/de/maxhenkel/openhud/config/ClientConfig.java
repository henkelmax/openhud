package de.maxhenkel.openhud.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfig {

    public ModConfigSpec.BooleanValue hideHud;

    public ClientConfig(ModConfigSpec.Builder builder) {
        hideHud = builder.define("hide_hud", false);
    }

}
