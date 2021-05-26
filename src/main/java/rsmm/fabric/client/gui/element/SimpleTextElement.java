package rsmm.fabric.client.gui.element;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.text.Text;

import rsmm.fabric.client.MultimeterClient;
import rsmm.fabric.client.gui.action.MousePress;
import rsmm.fabric.client.gui.action.MouseRelease;

public class SimpleTextElement extends TextElement {
	
	public SimpleTextElement(MultimeterClient client, int x, int y, Supplier<Text> textSupplier) {
		this(client, x, y, textSupplier, () -> Collections.emptyList());
	}
	
	public SimpleTextElement(MultimeterClient client, int x, int y, Supplier<Text> textSupplier, Supplier<List<Text>> tooltipSupplier) {
		this(client, x, y, textSupplier, tooltipSupplier, t -> false, t -> false);
	}
	
	public SimpleTextElement(MultimeterClient client, int x, int y, Supplier<Text> textSupplier, Supplier<List<Text>> tooltipSupplier, MousePress<TextElement> mousePress, MouseRelease<TextElement> mouseRelease) {
		super(client, x, y, () -> Arrays.asList(textSupplier.get()), tooltipSupplier, mousePress, mouseRelease);
	}
}
