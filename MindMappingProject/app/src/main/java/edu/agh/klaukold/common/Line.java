package edu.agh.klaukold.common;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import java.io.Serializable;

import edu.agh.klaukold.enums.LineStyle;
import edu.agh.klaukold.interfaces.Element;

public class Line implements Serializable {
	private LineStyle shape;
	private int thickness;
	private ColorDrawable color;
	private Point start;
	private Point end;
	private boolean isVisible;
	


	public Line(LineStyle shape, int thickness, ColorDrawable color, Point start,
			Point end, boolean isVisible) {
		super();
		this.shape = shape;
		this.thickness = thickness;
		this.color = color;
		this.start = start;
		this.end = end;
		this.isVisible = isVisible;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	public LineStyle getShape() {
		return shape;
	}

	public void setShape(LineStyle shape) {
		this.shape = shape;
	}

	public int getThickness() {
		return thickness;
	}

	public void setThickness(int thickness) {
		this.thickness = thickness;
	}

	public ColorDrawable getColor() {
		return color;
	}

	public void setColor(ColorDrawable color) {
		this.color = color;
	}

	public Point getStart() {
		return start;
	}

	public void setStart(Point start) {
		this.start = start;
	}

	public Point getEnd() {
		return end;
	}

	public void setEnd(Point end) {
		this.end = end;
	}

	private void calculateStart(Box box)
	{
		//TODO
	}
	
	private void calculateEnd(Box box)
	{
		//TODO
	}
	
	public Line() {}
	
	public void repaint()
	{}


}
