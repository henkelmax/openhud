package de.maxhenkel.openhud.screen;

import de.maxhenkel.openhud.net.DeleteWaypointPayload;
import de.maxhenkel.openhud.waypoints.Waypoint;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;

public class DeleteWaypointScreen extends Screen {

    private static final Component TITLE = Component.translatable("message.openhud.delete_waypoint.title");
    private static final Component CONFIRM = Component.translatable("message.openhud.delete_waypoint.confirm");
    private static final Component CANCEL = Component.translatable("message.openhud.delete_waypoint.cancel");

    @Nullable
    protected Screen parent;
    protected Waypoint waypoint;

    public DeleteWaypointScreen(@Nullable Screen parent, Waypoint waypoint) {
        super(TITLE);
        this.parent = parent;
        this.waypoint = waypoint;
    }

    @Override
    protected void init() {
        super.init();
        LinearLayout contentLayout = LinearLayout.vertical().spacing(5);

        contentLayout.addChild(new StringWidget(Component.translatable("message.openhud.delete_waypoint", waypoint.getName().copy().withStyle(ChatFormatting.UNDERLINE)), font), LayoutSettings.defaults().alignHorizontallyCenter());

        contentLayout.addChild(new SpacerElement(200, 20));

        LinearLayout linearlayout = LinearLayout.horizontal().spacing(4);
        linearlayout.addChild(Button.builder(CONFIRM, b -> {
            PacketDistributor.sendToServer(new DeleteWaypointPayload(waypoint.getId()));
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
        guiGraphics.drawCenteredString(font, TITLE, width / 2, 15, 0xFFFFFFFF);
    }

    @Override
    public void onClose() {
        super.onClose();
        if (parent instanceof UpdatableScreen updatableScreen) {
            updatableScreen.update();
        }
        minecraft.setScreen(parent);
    }

}
