package de.maxhenkel.openhud.render;

import de.maxhenkel.openhud.Main;
import de.maxhenkel.openhud.waypoints.Waypoint;
import de.maxhenkel.openhud.waypoints.WaypointClientManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.phys.Vec3;

public class RadarRenderer {

    private static final Minecraft mc = Minecraft.getInstance();

    public static final ResourceLocation GENERIC_MARKER = ResourceLocation.fromNamespaceAndPath(Main.MODID, "textures/hud/generic_marker.png");
    public static final ResourceLocation GENERIC_MARKER_OVERLAY = ResourceLocation.fromNamespaceAndPath(Main.MODID, "textures/hud/generic_marker_overlay.png");
    public static final int GENERIC_MARKER_SIZE = 6;
    public static final int GENERIC_MARKER_TEXTURE_SIZE = 8;

    public static final int HUD_FILL_COLOR = FastColor.ARGB32.colorFromFloat(0.25F, 0F, 0F, 0F);
    public static final int PADDING = 10;
    public static final int BOX_INNER_HORIZONTAL_PADDING = 2;
    public static final int LINE_HEIGHT = 6;
    public static final int SHORT_LINE_HEIGHT = 3;

    //TODO Add to waypoint properties
    public static final double MAX_DISTANCE = 1000D;
    public static final double MIN_DISTANCE = 4D;
    public static final float MAX_ICON_SCALE = 1.5F;
    public static final float MIN_ICON_SCALE = 0.5F;

    public static void render(GuiGraphics guiGraphics) {
        updatePulseFactor();
        if (mc.player == null) {
            return;
        }
        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();

        int hudWidth = width / 2;
        int hudHeight = 20;
        int startX = width / 2 - hudWidth / 2;
        int startY = PADDING;
        guiGraphics.fill(startX, startY, startX + hudWidth, startY + hudHeight, HUD_FILL_COLOR);

        int contentStartX = startX + BOX_INNER_HORIZONTAL_PADDING;
        int contentStartY = startY;

        int contentWidth = hudWidth - BOX_INNER_HORIZONTAL_PADDING * 2;
        int contentHeight = hudHeight;

        if (Main.CLIENT_CONFIG.renderCardinalDirections.get()) {
            float south = calculateHudPosition(0F);
            drawLine(guiGraphics, contentStartX, contentStartY, contentWidth, contentHeight, LINE_HEIGHT, south);
            drawString(guiGraphics, contentStartX, contentStartY, contentWidth, contentHeight, south, "S");
            float southwest = calculateHudPosition(45F);
            drawLine(guiGraphics, contentStartX, contentStartY, contentWidth, contentHeight, SHORT_LINE_HEIGHT, southwest);
            float west = calculateHudPosition(90F);
            drawLine(guiGraphics, contentStartX, contentStartY, contentWidth, contentHeight, LINE_HEIGHT, west);
            drawString(guiGraphics, contentStartX, contentStartY, contentWidth, contentHeight, west, "W");
            float northwest = calculateHudPosition(135F);
            drawLine(guiGraphics, contentStartX, contentStartY, contentWidth, contentHeight, SHORT_LINE_HEIGHT, northwest);
            float north = calculateHudPosition(180F);
            drawLine(guiGraphics, contentStartX, contentStartY, contentWidth, contentHeight, LINE_HEIGHT, north);
            drawString(guiGraphics, contentStartX, contentStartY, contentWidth, contentHeight, north, "N");
            float northeast = calculateHudPosition(225F);
            drawLine(guiGraphics, contentStartX, contentStartY, contentWidth, contentHeight, SHORT_LINE_HEIGHT, northeast);
            float east = calculateHudPosition(-90F);
            drawLine(guiGraphics, contentStartX, contentStartY, contentWidth, contentHeight, LINE_HEIGHT, east);
            drawString(guiGraphics, contentStartX, contentStartY, contentWidth, contentHeight, east, "E");
            float southeast = calculateHudPosition(315F);
            drawLine(guiGraphics, contentStartX, contentStartY, contentWidth, contentHeight, SHORT_LINE_HEIGHT, southeast);
        }

        Vec3 worldPosition = mc.gameRenderer.getMainCamera().getPosition();
        for (Waypoint waypoint : WaypointClientManager.getWaypoints().getWaypoints()) {
            float waypointPos = calculateHudPosition(WaypointUtils.getWaypointAngle(waypoint, worldPosition.x, worldPosition.z));
            drawWaypoint(guiGraphics, contentStartX, contentStartY, contentWidth, contentHeight, waypointPos, waypoint, worldPosition);
        }
    }

