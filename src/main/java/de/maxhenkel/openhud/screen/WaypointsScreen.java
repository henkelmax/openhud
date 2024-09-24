package de.maxhenkel.openhud.screen;

import de.maxhenkel.openhud.net.UpdateWaypointPayload;
import de.maxhenkel.openhud.waypoints.Waypoint;
import de.maxhenkel.openhud.waypoints.WaypointClientManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.text.NumberFormat;

public class WaypointsScreen extends Screen {

    private static final Component TITLE = Component.translatable("gui.openhud.waypoints.title");
    private static final Component BACK = Component.translatable("message.openhud.back");
    private static final Component EDIT = Component.translatable("message.openhud.edit");
    private static final int HEADER_SIZE = 30;
    private static final int FOOTER_SIZE = 50;
    private static final int CELL_HEIGHT = 40;
    private static final int PADDING = 5;
    private static final int COLOR_SIZE = 20;

    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance();

    @Nullable
    protected final Screen parent;
    protected WaypointList waypointList;
    protected Button backButton;

    public WaypointsScreen(@Nullable Screen parent) {
        super(TITLE);
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        if (waypointList != null) {
            waypointList.updateSizeAndPosition(width, height - HEADER_SIZE - FOOTER_SIZE, HEADER_SIZE);
        } else {
            waypointList = new WaypointList(width, height - HEADER_SIZE - FOOTER_SIZE, HEADER_SIZE, CELL_HEIGHT);
        }
        addRenderableWidget(waypointList);

        backButton = addRenderableWidget(Button.builder(BACK, button -> {
            back();
        }).bounds(width / 2 - 100, height - FOOTER_SIZE / 2 - 10, 200, 20).build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int x, int y, float delta) {
        super.render(guiGraphics, x, y, delta);
        guiGraphics.drawString(font, TITLE, width / 2 - font.width(TITLE) / 2, HEADER_SIZE / 2 - font.lineHeight / 2, 0xFFFFFF, true);
    }

    @Override
    public void onClose() {
        super.onClose();
        back();
    }

    private void back() {
        minecraft.setScreen(parent);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button) || waypointList.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return super.mouseReleased(mouseX, mouseY, button) || waypointList.mouseReleased(mouseX, mouseY, button);
    }

    public static void sendWaypointUpdate(Waypoint waypoint) {
        PacketDistributor.sendToServer(new UpdateWaypointPayload(waypoint));
    }

    public void update() {
        waypointList.updateEntries();
    }

    private class WaypointList extends ListBase<WaypointList.Entry> {

        public WaypointList(int width, int height, int y, int itemHeight) {
            super(width, height, y, itemHeight);
            updateEntries();
        }

        public void updateEntries() {
            clearEntries();
            setSelected(null);

            for (Waypoint waypoint : WaypointClientManager.getWaypoints().getWaypoints()) {
                addEntry(new Entry(waypoint));
            }
            setScrollAmount(0D);
        }

        @Override
        public int getRowWidth() {
            return super.getRowWidth() + 50;
        }

        @Override
        protected int getScrollbarPosition() {
            return WaypointsScreen.this.width - 6;
        }

        private class Entry extends ListEntryBase<Entry> {

            private final Waypoint waypoint;
            private final Checkbox visible;
            private final Button edit;

            public Entry(Waypoint waypoint) {
                this.waypoint = waypoint;
                visible = Checkbox.builder(Component.empty(), minecraft.font)
                        .pos(0, 0)
                        .onValueChange((checkbox, selected) -> {
                            waypoint.setVisible(selected);
                            sendWaypointUpdate(waypoint);
                        })
                        .selected(waypoint.isVisible())
                        .build();
                children.add(visible);

                edit = Button.builder(EDIT, button -> {
                    minecraft.setScreen(new WaypointScreen(WaypointsScreen.this, waypoint));
                    //TODO Open edit waypoint screen
                }).size(40, 20).build();
                children.add(edit);
            }

            @Override
            public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float delta) {
                guiGraphics.fill(left + PADDING, top + height / 2 - COLOR_SIZE / 2, left + PADDING + COLOR_SIZE, top + height / 2 + COLOR_SIZE / 2, 0);
                guiGraphics.fill(left + PADDING + 1, top + height / 2 - COLOR_SIZE / 2 + 1, left + PADDING + COLOR_SIZE - 1, top + height / 2 + COLOR_SIZE / 2 - 1, waypoint.getColor());

                int posY = top + 3;
                int colorEnd = left + PADDING + COLOR_SIZE;
                int buttonStart = left + width - visible.getWidth() - PADDING - 40 - PADDING;
                int textSpace = colorEnd - buttonStart;

                guiGraphics.drawString(font, waypoint.getName(), buttonStart + textSpace / 2 - WaypointsScreen.this.font.width(waypoint.getName()) / 2, posY, 0xFFFFFFFF, true);
                posY += font.lineHeight + 3;

                Component coords = Component.translatable("message.openhud.coordinates", waypoint.getPosition().getX(), waypoint.getPosition().getY(), waypoint.getPosition().getZ());
                guiGraphics.drawString(font, coords, buttonStart + textSpace / 2 - WaypointsScreen.this.font.width(coords) / 2, posY, 0xFFFFFFFF, true);
                posY += font.lineHeight + 3;

                int distanceInBlocks = (int) minecraft.gameRenderer.getMainCamera().getPosition().distanceTo(waypoint.getPosition().getCenter());
                Component distance = Component.translatable("message.openhud.distance", NUMBER_FORMAT.format(distanceInBlocks));
                guiGraphics.drawString(font, distance, buttonStart + textSpace / 2 - WaypointsScreen.this.font.width(distance) / 2, posY, 0xFFFFFFFF, true);

                visible.setPosition(left + width - visible.getWidth() - PADDING, top + height / 2 - visible.getHeight() / 2);
                visible.render(guiGraphics, mouseX, mouseY, delta);

                edit.setPosition(buttonStart, top + height / 2 - 10);
                edit.render(guiGraphics, mouseX, mouseY, delta);
            }
        }

    }

}
