package de.maxhenkel.openhud;

import de.maxhenkel.openhud.api.HudManager;
import de.maxhenkel.openhud.config.ClientConfig;
import de.maxhenkel.openhud.events.KeyEvents;
import de.maxhenkel.openhud.events.NetworkEvents;
import de.maxhenkel.openhud.impl.HudManagerImpl;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Consumer;

@Mod(Main.MODID)
public class Main {

    private static final String IMC_METHOD = "getHudManager";

    public static final int PROTOCOL_VERSION = 1;
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

        eventBus.addListener(NetworkEvents::register);
        eventBus.addListener(Main::processImc);
    }

    private static void processImc(InterModProcessEvent event) {
        event.getIMCStream().filter(imcMessage -> IMC_METHOD.equals(imcMessage.method())).forEach(message -> {
            if (message.messageSupplier().get() instanceof Consumer<?> consumer) {
                Consumer<HudManager> hudManagerConsumer = (Consumer<HudManager>) consumer;
                hudManagerConsumer.accept(HudManagerImpl.INSTANCE);
            }
        });
    }

}
