package de.maxhenkel.openhud.texture;

import de.maxhenkel.openhud.Main;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.TextureAtlasHolder;
import net.minecraft.resources.ResourceLocation;

public class WaypointIconTextureManager extends TextureAtlasHolder {

    public static final ResourceLocation ATLAS = ResourceLocation.fromNamespaceAndPath(Main.MODID, "waypoint_icons");
    private static final ResourceLocation ATLAS_LOCATION = ResourceLocation.fromNamespaceAndPath(Main.MODID, "textures/atlas/waypoint_icons.png");

    public WaypointIconTextureManager(TextureManager textureManager) {
        super(textureManager, ATLAS_LOCATION, ATLAS);
    }

    @Override
    public TextureAtlasSprite getSprite(ResourceLocation location) {
        return super.getSprite(location);
    }
}
