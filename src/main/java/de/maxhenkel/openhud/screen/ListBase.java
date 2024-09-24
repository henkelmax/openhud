package de.maxhenkel.openhud.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;

public abstract class ListBase<T extends ListEntryBase<T>> extends ContainerObjectSelectionList<T> {

    public ListBase(int width, int height, int top, int itemSize) {
        super(Minecraft.getInstance(), width, height, top, itemSize);
    }

}
