package de.maxhenkel.openhud.render;

import de.maxhenkel.openhud.Main;
import de.maxhenkel.openhud.utils.GraphicsUtils;
import de.maxhenkel.openhud.waypoints.Waypoint;
import de.maxhenkel.openhud.waypoints.WaypointClientManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;

public class RadarRenderer {

    private static final Minecraft mc = Minecraft.getInstance();

    private static final Comparator<Waypoint> DISTANCE_COMPARATOR = Comparator.comparingDouble(Waypoint::distanceToCamera);

    public static final Component SOUTH = Component.translatable("message.openhud.radar.south");
    public static final Component WEST = Component.translatable("message.openhud.radar.west");
    public static final Component NORTH = Component.translatable("message.openhud.radar.north");
    public static final Component EAST = Component.translatable("message.openhud.radar.east");

    public static final ResourceLocation GENERIC_MARKER = ResourceLocation.fromNamespaceAndPath(Main.MODID, "textures/hud/generic_marker.png");
    public static final ResourceLocation GENERIC_MARKER_OVERLAY = ResourceLocation.fromNamespaceAndPath(Main.MODID, "textures/hud/generic_marker_overlay.png");
    public static final int MARKER_SIZE = 6;
    public static final int GENERIC_MARKER_TEXTURE_SIZE = 8;

    public static final int HUD_FILL_COLOR = FastColor.ARGB32.colorFromFloat(0.25F, 0F, 0F, 0F);
    public static final int PADDING = 10;
    public static final int BOX_INNER_HORIZONTAL_PADDING = 2;
    public static final int LINE_HEIGHT = 6;
    public static final int SHORT_LINE_HEIGHT = 3;

