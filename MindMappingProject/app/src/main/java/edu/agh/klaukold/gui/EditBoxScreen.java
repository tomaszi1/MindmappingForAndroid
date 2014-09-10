package edu.agh.klaukold.gui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.Spinner;

public class EditBoxScreen extends Activity {
	public Spinner getBoxShape() {
		return boxShape;
	}

	public void setBoxShape(Spinner boxShape) {
		this.boxShape = boxShape;
	}

	public Spinner getLineShape() {
		return lineShape;
	}

	public void setLineShape(Spinner lineShape) {
		this.lineShape = lineShape;
	}

	public Spinner getLineThickness() {
		return lineThickness;
	}

	public void setLineThickness(Spinner lineThickness) {
		this.lineThickness = lineThickness;
	}

	public ColorPalette getColorBoxPalette() {
		return colorBoxPalette;
	}

	public void setColorBoxPalette(ColorPalette colorBoxPalette) {
		this.colorBoxPalette = colorBoxPalette;
	}

	public ColorPalette getColorLinePalette() {
		return colorLinePalette;
	}

	public void setColorLinePalette(ColorPalette colorLinePalette) {
		this.colorLinePalette = colorLinePalette;
	}

	public Spinner getTextHeight() {
		return textHeight;
	}

	public void setTextHeight(Spinner textHeight) {
		this.textHeight = textHeight;
	}

	public Spinner getTextAlign() {
		return textAlign;
	}

	public void setTextAlign(Spinner textAlign) {
		this.textAlign = textAlign;
	}

	public Spinner getFont() {
		return font;
	}

	public void setFont(Spinner font) {
		this.font = font;
	}

	public CheckBox getItalic() {
		return italic;
	}

	public void setItalic(CheckBox italic) {
		this.italic = italic;
	}

	public CheckBox getBold() {
		return bold;
	}

	public void setBold(CheckBox bold) {
		this.bold = bold;
	}

	public CheckBox getStrikeOut() {
		return strikeOut;
	}

	public void setStrikeOut(CheckBox strikeOut) {
		this.strikeOut = strikeOut;
	}

	private Spinner boxShape;
	private Spinner lineShape;
	private Spinner lineThickness;
	private ColorPalette colorBoxPalette;
	private ColorPalette colorLinePalette;
	private Spinner textHeight;
	private Spinner textAlign;
	private Spinner font;
	private CheckBox italic;
	private CheckBox bold;
	private CheckBox strikeOut;

	@Override
	public void onResume() {
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

	}
	
	@Override
	public void onDestroy() {
	}
}
