package edu.agh.klaukold.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;

import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RotateDrawable;

import android.os.Build;
import android.util.Log;


import org.xmind.core.IRelationship;
import org.xmind.core.ITopic;
import org.xmind.core.style.IStyle;
import org.xmind.ui.style.Styles;

;
import edu.agh.R;
import edu.agh.klaukold.enums.Position;
import edu.agh.klaukold.gui.MainActivity;


public class Box implements  Cloneable, Serializable, Comparable<Box> {
    @Override
    public int compareTo(Box another) {
        int res = 1;
        if (topic.getStyleId() == another.topic.getStyleId()) {
            res = 0;
        }
        if (!topic.getTitleText().equals(another.topic.getTitleText())) {
            res = 1;
        }
        return res;
    }

    public void updateBox(Box b) {
        this.height = b.height;
        this.width = b.width;
        this.point = b.point;
        this.children = b.children;
        this.topic = b.topic;
        this.lines = b.lines;
        this.newNote = b.newNote;
        this.addBox = b.addBox;
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
    public HashMap<IRelationship, Box> relationships = new HashMap<IRelationship, Box>();

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
    public  Point point;
	protected LinkedList<Box> children = new LinkedList<Box>();;
	protected HashMap<Box, Line> lines = new HashMap<Box, Line>();
    public Drawable newNote;
    public Drawable addBox;
    public Drawable collapseAction;
    public Drawable expandAction;
    public Position position;
    public Drawable addNote;
    public ITopic topic;
    public Box parent;



    public void clear() {
        children.clear();
    }


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
        int c = Color.BLUE;
        int c2 = Color.WHITE;
        if (MainActivity.sheet1.getTheme() != null) {
            if (MainActivity.sheet1.getTheme().getName().equals("%classic")) {
                if (topic.isRoot()) {
                    c = MainActivity.res.getColor(R.color.lime_green);
                } else {
                    c = MainActivity.res.getColor(R.color.light_blue);
                }
            } else if (MainActivity.sheet1.getTheme().getName().equals("%simple")) {
                c = Color.WHITE;
            } else if (MainActivity.sheet1.getTheme().getName().equals("%business")) {
                if (topic.isRoot()) {
                    c = MainActivity.res.getColor(R.color.copper);
                } else {
                    c = MainActivity.res.getColor(R.color.beige);
                }
             } else if (MainActivity.sheet1.getTheme().getName().equals("%academese")) {
                c = MainActivity.res.getColor(R.color.dark_gray);
                c2 = MainActivity.res.getColor(R.color.dark_gray);
            } else if (MainActivity.sheet1.getTheme().getName().equals("%comic")) {
                if (topic.isRoot()) {
                    c = MainActivity.res.getColor(R.color.orange);
                } else {
                    c = MainActivity.res.getColor(R.color.light_blue);
                }
            }

        }
        if (style!=null && style.getProperty(Styles.FillColor) != null) {
            Log.w("color", style.getProperty(Styles.FillColor));
            c = Color.parseColor(style.getProperty(Styles.FillColor));
        }
        int[] colors = {c2, c};
        String s = null;
        if (style != null) {
            s = style.getProperty(Styles.ShapeClass);
        }
//        if (s.equals(Styles.TOPIC_SHAPE_RECT)) {
//            ((GradientDrawable) drawableShape).setColors(colors);
//            drawableShape.setBounds(point.x, point.y, point.x + width, point.y + height);
//
//        }
        if (s == null) {
            ((GradientDrawable) drawableShape).setColors(colors);
            drawableShape.setBounds(point.x, point.y, point.x + width, point.y + height);
        }
        else if (s.equals(Styles.TOPIC_SHAPE_ELLIPSE)) {
            ((GradientDrawable) drawableShape).setColors(colors);
            drawableShape.setBounds(point.x, point.y, point.x + width, point.y + height);

        } else if (s.equals(Styles.TOPIC_SHAPE_ROUNDEDRECT)) {//dwie pierwsze warosci - polozenie lewego, gornego roku, pozostale pary zgodnie z ruchem wskazowek zegara;
            ((GradientDrawable) drawableShape).setColors(colors);
            drawableShape.setBounds(point.x, point.y, point.x + width, point.y + height);

        } else if (s.equals(Styles.TOPIC_SHAPE_DIAMOND)) {
            ((GradientDrawable) (((RotateDrawable) drawableShape).getDrawable())).setColors(colors);
            drawableShape.setBounds(point.x, point.y, point.x + width, point.y + width);

        } else if (s.equals(Styles.TOPIC_SHAPE_UNDERLINE)) {
            int c1;
            int  c3;
            if (style!=null && style.getProperty(Styles.FillColor) != null) {
                c1 = Color.WHITE;
                c3 = Color.parseColor(style.getProperty(Styles.FillColor));
            } else {
                c1 = Color.TRANSPARENT;
                c3 = Color.TRANSPARENT;
            }
            int[] colors2 = {c1, c3};
            drawableShape.setBounds(point.x, point.y, point.x + width, point.y + height);
             ((GradientDrawable)drawableShape).setColors(colors2);

        } else if (s.equals(Styles.TOPIC_SHAPE_NO_BORDER)) {
            int c1;
            int  c4;
            if (style!=null && style.getProperty(Styles.FillColor) != null) {
                c1 = Color.WHITE;
                c4 = Color.parseColor(style.getProperty(Styles.FillColor));
            } else {
                c1 = Color.TRANSPARENT;
                c4 = Color.TRANSPARENT;
            }
            int[] colors1 = {c1, c4};
            drawableShape.setBounds(point.x, point.y, point.x + width, point.y + height);
            ((GradientDrawable) drawableShape).setColors(colors1);
        } else {
            ((GradientDrawable) drawableShape).setColors(colors);
            drawableShape.setBounds(point.x, point.y, point.x + width, point.y + height);
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
        if (topic.getStyleId() != null && MainActivity.workbook.getStyleSheet().findStyle(topic.getStyleId()) != null && (MainActivity.workbook.getStyleSheet().findStyle(topic.getStyleId()).getProperty(Styles.ShapeClass) == null ||   !MainActivity.workbook.getStyleSheet().findStyle(topic.getStyleId()).getProperty(Styles.ShapeClass).equals(Styles.TOPIC_SHAPE_DIAMOND))) {
            ((GradientDrawable)drawableShape).setColors(colors);
        } else if (topic.getStyleId() != null && MainActivity.workbook.getStyleSheet().findStyle(topic.getStyleId()) != null && MainActivity.workbook.getStyleSheet().findStyle(topic.getStyleId()).getProperty(Styles.ShapeClass).equals(Styles.TOPIC_SHAPE_DIAMOND)){
            ((GradientDrawable)((RotateDrawable)drawableShape).getDrawable()).setColors(colors);
        } else if (isSelected) {
            ((GradientDrawable)drawableShape).setColors(colors);
        } else {
            int[] colors1 = {Color.WHITE, Color.BLUE};
            ((GradientDrawable)drawableShape).setColors(colors1);
        }
    }

    public void addChild(Box box) {
        children.add(box);
    }
}
