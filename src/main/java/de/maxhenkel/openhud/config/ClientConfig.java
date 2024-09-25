package de.maxhenkel.openhud.config;

import de.maxhenkel.openhud.screen.SortOrder;
import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfig {

    public ModConfigSpec.BooleanValue hideHud;
    public ModConfigSpec.BooleanValue renderWaypointNames;
    public ModConfigSpec.DoubleValue hudFov;
    public ModConfigSpec.BooleanValue renderCardinalDirections;
    public ModConfigSpec.IntValue lineColor;
    public ModConfigSpec.EnumValue<SortOrder> waypointSortOrder;

    public ClientConfig(ModConfigSpec.Builder builder) {
        hideHud = builder.define("hide_hud", false);
        renderWaypointNames = builder.define("render_waypoint_names", true);
        renderCardinalDirections = builder.define("render_cardinal_directions", true);
        hudFov = builder.defineInRange("hud_fov", 180D, 12.5D, 360D);
        lineColor = builder.defineInRange("line_color", 0xFFFFFFFF, Integer.MIN_VALUE, Integer.MAX_VALUE);
        waypointSortOrder = builder.defineEnum("waypoint_sort_order", SortOrder.CREATION_ASC);
    }

}
