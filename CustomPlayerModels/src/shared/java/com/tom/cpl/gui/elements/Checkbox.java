package com.tom.cpl.gui.elements;

import com.tom.cpl.gui.IGui;
import com.tom.cpl.gui.MouseEvent;

public class Checkbox extends GuiElement {
	private String name;
	protected Runnable action;
	private boolean selected;
	private Tooltip tooltip;
	public Checkbox(IGui gui, String name) {
		super(gui);
		this.name = name;
	}

	@Override
	public void draw(MouseEvent event, float partialTicks) {
		gui.drawCheckbox(this, event, partialTicks);
	}

	@Override
	public boolean mouseClick(int x, int y, int btn) {
		if(enabled && bounds.isInBounds(x, y)) {
			if(action != null)action.run();
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

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setTooltip(Tooltip tooltip) {
		this.tooltip = tooltip;
	}

	public Tooltip getTooltip() {
		return this.tooltip;
	}

	public void updateState(Boolean b) {
		setEnabled(b != null);
		if(b != null)setSelected(b);
		else setSelected(false);
	}
}
