package rsmm.fabric.client;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import rsmm.fabric.common.Meter;
import rsmm.fabric.common.MeterGroup;
import rsmm.fabric.util.ColorUtils;

public class MeterRenderer {
	
	private final MultimeterClient multimeterClient;
	private final MinecraftClient minecraftClient;
	
	public MeterRenderer(MultimeterClient multimeterClient) {
		this.multimeterClient = multimeterClient;
		this.minecraftClient = this.multimeterClient.getMinecraftClient();
	}
	
	public void renderMeters() {
		MeterGroup meterGroup = multimeterClient.getMeterGroup();
		
		if (meterGroup == null) {
			return;
		}
		
		GlStateManager.enableBlend();
		GlStateManager.disableTexture();
		GlStateManager.blendFuncSeparate(
			GlStateManager.SourceFactor.SRC_ALPHA,
			GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
			GlStateManager.SourceFactor.ONE,
			GlStateManager.DestFactor.ZERO
		);
		GlStateManager.depthMask(false);
		GlStateManager.disableLighting();
		
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder builder = tessellator.getBuffer();
		
		for (Meter meter : meterGroup.getMeters()) {
			if (meter.isIn(minecraftClient.world)) {
				drawMeter(builder, tessellator, meter);
			}
		}
		
		GlStateManager.enableLighting();
		GlStateManager.depthMask(true);
		GlStateManager.enableTexture();
		GlStateManager.disableBlend();
	}
	
	private void drawMeter(BufferBuilder builder, Tessellator tessellator, Meter meter) {
		BlockPos pos = meter.getPos().asBlockPos();
		int color = meter.getColor();
		boolean movable = meter.isMovable();
		
		Camera camera = minecraftClient.gameRenderer.getCamera();
		Vec3d cameraPos = camera.getPos();
		
		GlStateManager.pushMatrix();
		GlStateManager.translated(pos.getX() - cameraPos.x, pos.getY() - cameraPos.y, pos.getZ() - cameraPos.z);
		
		float r = ColorUtils.getRed(color) / 255.0F;
		float g = ColorUtils.getGreen(color) / 255.0F;
		float b = ColorUtils.getBlue(color) / 255.0F;
		
		drawFilledBox(builder, tessellator, r, g, b, 0.5F);
		
		if (movable) {
			drawBoxOutline(builder, tessellator, r, g, b, 1.0F);
		}
		
		GlStateManager.popMatrix();
	}
	
	private void drawFilledBox(BufferBuilder builder, Tessellator tessellator, float r, float g, float b, float a) {
		builder.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR);
		drawBox(builder, r, g, b, a, false);
		tessellator.draw();
	}
	
	private void drawBoxOutline(BufferBuilder builder, Tessellator tessellator, float r, float g, float b, float a) {
		builder.begin(GL11.GL_LINES, VertexFormats.POSITION_COLOR);
		drawBox(builder, r, g, b, a, true);
		tessellator.draw();
	}
	
	private void drawBox(BufferBuilder builder, float r, float g, float b, float a, boolean outline) {
		// The box is slightly larger than 1x1 to prevent z-fighting
		float c0 = -0.002F;
		float c1 = 1.002F;
		
		// Back Face
		builder.vertex(c0, c0, c0).color(r, g, b, a).next();
		builder.vertex(c0, c0, c1).color(r, g, b, a).next();
		builder.vertex(c0, c1, c1).color(r, g, b, a).next();
		builder.vertex(c0, c1, c0).color(r, g, b, a).next();
		if (outline) {
			builder.vertex(c0, c0, c0).color(r, g, b, a).next();
		}
		
		// Front Face
		builder.vertex(c1, c0, c0).color(r, g, b, a).next();
		builder.vertex(c1, c1, c0).color(r, g, b, a).next();
		builder.vertex(c1, c1, c1).color(r, g, b, a).next();
		builder.vertex(c1, c0, c1).color(r, g, b, a).next();
		if (outline) {
			builder.vertex(c1, c0, c0).color(r, g, b, a).next();
		}
		
		// Right Face
		builder.vertex(c0, c0, c0).color(r, g, b, a).next();
		builder.vertex(c0, c1, c0).color(r, g, b, a).next();
		builder.vertex(c1, c1, c0).color(r, g, b, a).next();
		builder.vertex(c1, c0, c0).color(r, g, b, a).next();
		if (outline) {
			builder.vertex(c0, c0, c0).color(r, g, b, a).next();
		}
		
		// Left Face
		builder.vertex(c0, c0, c1).color(r, g, b, a).next();
		builder.vertex(c1, c0, c1).color(r, g, b, a).next();
		builder.vertex(c1, c1, c1).color(r, g, b, a).next();
		builder.vertex(c0, c1, c1).color(r, g, b, a).next();
		if (outline) {
			builder.vertex(c0, c0, c1).color(r, g, b, a).next();
		}
		
		// Bottom Face
		builder.vertex(c0, c0, c0).color(r, g, b, a).next();
		builder.vertex(c1, c0, c0).color(r, g, b, a).next();
		builder.vertex(c1, c0, c1).color(r, g, b, a).next();
		builder.vertex(c0, c0, c1).color(r, g, b, a).next();
		if (outline) {
			builder.vertex(c0, c0, c0).color(r, g, b, a).next();
		}
		
		// Top Face
		builder.vertex(c0, c1, c0).color(r, g, b, a).next();
		builder.vertex(c0, c1, c1).color(r, g, b, a).next();
		builder.vertex(c1, c1, c1).color(r, g, b, a).next();
		builder.vertex(c1, c1, c0).color(r, g, b, a).next();
		if (outline) {
			builder.vertex(c0, c1, c0).color(r, g, b, a).next();
		}
	}
}
