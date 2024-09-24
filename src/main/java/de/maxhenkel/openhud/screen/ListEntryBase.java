package de.maxhenkel.openhud.screen;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;

import java.util.List;

public abstract class ListEntryBase<T extends ContainerObjectSelectionList.Entry<T>> extends ContainerObjectSelectionList.Entry<T> {

    protected final List<AbstractWidget> children;

    public ListEntryBase() {
        this.children = Lists.newArrayList();
    }

    @Override
    public List<? extends NarratableEntry> narratables() {
        return children;
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return children;
    }

}
