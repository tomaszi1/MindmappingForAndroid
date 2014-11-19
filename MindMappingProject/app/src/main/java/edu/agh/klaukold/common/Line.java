package edu.agh.klaukold.common;

import android.graphics.Color;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import org.xmind.ui.style.Styles;

import java.io.Serializable;


import edu.agh.klaukold.enums.Position;
import edu.agh.klaukold.gui.MainActivity;


public class Line implements Serializable, Cloneable {
	public String shape;
	private int thickness;
	private ColorDrawable color;
	private Point start;
	private Point end;
	private boolean isVisible;
    public Position position;
    public Box box;
    public int off = 0;

    public Drawable deleteLine;

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    private Path path = new Path();

    public void preparePath() {
        path.reset();
        path.moveTo(start.x, start.y);
        if (shape == null) {
          path.lineTo(end.x, end.y);
        } else if (shape.equals(Styles.BRANCH_CONN_STRAIGHT)) {
            path.lineTo(end.x, end.y);
        } else if (shape.equals(Styles.BRANCH_CONN_CURVE)) {
//            int startAngle = (int) (180 / Math.PI * Math.atan2(start.y - end.y, end.x - start.x));
//            float radius = 20;
//            final RectF oval = new RectF();
//            oval.set(start.x, start.y, end.x, end.y);
//            path.arcTo(oval, 0, 360, true);
//            if (end.x < 0) {
//                if (LineDirection.UP_RIGHT) {
            if (position == Position.RIGHT) {
                path.cubicTo(start.x, start.y, start.x + (start.x - start.y) + off, start.y + off, end.x, end.y);
            } else {
                path.cubicTo(start.x, start.y, start.x - (start.x - start.y) + off, start.y + off, end.x, end.y);
            }

//                    LineDirection.UP_RIGHT = false;
//                } else {
//                    path.cubicTo(start.x, start.y, (start.x - end.x) / 2, 1, end.x, end.y);
//                    LineDirection.UP_RIGHT = true;
//                }
//            } else {
//                if (LineDirection.UP_LEFT) {
//                    path.cubicTo(start.x, start.y, (start.x - end.x) / 2, 1, end.x, end.y);
//                    LineDirection.UP_LEFT = false;
//                } else {
//                    path.cubicTo(start.x, start.y, (start.x - end.x) / 2, 1, end.x, end.y);
//                    LineDirection.UP_LEFT = true;
//                }
//            }
//        } else if (shape == LineStyle.ARROWED_CURVE) {
//            if (position == Position.RIGHT) {
//                path.cubicTo(start.x, start.y, start.x + (start.x - start.y), start.y + 20, end.x, end.y);
//            } else {
//                path.cubicTo(start.x, start.y, start.x - (start.x - start.y), start.y + 20, end.x, end.y);
//            }
//            path.rMoveTo(end.x + ); }
       }  else {
            path.lineTo(end.x, end.y);
        }


    }

	public Line(String shape, int thickness, ColorDrawable color, Point start,
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


    public Line Clone() throws CloneNotSupportedException {
        return (Line) super.clone();
    }


}
