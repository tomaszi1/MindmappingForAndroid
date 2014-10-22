package edu.agh.klaukold.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RotateDrawable;

import android.os.Build;


import org.xmind.core.ITopic;
import org.xmind.core.style.IStyle;
import org.xmind.ui.style.Styles;

;
import edu.agh.klaukold.enums.Position;
import edu.agh.klaukold.gui.MainActivity;


public class Box implements  Cloneable, Serializable {
    public void updateBox(Box b) {
        this.height = b.height;
        this.width = b.width;
        this.point = b.point;
        this.children = b.children;
        this.topic = b.topic;
        this.lines = b.lines;
        this.newNote = b.newNote;
        this.addBox = b.addBox;
       // this.newMarker = b.newMarker;
        this.collapseAction = b.collapseAction;
        this.expandAction = b.expandAction;
        this.position = b.position;
        this.drawableShape = b.drawableShape;
    }


	public LinkedList<Box> getChildren() {
		return children;
	}
	public HashMap<Box, Line> getLines() {
		return lines;
	}
    public boolean isExpendable = false;
    public boolean isSelected = false;


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
	protected LinkedList<Box> children = new LinkedList<Box>();;
	protected HashMap<Box, Line> lines = new HashMap<Box, Line>();
    public Drawable newNote;
    public Drawable addBox;
    public Boolean isNote = false;
    public String note;
    //public Drawable newMarker;
    public Drawable collapseAction;
    public Drawable expandAction;
    public Position position;
    public Drawable addNote;
    public ITopic topic;


    public Drawable getDrawableShape() {
        return drawableShape;
    }

    public void setDrawableShape(Drawable drawableShape) {
        this.drawableShape = drawableShape;
    }
    public Drawable drawableShape;

	public Box() {}

    public Drawable prepareDrawableShape()
    {
        IStyle style = MainActivity.workbook.getStyleSheet().findStyle(topic.getStyleId());
        int[] colors = {Color.WHITE, Integer.parseInt(style.getProperty(Styles.FillColor))};
        String s = style.getProperty(Styles.ShapeClass);
        if (s.equals(Styles.TOPIC_SHAPE_RECT)) {
            ((GradientDrawable) drawableShape).setColors(colors);
            drawableShape.setBounds(point.x, point.y, point.x + width, point.y + height);

        } else if (s.equals(Styles.TOPIC_SHAPE_ELLIPSE)) {
            ((GradientDrawable) drawableShape).setColors(colors);
            drawableShape.setBounds(point.x, point.y, point.x + width, point.y + height);

        } else if (s.equals(Styles.TOPIC_SHAPE_ROUNDEDRECT)) {//dwie pierwsze warosci - polozenie lewego, gornego roku, pozostale pary zgodnie z ruchem wskazowek zegara
//                float[] arr = {10, 10, 10, 10, 10, 10, 10, 10};
//                RoundRectShape roundRectShape = new RoundRectShape(arr, null,null);
//                drawableShape = new ShapeDrawable(roundRectShape);
            ((GradientDrawable) drawableShape).setColors(colors);
            drawableShape.setBounds(point.x, point.y, point.x + width, point.y + height);

        } else if (s.equals(Styles.TOPIC_SHAPE_DIAMOND)) {
            ((GradientDrawable) ((RotateDrawable) drawableShape).getDrawable()).setColors(colors);
            drawableShape.setBounds(point.x, point.y, point.x + width, point.y + width);

        } else if (s.equals(Styles.TOPIC_SHAPE_UNDERLINE)) {// int[] colors2 = {Color.TRANSPARENT, Color.TRANSPARENT};
            drawableShape.setBounds(point.x, point.y, point.x + width, point.y + height);
            //  ((GradientDrawable)drawableShape).setColors(colors2);

        } else if (s.equals(Styles.TOPIC_SHAPE_NO_BORDER)) {
            int[] colors1 = {Color.TRANSPARENT, Color.TRANSPARENT};
            drawableShape.setBounds(point.x, point.y, point.x + width, point.y + height);
            ((GradientDrawable) drawableShape).setColors(colors1);
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
        if ( !MainActivity.workbook.getStyleSheet().findStyle(topic.getStyleId()).equals(Styles.TOPIC_SHAPE_DIAMOND)) {
            ((GradientDrawable)drawableShape).setColors(colors);
        } else {
            ((GradientDrawable)((RotateDrawable)drawableShape).getDrawable()).setColors(colors);
        }
    }

    public void addChild(Box box) {
        children.add(box);
    }
}
