package de.maxhenkel.openhud.waypoints;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber
public class WaypointClientManager {

    private static final Minecraft mc = Minecraft.getInstance();

    private static PlayerWaypoints waypoints = new PlayerWaypoints();

    public WaypointClientManager() {
        waypoints = new PlayerWaypoints();
    }

    public static void updateWaypoints(PlayerWaypoints wp) {
        waypoints = wp;
    }

    public static PlayerWaypoints getWaypoints() {
        return waypoints;
    }

    @SubscribeEvent
    public static void onDisconnect(LevelEvent.Unload event) {
        clear();
    }

    @SubscribeEvent
    public static void onJoinServer(ClientPlayerNetworkEvent.LoggingIn event) {
        if (event.getPlayer() == mc.player) {
            clear();
        }
    }

    private static void clear() {
        waypoints = new PlayerWaypoints();
    }

}
