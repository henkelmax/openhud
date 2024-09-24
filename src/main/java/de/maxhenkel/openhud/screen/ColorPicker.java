package de.maxhenkel.openhud.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;

import java.awt.*;
import java.util.function.Consumer;

public class ColorPicker extends AbstractWidget {

    protected Consumer<Integer> onColorChange;

    public ColorPicker(int x, int y, int width, int height, Consumer<Integer> onColorChange) {
        super(x, y, width, height, Component.empty());
        this.onColorChange = onColorChange;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), 0xFFFFFFFF);

        int startX = getX() + 1;
        int width = getWidth() - 2;

        for (int i = 0; i < width; i++) {
            int color = getColor((float) i / (float) width);
            guiGraphics.fill(startX + i, getY() + 1, startX + i + 1, getY() + getHeight() - 1, color);
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return updateColor(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean success = updateColor(mouseX, mouseY, button);
        if (success) {
            playDownSound(Minecraft.getInstance().getSoundManager());
        }
        return success;
    }

    private boolean updateColor(double mouseX, double mouseY, int button) {
        if (!isValidClickButton(button)) {
            return false;
        }

        if (!isMouseOver(mouseX, mouseY)) {
            return false;
        }

        double value = Math.max(Math.min((mouseX - getX() + 1) / (getWidth() - 2), 1D), 0D);

        onColorChange.accept(getColor((float) value));
        return true;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narration) {

    }

    public static int getColor(float value) {
        Color hsbColor = Color.getHSBColor(value, 1F, 1F);
        return FastColor.ARGB32.color(hsbColor.getRed(), hsbColor.getGreen(), hsbColor.getBlue());
    }
}
