package edu.agh.klaukold.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.CornerPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RotateDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;


import edu.agh.klaukold.enums.BlockShape;
import edu.agh.klaukold.enums.LineStyle;
import edu.agh.klaukold.enums.LineThickness;
import edu.agh.klaukold.enums.Position;


public class Box implements  Cloneable, Serializable {
    public void updateBox(Box b) {
        this.height = b.height;
        this.width = b.width;
        this.point = b.point;
        this.isSelected = b.isSelected;
        this.parent = b.parent;
        this.children = b.children;
        this.shape = b.shape;
        this.color = b.color;
        this.lines = b.lines;
        this.text = b.text;
        this.notes = b.notes;
        this.markers = b.markers;
        this.isVisible = b.isVisible;
        this.isExpanded = b.isExpanded;
        this.newNote = b.newNote;
        this.addBox = b.addBox;
       // this.newMarker = b.newMarker;
        this.collapseAction = b.collapseAction;
        this.expandAction = b.expandAction;
        this.position = b.position;
        this.lineThickness = b.lineThickness;
        this.lineColor = b.lineColor;
        this.lineStyle = b.lineStyle;
        this.drawableShape = b.drawableShape;
    }

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
	public HashMap<Box, Line> getLines() {
		return lines;
	}
	public void setLines(HashMap<Box, Line> lines) {
		this.lines = lines;
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
    public boolean isExpendable = false;
	
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

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    protected  int height = 110;
    protected  int width = 150;
    public  Point point = new Point();
	protected boolean isSelected = false;
	protected Box parent;
	protected LinkedList<Box> children = new LinkedList<Box>();
	protected BlockShape shape;
	protected ColorDrawable color = new ColorDrawable();
	protected HashMap<Box, Line> lines = new HashMap<Box, Line>();
	protected Text text;
	protected LinkedList<Note> notes = new LinkedList<Note>();
	protected LinkedList<Marker> markers = new LinkedList<Marker>();
	protected boolean isVisible = true;
    protected boolean isExpanded = true;
    public Drawable newNote;
    public Drawable addBox;
    public Boolean isNote = false;
    public String note;
    //public Drawable newMarker;
    public Drawable collapseAction;
    public Drawable expandAction;
    public Position position;
    public Drawable addNote;


    public LineThickness getLineThickness() {
        return lineThickness;
    }

    public void setLineThickness(LineThickness lineThickness) {
        this.lineThickness = lineThickness;
    }

    private LineThickness lineThickness;

    public int getLineColor() {
        return lineColor;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }

    private int lineColor;

    public LineStyle getLineStyle() {
        return lineStyle;
    }

    public void setLineStyle(LineStyle lineStyle) {
        this.lineStyle = lineStyle;
    }

    private LineStyle lineStyle;

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean isExpanded) {
        this.isExpanded = isExpanded;
    }
    public Drawable getDrawableShape() {
        return drawableShape;
    }

    public void setDrawableShape(Drawable drawableShape) {
        this.drawableShape = drawableShape;
    }
    protected Drawable drawableShape;

	public Box() {}



	public void hideChildren() {}
	public void showChildren() {}
	public void repaint() {}
    public void draw(ShapeDrawable drawable)
    {
        drawable.getPaint().setColor(color.getColor());
    }
    public Drawable prepareDrawableShape()
    {
        int[] colors = {Color.WHITE, color.getColor()};
        switch (shape)
        {
            case  RECTANGLE:
                ((GradientDrawable)drawableShape).setColors(colors);
                drawableShape.setBounds(point.x, point.y, point.x + width, point.y + height);
                break;
            case ELLIPSE:
                ((GradientDrawable)drawableShape).setColors(colors);
                drawableShape.setBounds(point.x, point.y, point.x + width, point.y + height);
                break;
            case ROUNDED_RECTANGLE:
                //dwie pierwsze warosci - polozenie lewego, gornego roku, pozostale pary zgodnie z ruchem wskazowek zegara
//                float[] arr = {10, 10, 10, 10, 10, 10, 10, 10};
//                RoundRectShape roundRectShape = new RoundRectShape(arr, null,null);
//                drawableShape = new ShapeDrawable(roundRectShape);
                ((GradientDrawable)drawableShape).setColors(colors);
                drawableShape.setBounds(point.x, point.y, point.x + width, point.y + height);
             break;
            case DIAMOND:
                ((GradientDrawable)((RotateDrawable)drawableShape).getDrawable()).setColors(colors);
                drawableShape.setBounds(point.x, point.y, point.x + width, point.y + width);
                break;
            case UNDERLINE:
               // int[] colors2 = {Color.TRANSPARENT, Color.TRANSPARENT};
                drawableShape.setBounds(point.x, point.y, point.x + width, point.y + height);
              //  ((GradientDrawable)drawableShape).setColors(colors2);
                break;
            case NO_BORDER:
                int[] colors1 = {Color.TRANSPARENT, Color.TRANSPARENT};
                drawableShape.setBounds(point.x, point.y, point.x + width, point.y + height);
                ((GradientDrawable)drawableShape).setColors(colors1);
                break;
        }
        return  drawableShape;
    }
//
//    public void Clone(Box box)
//    {
//        height = box.getHeight();
//        width = box.getWidth();
//        isSelected = box.isSelected();
//        point = box.getPoint();
//        parent = box.getParent();
//        children = box.getChildren();
//        shape = box.getShape();
//        color = box.getColor();
//        lines = box.getLines();
//        text = box.getText().clone();
//        notes = box.getNotes();
//        markers = box.getMarkers();
//        isVisible = box.isVisible;
//        isExpanded = box.isExpanded;
//    }
    public Box BoxClone() throws CloneNotSupportedException {
      return  (Box)super.clone();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setActiveColor() {
        int[] colors = {Color.rgb(0,51,102), Color.rgb(0,51, 102)};
        if (shape != BlockShape.DIAMOND) {
            ((GradientDrawable)drawableShape).setColors(colors);
        } else {
            ((GradientDrawable)((RotateDrawable)drawableShape).getDrawable()).setColors(colors);
        }
    }

    public void addChild(Box box) {
        children.add(box);
    }
}
