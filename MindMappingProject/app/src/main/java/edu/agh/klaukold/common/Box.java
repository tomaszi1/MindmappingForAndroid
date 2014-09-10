package edu.agh.klaukold.common;

import java.util.LinkedList;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.CornerPathEffect;
import android.graphics.LinearGradient;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;


import edu.agh.klaukold.enums.BlockShape;



public class Box  {
	public Box getParent() {
		return parent;
	}
	public void setParent(Box parent) {
		this.parent = parent;
	}
	public LinkedList<Box> getChildren() {
		return children;
	}
	public void setChildren(LinkedList<Box> children) {
		this.children = children;
	}
	public BlockShape getShape() {
		return shape;
	}
	public void setShape(BlockShape shape) {
		this.shape = shape;
	}
	public ColorDrawable getColor() {
		return color;
	}
	public void setColor(ColorDrawable color) {
		this.color = color;
	}
	public Line getLine() {
		return line;
	}
	public void setLine(Line line) {
		this.line = line;
	}
	public Text getText() {
		return text;
	}
	public void setText(Text text) {
		this.text = text;
	}
	public LinkedList<Note> getNotes() {
		return notes;
	}
	public void setNotes(LinkedList<Note> notes) {
		this.notes = notes;
	}
	public LinkedList<Marker> getMarkers() {
		return markers;
	}
	public void setMarkers(LinkedList<Marker> markers) {
		this.markers = markers;
	}

	public boolean isSelected() {
		return isSelected;
	}
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	
	public boolean isVisible() {
		return isVisible;
	}
	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
    protected  int height = 80;
    protected  int width = 110;
	protected boolean isSelected = false;
	protected Box parent;
	protected LinkedList<Box> children = new LinkedList<Box>();
	protected BlockShape shape;
	protected ColorDrawable color;
	protected Line line;
	protected Text text;
	protected LinkedList<Note> notes = new LinkedList<Note>();
	protected LinkedList<Marker> markers = new LinkedList<Marker>();
	protected boolean isVisible = true;
    protected boolean isExpanded = false;

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean isExpanded) {
        this.isExpanded = isExpanded;
    }
    public GradientDrawable getDrawableShape() {
        return drawableShape;
    }

    public void setDrawableShape(GradientDrawable drawableShape) {
        this.drawableShape = drawableShape;
    }

    protected GradientDrawable drawableShape;

	public Box() {}
	public Box(Box parent)
	{
		this.parent = parent; 
	}
	
	public void hideChildren() {}
	public void showChildren() {}
	public void repaint() {}
    public void draw(ShapeDrawable drawable)
    {
        drawable.getPaint().setColor(color.getColor());
    }
    public GradientDrawable prepareDrawableShape()
    {
        //TODO dokonczyc ksztalty
        switch (shape)
        {
            case  RECTANGLE:
                break;
            case ELLIPSE:
                break;
            case ROUNDED_RECTANGLE:
                //dwie pierwsze warosci - polozenie lewego, gornego roku, pozostale pary zgodnie z ruchem wskazowek zegara
//                float[] arr = {10, 10, 10, 10, 10, 10, 10, 10};
//                RoundRectShape roundRectShape = new RoundRectShape(arr, null,null);
//                drawableShape = new ShapeDrawable(roundRectShape);
                int[] colors = {Color.WHITE, color.getColor()};
                drawableShape.setColors(colors);
                drawableShape.setBounds(30, 50, 30 + width, 50 + height);
//                Shader shader1 = new LinearGradient(0, 0, 0, 50, new int[] {
//                        0xFFFFFF, color.getColor() }, null, Shader.TileMode.CLAMP);
//                drawableShape.getPaint().setShader(shader1);
             break;
            case DIAMOND:
                break;
            case UNDERLINE:
                break;
            case NO_BORDER:
                break;
        }
        return  drawableShape;
    }
}
