package de.maxhenkel.openhud;

import de.maxhenkel.openhud.config.ClientConfig;
import de.maxhenkel.openhud.events.KeyEvents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Main.MODID)
public class Main {

    public static final String MODID = "openhud";

    public static final Logger LOGGER = LogManager.getLogger(Main.MODID);

    public static ClientConfig CLIENT_CONFIG;

    public Main(IEventBus eventBus, ModContainer container) {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        CLIENT_CONFIG = new ClientConfig(builder);
        ModConfigSpec spec = builder.build();
        container.registerConfig(ModConfig.Type.CLIENT, spec);

        if (FMLEnvironment.dist.isClient()) {
            eventBus.addListener(KeyEvents::onRegisterKeyBinds);
        }
    }

}
