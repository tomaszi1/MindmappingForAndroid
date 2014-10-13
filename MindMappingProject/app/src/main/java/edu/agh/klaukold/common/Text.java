package edu.agh.klaukold.common;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;

import java.io.Serializable;

import edu.agh.klaukold.enums.Align;
import edu.agh.klaukold.enums.Font;

public class Text implements  Cloneable, Serializable{
    public  Text()
    {
//        align = Align.CENTER;
//        color.setColor(Color.BLACK);
//        size = 13;
        font = Font.ARIAL;
        text = "Central Topic";
        //text = "Central Topic ivdsvwegiv bzxuivweiv xwf egdwbv  dfidsvb\n buacbfwbev vwufq  uwlwq v uwdvwvewighve wdgiewdv z\n buifqwvcu3yewvd  wedfwegdh vegwqivfwegv  dffwegedghd cyeugf\n";
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

    public void setText(Text t) {
        this.font = t.font;
        this.size = t.size;
        this.isBold = t.isBold;
        this.isItalic = t.isItalic;
        this.isStrikeOut = t.isStrikeOut;
        this.align = t.align;
        this.color = t.getColor();
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

    public ColorDrawable getColor() {
         return color;
    }

    public void setColor(ColorDrawable color) {
        this.color = color;
    }


    private Font font;
	private int size; 
	private boolean isBold;
	private boolean isItalic;
	private boolean isStrikeOut;
	private Align align;
	private String text;
    private ColorDrawable color = new ColorDrawable();

//    public Text clone() {
//        Text text = new Text();
//        text.font = this.font.;
//        text.size = this.size;
//        text.isBold = this.isBold;
//        text.isItalic = this.isItalic;
//        text.isStrikeOut = this.isStrikeOut;
//        text.align = this.align;
//        text.text = this.text;
//        return text;
//    }

    public Text TextClone() throws CloneNotSupportedException {
        return (Text) super.clone();
    }
}