    private static void drawLine(GuiGraphics guiGraphics, float hudX, float hudY, float hudWidth, float hudHeight, float lineHeight, float perc) {
        if (perc < 0F || perc > 1F) {
            return;
        }
        float posX = hudX + (hudWidth - 1F) * perc;
        GraphicsUtils.fill(guiGraphics, posX, hudY, posX + 1F, hudY + lineHeight, Main.CLIENT_CONFIG.lineColor.get());
    }

    private static void drawString(GuiGraphics guiGraphics, float hudX, float hudY, float hudWidth, float hudHeight, float perc, String str) {
        if (perc < 0F || perc > 1F) {
            return;
        }
        float posX = hudX + (hudWidth - 1F) * perc + 1F;
        float stringWidth = mc.font.width(str);
        guiGraphics.drawString(mc.font, str, posX - stringWidth / 2F, hudY + hudHeight - mc.font.lineHeight - 2, 0xFFFFFF, false);
    }

    private static void drawWaypoint(GuiGraphics guiGraphics, float hudX, float hudY, float hudWidth, float hudHeight, float hudPositionFactor, Waypoint waypoint, Vec3 worldPosition) {
        double distance = Math.min(MAX_DISTANCE, worldPosition.multiply(1D, 0D, 1D).distanceTo(waypoint.getPosition().getCenter().multiply(1D, 0D, 1D)));

        float factor;
        if (distance > MIN_DISTANCE) {
            factor = 1F - (float) (distance / MAX_DISTANCE);
        } else {
            factor = pulseFactor;
            //Always center the waypoint if close by
            hudPositionFactor = 0.5F;
        }
        float scale = MIN_ICON_SCALE + (MAX_ICON_SCALE - MIN_ICON_SCALE) * factor;

        if (hudPositionFactor < 0F || hudPositionFactor > 1F) {
            return;
        }

        float posX = hudX + (hudWidth - 1F) * hudPositionFactor + 1F;
        float posY = hudY + hudHeight / 2F;
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(posX, posY, 0F);

        guiGraphics.pose().scale(scale, scale, 1F);

        //TODO Check if marker has custom icon
        drawColorMarker(guiGraphics, waypoint.getColor());

        guiGraphics.pose().popPose();
    }

    private static void drawColorMarker(GuiGraphics guiGraphics, int color) {
        float markerSize = GENERIC_MARKER_SIZE;
        float texPos = (float) GENERIC_MARKER_SIZE / (float) GENERIC_MARKER_TEXTURE_SIZE;
        GraphicsUtils.blitColored(guiGraphics, GENERIC_MARKER, -markerSize / 2, markerSize / 2, -markerSize / 2, markerSize / 2, 0F, texPos, 0F, texPos, color);
        GraphicsUtils.blit(guiGraphics, GENERIC_MARKER_OVERLAY, -markerSize / 2, markerSize / 2, -markerSize / 2, markerSize / 2, 0F, texPos, 0F, texPos);
    }

    /**
     * Calculates the HUD position based on the angle of the poi relative to the world direction while considering the FOV of the HUD.
     *
     * @param angle of the poi
     * @return the position of the poi on the HUD. 0 means left side of the HUD and 1 means right side. Anything smaller than 0 or greater than 1 will be ignored.
     */
    public static float calculateHudPosition(float angle) {
        float playerAngle = mc.gameRenderer.getMainCamera().getYRot();
        // Normalize both angles to a range between 0 and 360
        float normalizedPlayerAngle = (playerAngle + 360F) % 360F;
        float normalizedPoiAngle = (angle + 360F) % 360F;

        // Calculate the difference between the two angles
        float angleDifference = (normalizedPoiAngle - normalizedPlayerAngle + 360F) % 360F;

        // Adjust the difference to be within -180 to 180 degrees (shortest path between angles)
        if (angleDifference > 180F) {
            angleDifference -= 360F;
        }

        float fov = Main.CLIENT_CONFIG.hudFov.get().floatValue();

        // Now the angleDifference is between -180 and 180, we need to map it based on FOV
        float halfFov = fov / 2F;

        // Map the angleDifference to a range of [0, 1]
        // Convert -halfFov to halfFov into 0 to 1 (where 0.5 is forward direction)
        return (angleDifference + halfFov) / fov;
    }

    private static float pulseFactor;
    private static boolean growing;
    private static final float PULE_TIME_FACTOR = 0.1F;

    private static void updatePulseFactor() {
        float realtimeDeltaTicks = mc.getTimer().getRealtimeDeltaTicks();
        if (growing) {
            pulseFactor += realtimeDeltaTicks * PULE_TIME_FACTOR;
            if (pulseFactor >= 1F) {
                pulseFactor = 1F - (pulseFactor - 1F);
                growing = false;
            }
        } else {
            pulseFactor -= realtimeDeltaTicks * PULE_TIME_FACTOR;
            if (pulseFactor <= 0F) {
                pulseFactor = -pulseFactor;
                growing = true;
            }
        }
    }

}
