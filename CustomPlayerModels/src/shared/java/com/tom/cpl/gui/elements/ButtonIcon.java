package com.tom.cpl.gui.elements;

import com.tom.cpl.gui.IGui;
import com.tom.cpl.gui.MouseEvent;

public class ButtonIcon extends Button {
	protected int u, v;
	protected boolean tintIcon;

	public ButtonIcon(IGui gui, String name, int u, int v, Runnable action) {
		super(gui, "", action);
		this.name = name;
		this.u = u;
		this.v = v;
	}

	public ButtonIcon(IGui gui, String name, int u, int v, boolean tintIcon, Runnable action) {
		super(gui, "", action);
		this.name = name;
		this.u = u;
		this.v = v;
		this.tintIcon = tintIcon;
	}

	@Override
	public void draw(MouseEvent event, float partialTicks) {
		gui.drawButton(this, event, partialTicks);
/*
		int bgColor = gui.getColors().button_fill;
		int color = gui.getColors().button_text_color;
		if(!enabled) {
			color = gui.getColors().button_text_disabled;
			bgColor = gui.getColors().button_disabled;
		} else if(event.isHovered(bounds)) {
			color = gui.getColors().button_text_hover;
			bgColor = gui.getColors().button_hover;
		}
		if(event.isHovered(bounds) && tooltip != null)
			tooltip.set();
		gui.drawBox(bounds.x, bounds.y, bounds.w, bounds.h, gui.getColors().button_border);
		gui.drawBox(bounds.x+1, bounds.y+1, bounds.w-2, bounds.h-2, bgColor);
		if(tintIcon)
			gui.drawTexture(bounds.x+2, bounds.y+2, bounds.w-4, bounds.h-4, u, v, name, color);
		else
			gui.drawTexture(bounds.x+2, bounds.y+2, bounds.w-4, bounds.h-4, u, v, name);*/
	}

	public void setU(int u) {
		this.u = u;
	}

	public void setV(int v) {
		this.v = v;
	}

	public int getU() {
		return u;
	}

	public int getV() {
		return v;
	}

	public boolean getTintIcon() {return tintIcon;}
}
