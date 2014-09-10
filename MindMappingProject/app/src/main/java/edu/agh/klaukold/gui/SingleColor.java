package edu.agh.klaukold.gui;

import android.graphics.Color;
import android.graphics.Rect;

public class SingleColor {
	private Rect rectangle;
	private Color color;
	
	public SingleColor(Rect rectangle, Color color) {
		super();
		this.rectangle = rectangle;
		this.color = color;
	}
	public Rect getRectangle() {
		return rectangle;
	}
	public void setRectangle(Rect rectangle) {
		this.rectangle = rectangle;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}

}
