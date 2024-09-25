package de.maxhenkel.openhud.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class GraphicsUtils {

    public static void blitColored(GuiGraphics guiGraphics, ResourceLocation atlasLocation, float x1, float x2, float y1, float y2, float minU, float maxU, float minV, float maxV, int color) {
        RenderSystem.setShaderTexture(0, atlasLocation);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        Matrix4f matrix4f = guiGraphics.pose().last().pose();
        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.addVertex(matrix4f, x1, y1, 0F).setUv(minU, minV).setColor(color);
        bufferbuilder.addVertex(matrix4f, x1, y2, 0F).setUv(minU, maxV).setColor(color);
        bufferbuilder.addVertex(matrix4f, x2, y2, 0F).setUv(maxU, maxV).setColor(color);
        bufferbuilder.addVertex(matrix4f, x2, y1, 0F).setUv(maxU, minV).setColor(color);
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
    }

    public static void blit(GuiGraphics guiGraphics, ResourceLocation atlasLocation, float x1, float x2, float y1, float y2, float minU, float maxU, float minV, float maxV) {
        RenderSystem.setShaderTexture(0, atlasLocation);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Matrix4f matrix4f = guiGraphics.pose().last().pose();
        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.addVertex(matrix4f, x1, y1, 0F).setUv(minU, minV);
        bufferbuilder.addVertex(matrix4f, x1, y2, 0F).setUv(minU, maxV);
        bufferbuilder.addVertex(matrix4f, x2, y2, 0F).setUv(maxU, maxV);
        bufferbuilder.addVertex(matrix4f, x2, y1, 0F).setUv(maxU, minV);
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
    }

    public static void blit(GuiGraphics guiGraphics, TextureAtlasSprite sprite, float x1, float x2, float y1, float y2, float minU, float maxU, float minV, float maxV) {
        float spriteXSize = sprite.getU1() - sprite.getU0();
        float spriteMinU = sprite.getU0() + spriteXSize * minU;
        float spriteMaxU = sprite.getU0() + spriteXSize * maxU;
        float spriteYSize = sprite.getV1() - sprite.getV0();
        float spriteMinV = sprite.getV0() + spriteYSize * minV;
        float spriteMaxV = sprite.getV0() + spriteYSize * maxV;
        //Prevent texture bleeding
        spriteMinU = Math.max(0F, Math.min(1F, spriteMinU + 0.0001F));
        spriteMaxU = Math.max(0F, Math.min(1F, spriteMaxU - 0.0001F));
        spriteMinV = Math.max(0F, Math.min(1F, spriteMinV + 0.0001F));
        spriteMaxV = Math.max(0F, Math.min(1F, spriteMaxV - 0.0001F));
        blit(guiGraphics, sprite.atlasLocation(), x1, x2, y1, y2, spriteMinU, spriteMaxU, spriteMinV, spriteMaxV);
    }

    public static void fill(GuiGraphics guiGraphics, float minX, float minY, float maxX, float maxY, int color) {
        Matrix4f matrix4f = guiGraphics.pose().last().pose();
        if (minX < maxX) {
            float i = minX;
            minX = maxX;
            maxX = i;
        }

        if (minY < maxY) {
            float j = minY;
            minY = maxY;
            maxY = j;
        }

        VertexConsumer vertexconsumer = guiGraphics.bufferSource().getBuffer(RenderType.gui());
        vertexconsumer.addVertex(matrix4f, minX, minY, 0F).setColor(color);
        vertexconsumer.addVertex(matrix4f, minX, maxY, 0F).setColor(color);
        vertexconsumer.addVertex(matrix4f, maxX, maxY, 0F).setColor(color);
        vertexconsumer.addVertex(matrix4f, maxX, minY, 0F).setColor(color);
        guiGraphics.flush();
    }

}
