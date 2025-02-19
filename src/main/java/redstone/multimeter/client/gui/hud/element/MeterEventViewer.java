package redstone.multimeter.client.gui.hud.element;

import net.minecraft.client.util.math.MatrixStack;

import redstone.multimeter.client.gui.element.AbstractElement;
import redstone.multimeter.client.gui.hud.Directionality;
import redstone.multimeter.client.gui.hud.MultimeterHud;
import redstone.multimeter.common.meter.Meter;

public abstract class MeterEventViewer extends AbstractElement {
	
	protected final MultimeterHud hud;
	
	protected MeterEventViewer(MultimeterHud hud) {
		super(0, 0, 0, 0);
		
		this.hud = hud;
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY) {
		matrices.push();
		drawHighlights(matrices, mouseX, mouseY);
		matrices.translate(0, 0, -1);
		drawDecorators(matrices);
		matrices.translate(0, 0, -1);
		drawMeterEvents(matrices);
		matrices.translate(0, 0, -1);
		drawGridLines(matrices);
		matrices.translate(0, 0, -1);
		hud.renderer.renderRect(matrices, 0, 0, getWidth(), getHeight(), hud.settings.colorBackground);
		matrices.pop();
	}
	
	@Override
	public void mouseMove(double mouseX, double mouseY) {
		
	}
	
	@Override
	public boolean mouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		return false;
	}
	
	@Override
	public boolean mouseScroll(double mouseX, double mouseY, double scrollX, double scrollY) {
		return false;
	}
	
	@Override
	public boolean keyPress(int keyCode, int scanCode, int modifiers) {
		return false;
	}
	
	@Override
	public boolean keyRelease(int keyCode, int scanCode, int modifiers) {
		return false;
	}
	
	@Override
	public boolean typeChar(char chr, int modifiers) {
		return false;
	}
	
	@Override
	public void onRemoved() {
		
	}
	
	@Override
	public void tick() {
		
	}
	
	@Override
	public void update() {
		
	}
	
	protected void drawMeterLogs(MeterEventRenderEvent event) {
		int x = 0;
		int y = 0;
		
		for (int index = 0; index < hud.meters.size(); index++) {
			Meter meter = hud.meters.get(index);
			event.accept(x, y, meter);
			
			y += hud.settings.rowHeight + hud.settings.gridSize;
		}
	}
	
	protected abstract void drawHighlights(MatrixStack matrices, int mouseX, int mouseY);
	
	protected void drawHighlight(MatrixStack matrices, int column, int columnCount, int row, int rowCount, boolean selection) {
		int w = hud.settings.columnWidth + hud.settings.gridSize;
		int h = hud.settings.rowHeight + hud.settings.gridSize;
		int x = column * w;
		int y = row * h;
		int width = columnCount * w;
		int height = rowCount * h;
		
		hud.renderer.renderHighlight(matrices, x, y, width, height, selection);
	}
	
	protected abstract void drawDecorators(MatrixStack matrices);
	
	protected abstract void drawMeterEvents(MatrixStack matrices);
	
	private void drawGridLines(MatrixStack matrices) {
		matrices.push();
		
		int columns = getColumnCount();
		int rows = hud.meters.size();
		int marker = getMarkerColumn();
		
		int lineX;
		int lineY;
		int lineWidth;
		int lineHeight;
		int color;
		
		// marker
		if (marker >= 0 && marker <= columns) {
			lineX = marker * (hud.settings.columnWidth + hud.settings.gridSize);
			lineY = hud.settings.gridSize;
			lineWidth = hud.settings.gridSize;
			lineHeight = getHeight() - 2 * hud.settings.gridSize;
			color = hud.settings.colorGridMarker;
			
			hud.renderer.renderRect(matrices, lineX, lineY, lineWidth, lineHeight, color);
		}
		
		matrices.translate(0, 0, -0.1);
		
		// horizonal lines
		for (int i = 0; i <= rows; i++) {
			lineX = 0;
			lineY = i * (hud.settings.rowHeight + hud.settings.gridSize);
			lineWidth = getWidth();
			lineHeight = hud.settings.gridSize;
			color = hud.settings.colorGridMain;
			
			hud.renderer.renderRect(matrices, lineX, lineY, lineWidth, lineHeight, color);
		}
		
		matrices.translate(0, 0, -0.1);
		
		// vertical lines
		for (int i = 0; i <= columns; i++) {
			lineX = i * (hud.settings.columnWidth + hud.settings.gridSize);
			lineY = 0;
			lineWidth = hud.settings.gridSize;
			lineHeight = getHeight();
			color = (i > 0 && i < columns && i % 5 == 0) ? hud.settings.colorGridInterval : hud.settings.colorGridMain;
			
			hud.renderer.renderRect(matrices, lineX, lineY, lineWidth, lineHeight, color);
		}
		
		matrices.pop();
	}
	
	protected abstract int getColumnCount();
	
	protected int getMarkerColumn() {
		return -1;
	}
	
	public int getHoveredColumn(double mouseX) {
		int max = getColumnCount() - 1;
		int column = Math.min(max, (int)((mouseX - getX()) / (hud.settings.columnWidth + hud.settings.gridSize)));
		
		if (hud.getDirectionalityX() == Directionality.X.RIGHT_TO_LEFT) {
			column = max - column;
		}
		
		return column;
	}
	
	public void updateWidth() {
		int columns = getColumnCount();
		
		if (columns == 0) {
			setWidth(0);
		} else {
			setWidth(columns * (hud.settings.columnWidth + hud.settings.gridSize) + hud.settings.gridSize);
		}
	}
	
	public void updateHeight() {
		setHeight(hud.meters.size() * (hud.settings.rowHeight + hud.settings.gridSize) + hud.settings.gridSize);
	}
	
	protected interface MeterEventRenderEvent {
		
		public void accept(int x, int y, Meter meter);
		
	}
}
