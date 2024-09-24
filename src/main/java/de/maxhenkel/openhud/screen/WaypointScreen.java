package de.maxhenkel.openhud.screen;

import de.maxhenkel.openhud.net.UpdateWaypointPayload;
import de.maxhenkel.openhud.waypoints.Waypoint;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;

public class WaypointScreen extends Screen {

    private static final Component TITLE = Component.translatable("gui.openhud.edit_waypoint.title");
    private static final Component WAYPOINT_NAME = Component.translatable("message.openhud.edit_waypoint.waypoint_name").withStyle(ChatFormatting.GRAY);
    private static final Component VISIBLE = Component.translatable("message.openhud.edit_waypoint.visible").withStyle(ChatFormatting.GRAY);
    private static final Component COLOR = Component.translatable("message.openhud.edit_waypoint.color").withStyle(ChatFormatting.GRAY);
    private static final Component SAVE = Component.translatable("message.openhud.edit_waypoint.save");
    private static final Component CANCEL = Component.translatable("message.openhud.edit_waypoint.cancel");

    @Nullable
    protected Screen parent;
    protected EditBox waypointName;
    protected Checkbox visible;
    protected ColorDisplay waypointColor;

    protected Waypoint waypoint;

    public WaypointScreen(@Nullable Screen parent, Waypoint waypoint) {
        super(TITLE);
        this.parent = parent;
        this.waypoint = waypoint;
    }

    @Override
    protected void init() {
        super.init();
        LinearLayout contentLayout = LinearLayout.vertical().spacing(5);

        contentLayout.addChild(new StringWidget(WAYPOINT_NAME, font));
        waypointName = contentLayout.addChild(new EditBox(font, 200, 20, WAYPOINT_NAME));
        waypointName.setValue(waypoint.getName().getString());

        contentLayout.addChild(new StringWidget(COLOR, font));
        LinearLayout colorLayout = LinearLayout.horizontal().spacing(4);
        waypointColor = colorLayout.addChild(new ColorDisplay(0, 0, 20, 20, waypoint.getColor()));
        colorLayout.addChild(new ColorPicker(0, 0, 176, 20, color -> {
            waypointColor.setColor(color);
        }));
        contentLayout.addChild(colorLayout);

        visible = contentLayout.addChild(Checkbox.builder(VISIBLE, font).selected(waypoint.isVisible()).build());

        contentLayout.addChild(new SpacerElement(200, 10));

        LinearLayout linearlayout = LinearLayout.horizontal().spacing(4);
        linearlayout.addChild(Button.builder(SAVE, b -> {
            updateWaypoint();
            onClose();
        }).width(98).build());
        linearlayout.addChild(Button.builder(CANCEL, b -> onClose()).width(98).build());
        contentLayout.addChild(linearlayout);

        contentLayout.visitWidgets(this::addRenderableWidget);
        contentLayout.arrangeElements();
        FrameLayout.centerInRectangle(contentLayout, getRectangle());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(font, title, width / 2, 15, 0xFFFFFFFF);
    }

    @Override
    public void onClose() {
        super.onClose();
        if (parent instanceof UpdatableScreen updatableScreen) {
            updatableScreen.update();
        }
        minecraft.setScreen(parent);
    }

    private void updateWaypoint() {
        waypoint.setName(Component.literal(waypointName.getValue()));
        waypoint.setColor(waypointColor.getColor());
        waypoint.setVisible(visible.selected());
        PacketDistributor.sendToServer(new UpdateWaypointPayload(waypoint));
    }

}
