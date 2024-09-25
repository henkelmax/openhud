package de.maxhenkel.openhud.texture;

import de.maxhenkel.openhud.Main;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import javax.annotation.Nullable;

public class WaypointIcons {

    private static final ResourceLocation DEFAULT_MAP_ICON = ResourceLocation.fromNamespaceAndPath(Main.MODID, "cross");
    private static final ResourceLocation MISSING_TEXTURE_LOCATION = ResourceLocation.withDefaultNamespace("missingno");
    private static WaypointIconTextureManager waypointIconTextureManager;
    @Nullable
    private static TextureAtlasSprite defaultMapIcon;

    public static void onRegisterStage(RenderLevelStageEvent.RegisterStageEvent event) {
        Minecraft instance = Minecraft.getInstance();
        waypointIconTextureManager = new WaypointIconTextureManager(instance.getTextureManager());
        if (instance.getResourceManager() instanceof ReloadableResourceManager resourceManager) {
            resourceManager.registerReloadListener(waypointIconTextureManager);
        }
    }

    public static TextureAtlasSprite getDefaultMapIcon() {
        if (defaultMapIcon == null) {
            defaultMapIcon = waypointIconTextureManager.getSprite(DEFAULT_MAP_ICON);
        }
        return defaultMapIcon;
    }

    public static TextureAtlasSprite get(ResourceLocation location) {
        TextureAtlasSprite sprite = waypointIconTextureManager.getSprite(location);
        if (MISSING_TEXTURE_LOCATION.equals(sprite.contents().name())) {
            return getDefaultMapIcon();
        }
        return sprite;
    }

}
