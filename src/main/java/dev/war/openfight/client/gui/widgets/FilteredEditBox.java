package dev.war.openfight.client.gui.widgets;

import java.util.function.Consumer;
import java.util.function.Predicate;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

/**
 * EditBox with the 1.21.1 {@code setFilter(Predicate<String>)} behaviour, which 26.3 removed.
 * Edits whose resulting value does not satisfy the filter are rolled back.
 */
public class FilteredEditBox extends EditBox {
	private Predicate<String> filter = s -> true;
	private boolean rollingBack;
	
	public FilteredEditBox(Font font, int x, int y, int width, int height, Component narration) {
		super(font, x, y, width, height, narration);
	}
	
	public FilteredEditBox(Font font, int width, int height, Component narration) {
		super(font, width, height, narration);
	}
	
	public void setFilter(Predicate<String> filter) {
		this.filter = filter == null ? s -> true : filter;
	}
	
	private void rollback(String oldValue, int oldCursor) {
		Consumer<String> responder = this.responder;
		this.responder = null;
		this.rollingBack = true;
		
		try {
			super.setValue(oldValue);
			this.setCursorPosition(oldCursor);
			this.setHighlightPos(oldCursor);
		} finally {
			this.rollingBack = false;
			this.responder = responder;
		}
	}
	
	@Override
	public void setValue(String value) {
		if (this.rollingBack || this.filter.test(value)) {
			super.setValue(value);
		}
	}
	
	@Override
	public void insertText(String input) {
		String oldValue = this.getValue();
		int oldCursor = this.getCursorPosition();
		Consumer<String> responder = this.responder;
		this.responder = null;
		
		try {
			super.insertText(input);
		} finally {
			this.responder = responder;
		}
		
		if (!this.filter.test(this.getValue())) {
			this.rollback(oldValue, oldCursor);
		} else if (!oldValue.equals(this.getValue()) && responder != null) {
			responder.accept(this.getValue());
		}
	}
	
	@Override
	public void deleteCharsToPos(int pos) {
		String oldValue = this.getValue();
		int oldCursor = this.getCursorPosition();
		Consumer<String> responder = this.responder;
		this.responder = null;
		
		try {
			super.deleteCharsToPos(pos);
		} finally {
			this.responder = responder;
		}
		
		if (!this.filter.test(this.getValue())) {
			this.rollback(oldValue, oldCursor);
		} else if (!oldValue.equals(this.getValue()) && responder != null) {
			responder.accept(this.getValue());
		}
	}
}
