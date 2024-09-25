package de.maxhenkel.openhud.screen;

import de.maxhenkel.openhud.utils.GraphicsUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public class WaypointIconDisplay extends AbstractWidget {

    protected int color;
    @Nullable
    protected ResourceLocation icon;

    public WaypointIconDisplay(int x, int y, int width, int height, int color, @Nullable ResourceLocation icon) {
        super(x, y, width, height, Component.empty());
        this.color = color;
        this.icon = icon;
    }

    public void setColor(int color) {
        this.color = color;
        this.icon = null;
    }

    public int getColor() {
        return color;
    }

    public void setIcon(@Nullable ResourceLocation icon) {
        this.icon = icon;
    }

    @Nullable
    public ResourceLocation getIcon() {
        return icon;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (icon == null) {
            guiGraphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), 0xFFFFFFFF);
            guiGraphics.fill(getX() + 1, getY() + 1, getX() + getWidth() - 1, getY() + getHeight() - 1, color);
            return;
        }

        GraphicsUtils.blit(guiGraphics, icon, getX() + 1, getX() + getHeight() - 1, getY() + 1, getY() + getWidth() - 1, 0F, 1F, 0F, 1F);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
