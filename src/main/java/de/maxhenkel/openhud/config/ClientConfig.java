package de.maxhenkel.openhud.config;

import de.maxhenkel.openhud.screen.SortOrder;
import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfig {

    public ModConfigSpec.BooleanValue hideHud;
    public ModConfigSpec.BooleanValue renderWaypointNames;
    public ModConfigSpec.DoubleValue hudFov;
    public ModConfigSpec.DoubleValue hudWidth;
    public ModConfigSpec.DoubleValue hudScale;
    public ModConfigSpec.BooleanValue renderCardinalDirections;
    public ModConfigSpec.IntValue lineColor;
    public ModConfigSpec.EnumValue<SortOrder> waypointSortOrder;

    public ClientConfig(ModConfigSpec.Builder builder) {
        hideHud = builder.comment("If the HUD should be hidden").define("hide_hud", false);
        renderWaypointNames = builder.comment("If waypoint names should be rendered on the HUD").define("render_waypoint_names", true);
        renderCardinalDirections = builder.comment("If cardinal directions should be rendered on the HUD").define("render_cardinal_directions", true);
        hudFov = builder.comment("The FOV of the HUD").defineInRange("hud_fov", 180D, 12.5D, 360D);
        hudWidth = builder.comment("The width of the HUD").defineInRange("hud_width", 0.5D, 0.1D, 1D);
        hudScale = builder.comment("The scale of the HUD").defineInRange("hud_scale", 1D, 0.1D, 10D);
        lineColor = builder.comment("The color of the waypoint lines (ARGB packed as integer)").defineInRange("line_color", 0xFFFFFFFF, Integer.MIN_VALUE, Integer.MAX_VALUE);
        waypointSortOrder = builder.comment("The order in which waypoints are sorted in the waypoints screen").defineEnum("waypoint_sort_order", SortOrder.CREATION_ASC);
    }

}
