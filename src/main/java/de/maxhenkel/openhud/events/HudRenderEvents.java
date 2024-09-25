package de.maxhenkel.openhud.events;

import de.maxhenkel.openhud.Main;
import de.maxhenkel.openhud.render.RadarRenderer;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(Dist.CLIENT)
public class HudRenderEvents {

    private static final Minecraft mc = Minecraft.getInstance();

    @SubscribeEvent
    public static void onRenderGuiLayer(RenderGuiLayerEvent.Pre event) {
        if (mc.options.hideGui) {
            return;
        }
        if (!VanillaGuiLayers.HOTBAR.equals(event.getName())) {
            return;
        }
        if (Main.CLIENT_CONFIG.hideHud.get()) {
            return;
        }
        RadarRenderer.render(event.getGuiGraphics());
    }

}
