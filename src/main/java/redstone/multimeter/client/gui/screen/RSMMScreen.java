package redstone.multimeter.client.gui.screen;

import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import redstone.multimeter.client.MultimeterClient;
import redstone.multimeter.client.gui.Texture;
import redstone.multimeter.client.gui.Tooltip;
import redstone.multimeter.client.gui.element.AbstractParentElement;

public abstract class RSMMScreen extends AbstractParentElement {
	
	protected final MultimeterClient client;
	protected final MinecraftClient minecraftClient;
	protected final TextRenderer font;
	
	private final Text title;
	private final boolean drawTitle;
	
	protected ScreenWrapper wrapper;
	
	protected RSMMScreen(MultimeterClient client, Text title, boolean drawTitle) {
		this.client = client;
		this.minecraftClient = client.getMinecraftClient();
		this.font = this.minecraftClient.textRenderer;
		
		this.title = title;
		this.drawTitle = drawTitle;
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY) {
		renderBackground(matrices);
		renderContent(matrices, mouseX, mouseY);
		
		if (drawTitle) {
			int width = font.getWidth(title);
			int x = getX() + (getWidth() - width) / 2;
			int y = getY() + 6;
			
			renderText(font, matrices, title, x, y, true, 0xFFFFFFFF);
		}
		
		Tooltip tooltip = getTooltip(mouseX, mouseY);
		
		if (!tooltip.isEmpty()) {
			drawTooltip(matrices, tooltip, mouseX, mouseY);
		}
	}
	
	@Override
	public boolean mouseClick(double mouseX, double mouseY, int button) {
		return super.mouseClick(mouseX, mouseY, button) || client.getInputHandler().mouseClick(this, mouseX, mouseY, button);
	}
	
	@Override
	public boolean keyPress(int keyCode, int scanCode, int modifiers) {
		return super.keyPress(keyCode, scanCode, modifiers) || client.getInputHandler().keyPress(this, keyCode, scanCode, modifiers);
	}
	
	@Override
	public boolean mouseScroll(double mouseX, double mouseY, double scrollX, double scrollY) {
		return super.mouseScroll(mouseX, mouseY, scrollX, scrollY) || client.getInputHandler().mouseScroll(this, scrollX, scrollY);
	}
	
	@Override
	public final void setX(int x) {
		
	}
	
	@Override
	public final void setY(int y) {
		
	}
	
	@Override
	public void setWidth(int width) {
		super.setWidth(width);
	}
	
	@Override
	public void setHeight(int height) {
		super.setHeight(height);
	}
	
	@Override
	protected final void onChangedX(int x) {
		
	}
	
	@Override
	protected final void onChangedY(int y) {
		
	}
	
	protected abstract void initScreen();
	
	protected boolean shouldCloseOnEsc() {
		return true;
	}
	
	public void close() {
		minecraftClient.setScreen(wrapper.getParent());
	}
	
	protected void renderBackground(MatrixStack matrices) {
		if (hasTransparentBackground()) {
			renderGradient(matrices, getX(), getY(), getWidth(), getHeight(), 0xC0101010, 0xD0101010);
		} else {
			renderBackgroundTexture(matrices);
		}
	}
	
	protected boolean hasTransparentBackground() {
		return minecraftClient.world != null;
	}
	
	protected void renderBackgroundTexture(MatrixStack matrices) {
		int x0 = getX();
		int y0 = getY();
		int x1 = x0 + getWidth();
		int y1 = y0 + getHeight();
		
		int tx0 = x0 / 2;
		int ty0 = y0 / 2;
		int tx1 = x1 / 2;
		int ty1 = y1 / 2;
		
		renderTextureColor(matrices, Texture.OPTIONS_BACKGROUND, x0, y0, x1, y1, tx0, ty0, tx1, ty1, 0xFF, 0x40, 0x40, 0x40);
	}
	
	protected void renderContent(MatrixStack matrices, int mouseX, int mouseY) {
		super.render(matrices, mouseX, mouseY);
	}
	
	protected void drawTooltip(MatrixStack matrices, Tooltip tooltip, int mouseX, int mouseY) {
		List<Text> lines = tooltip.getLines();
		
		int lineHeight = font.fontHeight;
		int lineSpacing = 1;
		
		int width = 0;
		int height = (lines.size() - 1) * (lineHeight + lineSpacing) + lineHeight;
		
		for (int index = 0; index < lines.size(); index++) {
			Text text = lines.get(index);
			int lineWidth = font.getWidth(text);
			
			if (lineWidth > width) {
				width = lineWidth;
			}
		}
		
		width += 8;
		height += 8;
		
		int x = mouseX + 15;
		int y = mouseY;
		
		if (x + width > getX() + getWidth()) {
			x = mouseX - 15 - width;
		}
		if (y + height > getY() + getHeight()) {
			y = mouseY - height;
		}
		
		drawTooltip(matrices, lines, x, y, width, height);
	}
	
	private void drawTooltip(MatrixStack matrices, List<Text> lines, int x, int y, int width, int height) {
		int backgroundColor = 0xF0100010;
		int borderColor0    = 0x505000FF;
		int borderColor1    = 0x5028007F;
		
		matrices.push();
		matrices.translate(0, 0, 400);
		
		renderRect(matrices, (bufferBuilder, model) -> {
			// background
			drawRect(bufferBuilder, model, x    , y + 1         , width    , height - 2, backgroundColor); // center, left/right outer borders
			drawRect(bufferBuilder, model, x + 1, y             , width - 2, 1         , backgroundColor); // top outer border
			drawRect(bufferBuilder, model, x + 1, y + height - 1, width - 2, 1         , backgroundColor); // bottom outer border
			
			// inner border
			drawGradient(bufferBuilder, model, x + 1        , y + 2         , 1        , height - 4, borderColor0, borderColor1); // left
			drawRect    (bufferBuilder, model, x + 1        , y + height - 2, width - 2, 1         , borderColor1);               // bottom
			drawGradient(bufferBuilder, model, x + width - 2, y + 2         , 1        , height - 4, borderColor0, borderColor1); // right
			drawRect    (bufferBuilder, model, x + 1        , y + 1         , width - 2, 1         , borderColor0);               // top
		});
		
		renderText(matrices, (immediate, model) -> {
			int textX = x + 4;
			int textY = y + 4;
			
			for (int index = 0; index < lines.size(); index++) {
				Text line = lines.get(index);
				drawText(immediate, model, font, line, textX, textY, true);
				
				textY += font.fontHeight + 1;
			}
		});
		
		matrices.pop();
	}
	
	public Text getTitle() {
		return title;
	}
	
	public boolean isPauseScreen() {
		return true;
	}
	
	public static boolean isControlPressed() {
		return Screen.hasControlDown() && !Screen.hasShiftDown() && !Screen.hasAltDown();
	}
}
