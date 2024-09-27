package de.maxhenkel.openhud.events;

import com.mojang.blaze3d.platform.InputConstants;
import de.maxhenkel.openhud.Main;
import de.maxhenkel.openhud.api.OpenHud;
import de.maxhenkel.openhud.screen.WaypointScreen;
import de.maxhenkel.openhud.screen.WaypointsScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(Dist.CLIENT)
public class KeyEvents {

    private static final Minecraft mc = Minecraft.getInstance();

    public static KeyMapping HIDE_HUD;
    public static KeyMapping WAYPOINTS;
    public static KeyMapping CREATE_WAYPOINT;

    public static void onRegisterKeyBinds(RegisterKeyMappingsEvent event) {
        HIDE_HUD = new KeyMapping("key.openhud.hide_hud", InputConstants.UNKNOWN.getValue(), "key.categories.misc");
        event.register(HIDE_HUD);
        WAYPOINTS = new KeyMapping("key.openhud.waypoints", GLFW.GLFW_KEY_M, "key.categories.misc");
        event.register(WAYPOINTS);
        CREATE_WAYPOINT = new KeyMapping("key.openhud.create_waypoint", InputConstants.UNKNOWN.getValue(), "key.categories.misc");
        event.register(CREATE_WAYPOINT);
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (HIDE_HUD.consumeClick()) {
            Main.CLIENT_CONFIG.hideHud.set(!Main.CLIENT_CONFIG.hideHud.get());
        }
        if (WAYPOINTS.consumeClick()) {
            mc.setScreen(new WaypointsScreen(mc.screen));
        }
        if (CREATE_WAYPOINT.consumeClick()) {
            //mc.setScreen(new WaypointScreen(mc.screen, null));
            OpenHud.getClientWaypointManager().newWaypoint().name(Component.translatable("message.openhud.edit_waypoint.waypoint_name")).save();
            Screen clientTest = OpenHud.getClientUtils().createWaypointScreen(null, builder -> builder.name(Component.literal("Client Test")).position(new BlockPos(123, 64, 456)));
            mc.setScreen(clientTest);
        }
    }

}
