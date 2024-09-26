package com.tom.cpl.gui.elements;

import com.tom.cpl.gui.IGui;
import com.tom.cpl.gui.MouseEvent;

public class Button extends GuiElement {
	protected String name;
	protected Runnable action;
	protected Tooltip tooltip;
	public Button(IGui gui, String name, Runnable action) {
		super(gui);
		this.name = name;
		this.action = action;
	}

	@Override
	public void draw(MouseEvent event, float partialTicks) {
		gui.drawButton(this, event, partialTicks);
	}

	@Override
	public boolean mouseClick(int x, int y, int btn) {
		if(enabled && bounds.isInBounds(x, y)) {
			if(action != null)
				action.run();
			return true;
		}
		return false;
	}

	public void setText(String name) {
		this.name = name;
	}

	public String getText() {
		return name;
	}

	public void setAction(Runnable action) {
		this.action = action;
	}

	public void setTooltip(Tooltip tooltip) {
		this.tooltip = tooltip;
	}

	public Tooltip getTooltip() {
		return this.tooltip;
	}

	public Runnable getAction() {
		return action;
	}
}