    //TODO Add to waypoint properties
    public static final double MAX_DISTANCE = 1000D;
    public static final double MIN_DISTANCE = 8D;
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
            drawTopLine(guiGraphics, contentStartX, contentStartY, contentWidth, contentHeight, LINE_HEIGHT, south);
            drawString(guiGraphics, contentStartX, contentStartY, contentWidth, contentHeight, south, SOUTH);
            float southwest = calculateHudPosition(45F);
            drawTopLine(guiGraphics, contentStartX, contentStartY, contentWidth, contentHeight, SHORT_LINE_HEIGHT, southwest);
            float west = calculateHudPosition(90F);
            drawTopLine(guiGraphics, contentStartX, contentStartY, contentWidth, contentHeight, LINE_HEIGHT, west);
            drawString(guiGraphics, contentStartX, contentStartY, contentWidth, contentHeight, west, WEST);
            float northwest = calculateHudPosition(135F);
            drawTopLine(guiGraphics, contentStartX, contentStartY, contentWidth, contentHeight, SHORT_LINE_HEIGHT, northwest);
            float north = calculateHudPosition(180F);
            drawTopLine(guiGraphics, contentStartX, contentStartY, contentWidth, contentHeight, LINE_HEIGHT, north);
            drawString(guiGraphics, contentStartX, contentStartY, contentWidth, contentHeight, north, NORTH);
            float northeast = calculateHudPosition(225F);
            drawTopLine(guiGraphics, contentStartX, contentStartY, contentWidth, contentHeight, SHORT_LINE_HEIGHT, northeast);
            float east = calculateHudPosition(-90F);
            drawTopLine(guiGraphics, contentStartX, contentStartY, contentWidth, contentHeight, LINE_HEIGHT, east);
            drawString(guiGraphics, contentStartX, contentStartY, contentWidth, contentHeight, east, EAST);
            float southeast = calculateHudPosition(315F);
            drawTopLine(guiGraphics, contentStartX, contentStartY, contentWidth, contentHeight, SHORT_LINE_HEIGHT, southeast);
        }

        Vec3 worldPosition = mc.gameRenderer.getMainCamera().getPosition().multiply(1D, 0D, 1D);
        List<Waypoint> waypointsList = WaypointClientManager.getWaypoints().createWaypointsList();
        waypointsList.sort(DISTANCE_COMPARATOR);

        Waypoint displayWaypoint = null;
        float displayWaypointPos = 0.5F;
        float closest = 0.125F;

        for (int i = waypointsList.size() - 1; i >= 0; i--) {
            Waypoint waypoint = waypointsList.get(i);
            if (!waypoint.isVisible()) {
                continue;
            }
            double distance = Math.min(MAX_DISTANCE, worldPosition.distanceTo(waypoint.getPosition().getCenter().multiply(1D, 0D, 1D)));
            float waypointPos = calculateHudPosition(WaypointUtils.getWaypointAngle(waypoint, worldPosition.x, worldPosition.z));
            if (Math.abs(waypointPos - 0.5F) <= closest) {
                displayWaypoint = waypoint;
                displayWaypointPos = waypointPos;
                closest = Math.abs(waypointPos - 0.5F);
            }
            if (distance <= MIN_DISTANCE) {
                displayWaypoint = waypoint;
                displayWaypointPos = 0.5F;
                closest = 0F;
            }
            drawWaypoint(guiGraphics, contentStartX, contentStartY, contentWidth, contentHeight, waypointPos, waypoint, distance);
        }
        if (displayWaypoint != null && Main.CLIENT_CONFIG.renderWaypointNames.get()) {
            drawBottomLine(guiGraphics, contentStartX, contentStartY, contentWidth, contentHeight, SHORT_LINE_HEIGHT, displayWaypointPos);
            drawWaypointName(guiGraphics, contentStartX, contentStartY, contentWidth, contentHeight, displayWaypointPos, displayWaypoint);
        }
    }

    private static void drawTopLine(GuiGraphics guiGraphics, float hudX, float hudY, float hudWidth, float hudHeight, float lineHeight, float perc) {
        if (perc < 0F || perc > 1F) {
            return;
        }
        float posX = hudX + (hudWidth - 1F) * perc;
        GraphicsUtils.fill(guiGraphics, posX + 0.5F, hudY, posX + 1F + 0.5F, hudY + lineHeight, Main.CLIENT_CONFIG.lineColor.get());
    }

    private static void drawBottomLine(GuiGraphics guiGraphics, float hudX, float hudY, float hudWidth, float hudHeight, float lineHeight, float perc) {
        if (perc < 0F || perc > 1F) {
            return;
        }
        float posX = hudX + (hudWidth - 1F) * perc;
        GraphicsUtils.fill(guiGraphics, posX + 0.5F, hudY + hudHeight - lineHeight, posX + 1F + 0.5F, hudY + hudHeight, Main.CLIENT_CONFIG.lineColor.get());
    }

    private static void drawString(GuiGraphics guiGraphics, float hudX, float hudY, float hudWidth, float hudHeight, float perc, Component str) {
        if (perc < 0F || perc > 1F) {
            return;
        }
        float posX = hudX + (hudWidth - 1F) * perc + 1F;
        float stringWidth = mc.font.width(str);
        guiGraphics.drawString(mc.font, str.getVisualOrderText(), posX - stringWidth / 2F + 0.5F, hudY + hudHeight - mc.font.lineHeight - 2, 0xFFFFFF, false);
    }

    private static void drawWaypointName(GuiGraphics guiGraphics, float hudX, float hudY, float hudWidth, float hudHeight, float perc, Waypoint waypoint) {
        float centerX = hudX + hudWidth * perc;
        float padding = 2F;
        float stringWidth = mc.font.width(waypoint.getName());
        float textStartY = hudY + hudHeight + 4F;
        float textStartX = centerX - stringWidth / 2F;
        GraphicsUtils.fill(guiGraphics, textStartX - padding, textStartY - padding, textStartX + stringWidth + padding, textStartY + mc.font.lineHeight + padding, HUD_FILL_COLOR);
        guiGraphics.drawString(mc.font, waypoint.getName().getVisualOrderText(), textStartX + 0.5F, textStartY + 1F, 0xFFFFFF, false);
    }

    private static void drawWaypoint(GuiGraphics guiGraphics, float hudX, float hudY, float hudWidth, float hudHeight, float hudPositionFactor, Waypoint waypoint, double distance) {
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

        if (waypoint.getIcon() != null) {
            drawIconMarker(guiGraphics, waypoint.getIcon());
        } else {
            drawColorMarker(guiGraphics, waypoint.getColor());
        }

        guiGraphics.pose().popPose();
    }

    private static void drawColorMarker(GuiGraphics guiGraphics, int color) {
        float markerSize = MARKER_SIZE;
        float texPos = (float) MARKER_SIZE / (float) GENERIC_MARKER_TEXTURE_SIZE;
        GraphicsUtils.blitColored(guiGraphics, GENERIC_MARKER, -markerSize / 2, markerSize / 2, -markerSize / 2, markerSize / 2, 0F, texPos, 0F, texPos, color);
        GraphicsUtils.blit(guiGraphics, GENERIC_MARKER_OVERLAY, -markerSize / 2, markerSize / 2, -markerSize / 2, markerSize / 2, 0F, texPos, 0F, texPos);
    }

    private static void drawIconMarker(GuiGraphics guiGraphics, ResourceLocation icon) {
        float markerSize = MARKER_SIZE;
        GraphicsUtils.blit(guiGraphics, icon, -markerSize / 2, markerSize / 2, -markerSize / 2, markerSize / 2, 0F, 1F, 0F, 1F);
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
