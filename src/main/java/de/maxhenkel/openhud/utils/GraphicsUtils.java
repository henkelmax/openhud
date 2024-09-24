package de.maxhenkel.openhud.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
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
