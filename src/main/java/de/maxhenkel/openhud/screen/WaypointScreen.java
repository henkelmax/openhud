package de.maxhenkel.openhud.screen;

import de.maxhenkel.openhud.net.UpdateWaypointPayload;
import de.maxhenkel.openhud.waypoints.Waypoint;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.UUID;

public class WaypointScreen extends Screen {

    private static final Component TITLE = Component.translatable("gui.openhud.waypoint.title");
    private static final Component EDIT_WAYPOINT = Component.translatable("gui.openhud.edit_waypoint.title");
    private static final Component CREATE_WAYPOINT = Component.translatable("gui.openhud.create_waypoint.title");
    private static final Component WAYPOINT_NAME = Component.translatable("message.openhud.edit_waypoint.waypoint_name").withStyle(ChatFormatting.GRAY);
    private static final Component COORDINATES = Component.translatable("message.openhud.edit_waypoint.coordinates").withStyle(ChatFormatting.GRAY);
    private static final Component VISIBLE = Component.translatable("message.openhud.edit_waypoint.visible").withStyle(ChatFormatting.GRAY);
    private static final Component COLOR = Component.translatable("message.openhud.edit_waypoint.color").withStyle(ChatFormatting.GRAY);
    private static final Component SAVE = Component.translatable("message.openhud.edit_waypoint.save");
    private static final Component CANCEL = Component.translatable("message.openhud.edit_waypoint.cancel");

    private static final String COORDINATE_REGEX = "-?[0-9]{0,8}";

    @Nullable
    protected Screen parent;
    protected EditBox waypointName;
    protected EditBox coordinateX;
    protected EditBox coordinateY;
    protected EditBox coordinateZ;
    protected Checkbox visible;
    protected WaypointIconDisplay waypointColor;
    @Nullable
    protected ResourceLocation icon;
    protected Button saveButton;

    protected boolean newWaypoint;
    protected Waypoint waypoint;

    public WaypointScreen(@Nullable Screen parent, @Nullable Waypoint waypoint) {
        super(TITLE);
        this.parent = parent;
        if (waypoint == null) {
            minecraft = Minecraft.getInstance();
            newWaypoint = true;
            waypoint = new Waypoint(
                    UUID.randomUUID(),
                    minecraft.gameRenderer.getMainCamera().getBlockPosition(),
                    Component.empty(),
                    ColorPicker.getColor(minecraft.level != null ? minecraft.level.random.nextFloat() : 0.5F),
                    true
            );
        }
        this.waypoint = waypoint;
        icon = waypoint.getIcon();
    }

    @Override
    protected void init() {
        super.init();
        LinearLayout contentLayout = LinearLayout.vertical().spacing(5);

        contentLayout.addChild(new StringWidget(WAYPOINT_NAME, font));
        waypointName = contentLayout.addChild(new EditBox(font, 200, 20, WAYPOINT_NAME));
        waypointName.setValue(waypoint.getName().getString());
        waypointName.setMaxLength(Waypoint.MAX_WAYPOINT_NAME_LENGTH);

        contentLayout.addChild(new StringWidget(COORDINATES, font));
        LinearLayout coordsLayout = LinearLayout.horizontal().spacing(4);
        coordinateX = coordsLayout.addChild(new EditBox(font, 64, 20, COORDINATES));
        coordinateY = coordsLayout.addChild(new EditBox(font, 64, 20, COORDINATES));
        coordinateZ = coordsLayout.addChild(new EditBox(font, 64, 20, COORDINATES));
        coordinateX.setMaxLength(9);
        coordinateX.setFilter(s -> s.isEmpty() || s.matches(COORDINATE_REGEX));
        coordinateX.setValue(String.valueOf(waypoint.getPosition().getX()));
        coordinateY.setMaxLength(9);
        coordinateY.setFilter(s -> s.isEmpty() || s.matches(COORDINATE_REGEX));
        coordinateY.setValue(String.valueOf(waypoint.getPosition().getY()));
        coordinateZ.setMaxLength(9);
        coordinateZ.setFilter(s -> s.isEmpty() || s.matches(COORDINATE_REGEX));
        coordinateZ.setValue(String.valueOf(waypoint.getPosition().getZ()));
        contentLayout.addChild(coordsLayout);

        contentLayout.addChild(new StringWidget(COLOR, font));
        LinearLayout colorLayout = LinearLayout.horizontal().spacing(4);
        waypointColor = colorLayout.addChild(new WaypointIconDisplay(0, 0, 20, 20, waypoint.getColor(), waypoint.getIcon()));
        colorLayout.addChild(new ColorPicker(0, 0, 176, 20, color -> {
            waypointColor.setColor(color);
            icon = null;
        }));
        contentLayout.addChild(colorLayout);

        visible = contentLayout.addChild(Checkbox.builder(VISIBLE, font).selected(waypoint.isVisible()).build());

        contentLayout.addChild(new SpacerElement(200, 10));

        LinearLayout linearlayout = LinearLayout.horizontal().spacing(4);
        saveButton = linearlayout.addChild(Button.builder(SAVE, b -> {
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
    public void tick() {
        super.tick();
        if (saveButton != null) {
            saveButton.active = !waypointName.getValue().isBlank();
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(font, newWaypoint ? CREATE_WAYPOINT : EDIT_WAYPOINT, width / 2, 15, 0xFFFFFFFF);
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
        waypoint.setName(Component.literal(waypointName.getValue().trim()));
        waypoint.setPosition(new BlockPos(parseCoordinate(coordinateX), parseCoordinate(coordinateY), parseCoordinate(coordinateZ)));
        waypoint.setColor(waypointColor.getColor());
        waypoint.setIcon(icon);
        waypoint.setVisible(visible.selected());
        PacketDistributor.sendToServer(new UpdateWaypointPayload(waypoint));
    }

    private int parseCoordinate(EditBox editBox) {
        if (editBox.getValue().isBlank()) {
            return 0;
        }
        try {
            return Integer.parseInt(editBox.getValue());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

}
