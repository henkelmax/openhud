package de.maxhenkel.openhud.events;

import com.mojang.blaze3d.platform.InputConstants;
import de.maxhenkel.openhud.Main;
import de.maxhenkel.openhud.screen.WaypointsScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber
public class KeyEvents {

    private static final Minecraft mc = Minecraft.getInstance();

    public static KeyMapping HIDE_HUD;
    public static KeyMapping WAYPOINTS;
    public static KeyMapping TEST;

    @OnlyIn(Dist.CLIENT)
    public static void onRegisterKeyBinds(RegisterKeyMappingsEvent event) {
        HIDE_HUD = new KeyMapping("key.hide_hud", InputConstants.UNKNOWN.getValue(), "key.categories.misc");
        event.register(HIDE_HUD);
        WAYPOINTS = new KeyMapping("key.waypoints", GLFW.GLFW_KEY_M, "key.categories.misc");
        event.register(WAYPOINTS);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (HIDE_HUD.consumeClick()) {
            Main.CLIENT_CONFIG.hideHud.set(!Main.CLIENT_CONFIG.hideHud.get());
        }
        if (WAYPOINTS.consumeClick()) {
            mc.setScreen(new WaypointsScreen(mc.screen));
        }
    }

}
