package redstone.multimeter.client.gui.screen;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import redstone.multimeter.client.gui.CursorType;

public class ScreenWrapper extends Screen {
	
	private final Screen parent;
	private final RSMMScreen screen;
	
	public ScreenWrapper(Screen parent, RSMMScreen screen) {
		super(screen.getTitle());
		
		this.parent = parent;
		this.screen = screen;
		
		this.screen.wrapper = this;
	}
	
	@Override
	public Text getTitle() {
		return screen.getTitle();
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		screen.render(matrices, mouseX, mouseY);
	}
	
	@Override
	public final void mouseMoved(double mouseX, double mouseY) {
		screen.mouseMove(mouseX, mouseY);
	}
	
	@Override
	public final boolean mouseClicked(double mouseX, double mouseY, int button) {
		return screen.mouseClick(mouseX, mouseY, button);
	}
	
	@Override
	public final boolean mouseReleased(double mouseX, double mouseY, int button) {
		return screen.mouseRelease(mouseX, mouseY, button);
	}
	
	@Override
	public final boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		return screen.mouseDrag(mouseX, mouseY, button, deltaX, deltaY);
	}
	
	@Override
	public final boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		return false; // scrolling is handled in MouseMixin and InputHandler
	}
	
	@Override
	public final boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (screen.keyPress(keyCode, scanCode, modifiers)) {
			return true;
		}
		if (screen.shouldCloseOnEsc() && keyCode == GLFW.GLFW_KEY_ESCAPE) {
			screen.close();
			return true;
		}
		
		return false;
	}
	
	@Override
	public final boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		return screen.keyRelease(keyCode, scanCode, modifiers);
	}
	
	@Override
	public final boolean charTyped(char chr, int modifiers) {
		return screen.typeChar(chr, modifiers);
	}
	
	@Override
	public boolean shouldCloseOnEsc() {
		return screen.shouldCloseOnEsc();
	}
	
	@Override
	protected final void init() {
		screen.setWidth(width);
		screen.setHeight(height);
		
		screen.removeChildren();
		screen.initScreen();
		screen.update();
	}
	
	@Override
	public void tick() {
		screen.tick();
	}
	
	@Override
	public void removed() {
		screen.onRemoved();
		screen.setCursor(client, CursorType.ARROW);
	}
	
	public Screen getParent() {
		return parent;
	}
	
	public RSMMScreen getScreen() {
		return screen;
	}
}
