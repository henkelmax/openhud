package de.maxhenkel.openhud.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import de.maxhenkel.openhud.waypoints.Waypoint;
import de.maxhenkel.openhud.waypoints.WaypointClientManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.FastColor;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class RadarRenderer {

    private static final Minecraft mc = Minecraft.getInstance();

    public static final int HUD_FILL_COLOR = FastColor.ARGB32.colorFromFloat(0.25F, 0F, 0F, 0F);
    public static final int DIRECTION_MARKER_COLOR = FastColor.ARGB32.colorFromFloat(0.5F, 1F, 1F, 1F);
    public static final int PADDING = 10;
    public static final int BOX_INNER_HORIZONTAL_PADDING = 2;
    public static final int LINE_HEIGHT = 6;
    public static final int SHORT_LINE_HEIGHT = 3;
    public static final float FOV = 180F;

    public static void render(GuiGraphics guiGraphics) {
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

        float south = calculateDirection(0F);
        drawLine(guiGraphics, contentStartX, contentStartY, contentWidth, contentHeight, LINE_HEIGHT, south);
        drawString(guiGraphics, contentStartX, contentStartY, contentWidth, contentHeight, south, "S");
        float southwest = calculateDirection(45F);
        drawLine(guiGraphics, contentStartX, contentStartY, contentWidth, contentHeight, SHORT_LINE_HEIGHT, southwest);
        float west = calculateDirection(90F);
        drawLine(guiGraphics, contentStartX, contentStartY, contentWidth, contentHeight, LINE_HEIGHT, west);
        drawString(guiGraphics, contentStartX, contentStartY, contentWidth, contentHeight, west, "W");
        float northwest = calculateDirection(135F);
        drawLine(guiGraphics, contentStartX, contentStartY, contentWidth, contentHeight, SHORT_LINE_HEIGHT, northwest);
        float north = calculateDirection(180F);
        drawLine(guiGraphics, contentStartX, contentStartY, contentWidth, contentHeight, LINE_HEIGHT, north);
        drawString(guiGraphics, contentStartX, contentStartY, contentWidth, contentHeight, north, "N");
        float northeast = calculateDirection(225F);
        drawLine(guiGraphics, contentStartX, contentStartY, contentWidth, contentHeight, SHORT_LINE_HEIGHT, northeast);
        float east = calculateDirection(-90F);
        drawLine(guiGraphics, contentStartX, contentStartY, contentWidth, contentHeight, LINE_HEIGHT, east);
        drawString(guiGraphics, contentStartX, contentStartY, contentWidth, contentHeight, east, "E");
        float southeast = calculateDirection(315F);
        drawLine(guiGraphics, contentStartX, contentStartY, contentWidth, contentHeight, SHORT_LINE_HEIGHT, southeast);

        Vec3 cameraPos = mc.gameRenderer.getMainCamera().getPosition();
        double cameraX = cameraPos.x;
        double cameraZ = cameraPos.z;
        for (Waypoint waypoint : WaypointClientManager.getWaypoints().getWaypoints()) {
            float waypointPos = calculateDirection(getWaypointAngle(waypoint, cameraX, cameraZ));
            drawLine(guiGraphics, contentStartX, contentStartY, contentWidth, contentHeight, LINE_HEIGHT, waypointPos);
            drawString(guiGraphics, contentStartX, contentStartY, contentWidth, contentHeight, waypointPos, "X");
        }
    }

    public static float getWaypointAngle(Waypoint waypoint, double cameraX, double cameraZ) {
        double waypointX = waypoint.getPosition().getX() + 0.5D;
        double waypointZ = waypoint.getPosition().getZ() + 0.5D;
        // Calculate dx and dz
        double dx = waypointX - cameraX;
        double dz = waypointZ - cameraZ;
        // Calculate the angle in radians
        double angleInRadians = Math.atan2(dx, dz);

        // Normalize the angle to range [0, 360)
        return (float) ((-Math.toDegrees(angleInRadians) + 360D) % 360D);
    }

    private static void drawLine(GuiGraphics guiGraphics, float hudX, float hudY, float hudWidth, float hudHeight, float lineHeight, float perc) {
        if (perc < 0F || perc > 1F) {
            return;
        }
        float posX = hudX + (hudWidth - 1F) * perc;
        fill(guiGraphics, posX, hudY, posX + 1F, hudY + lineHeight, DIRECTION_MARKER_COLOR);
    }

    private static void drawString(GuiGraphics guiGraphics, float hudX, float hudY, float hudWidth, float hudHeight, float perc, String str) {
        if (perc < 0F || perc > 1F) {
            return;
        }
        float posX = hudX + (hudWidth - 1F) * perc + 1F;
        float stringWidth = mc.font.width(str);
        guiGraphics.drawString(mc.font, str, posX - stringWidth / 2F, hudY + hudHeight - mc.font.lineHeight - 2, 0xFFFFFF, false);
    }

    private static void fill(GuiGraphics guiGraphics, float minX, float minY, float maxX, float maxY, int color) {
        Matrix4f matrix4f = guiGraphics.pose().last().pose();
        if (minX < maxX) {
            float i = minX;
            minX = maxX;
            maxX = i;
        }

        if (minY < maxY) {
            float j = minY;
            minY = maxY;
            maxY = j;
        }

        VertexConsumer vertexconsumer = guiGraphics.bufferSource().getBuffer(RenderType.gui());
        vertexconsumer.addVertex(matrix4f, minX, minY, 0F).setColor(color);
        vertexconsumer.addVertex(matrix4f, minX, maxY, 0F).setColor(color);
        vertexconsumer.addVertex(matrix4f, maxX, maxY, 0F).setColor(color);
        vertexconsumer.addVertex(matrix4f, maxX, minY, 0F).setColor(color);
        guiGraphics.flush();
    }

    public static float calculateDirection(float angle) {
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

        // Now the angleDifference is between -180 and 180, we need to map it based on FOV
        float halfFov = FOV / 2;

        // Map the angleDifference to a range of [0, 1]
        // Convert -halfFov to halfFov into 0 to 1 (where 0.5 is forward direction)
        return (angleDifference + halfFov) / FOV;
    }
}
