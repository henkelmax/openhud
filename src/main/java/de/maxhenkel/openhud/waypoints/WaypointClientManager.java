package de.maxhenkel.openhud.waypoints;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(Dist.CLIENT)
public class WaypointClientManager {

    public static final ResourceKey<Level> OVERWORLD = ResourceKey.create(Registries.DIMENSION, ResourceLocation.withDefaultNamespace("overworld"));

    private static final Minecraft mc = Minecraft.getInstance();

    private static final Map<ResourceKey<Level>, PlayerWaypoints> waypoints = new HashMap<>();

    private WaypointClientManager() {

    }

    public static ResourceKey<Level> getFallback() {
        if (mc.level == null) {
            return OVERWORLD;
        } else {
            return mc.level.dimension();
        }
    }

    public static void updateWaypoints(ResourceKey<Level> dimension, PlayerWaypoints wp) {
        waypoints.put(dimension, wp);
    }

    public static PlayerWaypoints getWaypoints(ResourceKey<Level> dimension) {
        return waypoints.computeIfAbsent(dimension, resourceKey -> new PlayerWaypoints());
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
        waypoints.clear();
    }

}
