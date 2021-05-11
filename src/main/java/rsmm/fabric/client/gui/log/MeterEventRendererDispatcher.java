package rsmm.fabric.client.gui.log;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.font.TextRenderer;

import rsmm.fabric.common.Meter;
import rsmm.fabric.common.event.EventType;

public class MeterEventRendererDispatcher {
	
	private final List<MeterEventRenderer> eventRenderers;
	
	public MeterEventRendererDispatcher() {
		eventRenderers = new LinkedList<>();
		
		eventRenderers.add(new PoweredEventRenderer());
		eventRenderers.add(new ActiveEventRenderer());
		eventRenderers.add(new MovedEventRenderer());
	}
	
	public void renderTickLogs(TextRenderer font, int x, int y, long firstTick, long lastTick, Meter meter) {
		for (MeterEventRenderer eventRenderer : eventRenderers) {
			EventType type = eventRenderer.getType();
			
			if (meter.isMetering(type)) {
				eventRenderer.renderTickLogs(font, x, y, firstTick, lastTick, meter);
			}
		}
	}
	
	public void renderSubTickLogs(TextRenderer font, int x, int y, long tick, int subTickCount, Meter meter) {
		for (MeterEventRenderer eventRenderer : eventRenderers) {
			EventType type = eventRenderer.getType();
			
			if (meter.isMetering(type)) {
				eventRenderer.renderSubTickLogs(font, x, y, tick, subTickCount, meter);
			}
		}
	}
}
