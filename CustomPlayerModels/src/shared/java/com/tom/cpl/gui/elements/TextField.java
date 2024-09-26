package com.tom.cpl.gui.elements;

import java.util.function.Supplier;

import com.tom.cpl.gui.IGui;
import com.tom.cpl.gui.KeyboardEvent;
import com.tom.cpl.gui.MouseEvent;
import com.tom.cpl.gui.util.TabFocusHandler.Focusable;
import com.tom.cpl.math.Box;

public class TextField extends GuiElement implements Supplier<IGui>, Focusable {
	private ITextField field;
	private int bgColor;

	public TextField(IGui gui) {
		super(gui);
		field = gui.getNative().getNative(TextField.class, this);
		this.bgColor = gui.getColors().popup_border;
	}

	@Override
	public void draw(MouseEvent event, float partialTicks) {
		gui.drawTextField(this, field, event, partialTicks);
		//field.draw(event.x, event.y, partialTicks, bounds);
	}
	@Override
	public void keyPressed(KeyboardEvent evt) {
		if(field.isFocused() && (evt.matches(gui.getKeyCodes().KEY_ENTER) || evt.matches(gui.getKeyCodes().KEY_KP_ENTER) || evt.matches(gui.getKeyCodes().KEY_ESCAPE))) {
			setFocused(false);
			evt.consume();
		}
		field.keyPressed(evt);
	}

	@Override
	public void mouseClick(MouseEvent evt) {
		field.mouseClick(evt);
	}

	public static interface ITextField {
		void draw(int mouseX, int mouseY, float partialTicks, Box bounds);
		void keyPressed(KeyboardEvent evt);
		void mouseClick(MouseEvent evt);
		String getText();
		void setText(String txt);
		void setEventListener(Runnable eventListener);
		void setEnabled(boolean enabled);
		boolean isFocused();
		void setFocused(boolean focused);
		int getCursorPos();
		void setCursorPos(int pos);
		default int getSelectionPos() {
			return -1;
		}
		default void setSelectionPos(int pos) {}
	}

	public String getText() {
		return field.getText();
	}

	public void setText(String txt) {
		field.setText(txt);
	}

	public void setEventListener(Runnable eventListener) {
		field.setEventListener(eventListener);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		field.setEnabled(enabled);
	}

	@Override
	public IGui get() {
		return gui;
	}

	@Override
	public boolean isFocused() {
		return field.isFocused();
	}

	@Override
	public void setFocused(boolean focused) {
		field.setFocused(focused);
	}

	public void setBackgroundColor(int bgColor) {
		this.bgColor = bgColor;
	}
	public int getBackgroundColor() {
		return this.bgColor;
	}

	public int getCursorPos() {
		return field.getCursorPos();
	}

	public void setCursorPos(int pos) {
		field.setCursorPos(pos);
	}

	public int getSelectionPos() {
		return field.getSelectionPos();
	}

	public void setSelectionPos(int pos) {
		field.setSelectionPos(pos);
	}

	public void setSelectionPos(int start, int end) {
		field.setCursorPos(end);
		field.setSelectionPos(start);
	}

	@Override
	public boolean isSelectable() {
		return visible && enabled;
	}
}
