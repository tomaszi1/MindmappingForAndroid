package edu.agh.klaukold.common;

import edu.agh.klaukold.enums.Align;
import edu.agh.klaukold.enums.Font;

public class Text {
    public  Text()
    {
        text = "Main Topic";
    }
	public Text(Font font, int size, boolean isBold, boolean isItalic,
			boolean isStrikeOut, Align align, String text) {
		super();
		this.font = font;
		this.size = size;
		this.isBold = isBold;
		this.isItalic = isItalic;
		this.isStrikeOut = isStrikeOut;
		this.align = align;
		this.text = text;
	}
	public Font getFont() {
		return font;
	}
	public void setFont(Font font) {
		this.font = font;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public boolean isBold() {
		return isBold;
	}
	public void setBold(boolean isBold) {
		this.isBold = isBold;
	}
	public boolean isItalic() {
		return isItalic;
	}
	public void setItalic(boolean isItalic) {
		this.isItalic = isItalic;
	}
	public boolean isStrikeOut() {
		return isStrikeOut;
	}
	public void setStrikeOut(boolean isStrikeOut) {
		this.isStrikeOut = isStrikeOut;
	}
	public Align getAlign() {
		return align;
	}
	public void setAlign(Align align) {
		this.align = align;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	private Font font;
	private int size; 
	private boolean isBold;
	private boolean isItalic;
	private boolean isStrikeOut;
	private Align align;
	private String text;
}
