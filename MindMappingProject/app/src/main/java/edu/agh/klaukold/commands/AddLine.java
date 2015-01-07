package edu.agh.klaukold.commands;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;

import org.xmind.core.ITopic;
import org.xmind.core.style.IStyle;
import org.xmind.ui.style.Styles;

import java.util.Properties;

import edu.agh.klaukold.common.Box;
import edu.agh.klaukold.common.Line;
import edu.agh.klaukold.common.Point;
import edu.agh.klaukold.gui.MainActivity;
import edu.agh.klaukold.interfaces.Command;


public class AddLine implements Command {
   Properties after;
   Box box;
   Box pParent;

	@Override
	public void execute(Properties properties) {
        after = properties;
        Box child = (Box) properties.get("child");
        box = child;
        Box parent = null;
        pParent = child.parent;
        if (properties.containsKey("parent")) {
            parent = (Box) properties.get("parent");
            if (parent.topic.isRoot() || parent.topic.getParent() != null) {
              Line line = null;
              IStyle parentStyle = MainActivity.styleSheet.findStyle(parent.topic.getStyleId());
              if (child.drawableShape.getBounds().left <= MainActivity.root.drawableShape.getBounds().centerX()) {
                  if (parentStyle != null && parentStyle.getProperty(Styles.LineClass) != null) {
                      line = new Line(parentStyle.getProperty(Styles.LineClass), Integer.parseInt(parentStyle.getProperty(Styles.LineWidth).substring(0, parentStyle.getProperty(Styles.LineWidth).length() - 2)), new ColorDrawable(Integer.parseInt(parentStyle.getProperty(Styles.LineColor))),
                              new Point(parent.getDrawableShape().getBounds().left,
                                      parent.getDrawableShape().getBounds().top + (parent.getDrawableShape().getBounds().bottom - parent.getDrawableShape().getBounds().top) / 2),
                              new Point(child.getDrawableShape().getBounds().right,
                                      child.getDrawableShape().getBounds().top + (child.getDrawableShape().getBounds().bottom - child.getDrawableShape().getBounds().top) / 2), true);
                  } else {
                      line = new Line(Styles.BRANCH_CONN_STRAIGHT, 2, new ColorDrawable(Color.GRAY),
                              new Point(parent.getDrawableShape().getBounds().left,
                                      parent.getDrawableShape().getBounds().top + (parent.getDrawableShape().getBounds().bottom - parent.getDrawableShape().getBounds().top) / 2),
                              new Point(child.getDrawableShape().getBounds().right,
                                      child.getDrawableShape().getBounds().top + (child.getDrawableShape().getBounds().bottom - child.getDrawableShape().getBounds().top) / 2), true);
                  }
              } else {
                  if (parentStyle != null ) {
                      String width = parentStyle.getProperty(Styles.LineWidth);
                      if (width == null) {
                          width = "1";
                      } else {
                          width = width.substring(0, parentStyle.getProperty(Styles.LineWidth).length() - 2);
                      }
                      String color = parentStyle.getProperty(Styles.LineColor);
                      if (color == null) {
                          color = "#" + Integer.toString(Integer.valueOf(Color.red(Color.GRAY)), 16) + Integer.toString(Integer.valueOf(Color.green(Color.GRAY)), 16) + Integer.toString(Integer.valueOf(Color.blue(Color.GRAY)), 16);
                      }
                      line = new Line(parentStyle.getProperty(Styles.LineClass), Integer.parseInt(width), new ColorDrawable(Color.parseColor(color)),
                              new Point(parent.getDrawableShape().getBounds().right,
                                      parent.getDrawableShape().getBounds().top + (parent.getDrawableShape().getBounds().bottom - parent.getDrawableShape().getBounds().top) / 2),
                              new Point(child.getDrawableShape().getBounds().left,
                                      child.getDrawableShape().getBounds().top + (child.getDrawableShape().getBounds().bottom - child.getDrawableShape().getBounds().top) / 2), true);
                  } else {
                      line = new Line(Styles.BRANCH_CONN_STRAIGHT, 2,  new ColorDrawable(Color.GRAY),
                              new Point(parent.getDrawableShape().getBounds().right,
                                      parent.getDrawableShape().getBounds().top + (parent.getDrawableShape().getBounds().bottom - parent.getDrawableShape().getBounds().top) / 2),
                              new Point(child.getDrawableShape().getBounds().left,
                                      child.getDrawableShape().getBounds().top + (child.getDrawableShape().getBounds().bottom - child.getDrawableShape().getBounds().top) / 2), true);
                  }
              }
                if (!child.topic.isRoot()) {
                    if (child.parent != null) {
                        child.parent.getChildren().remove(child);
                        child.parent.getLines().remove(child);
                    }
                    parent.getChildren().add(child);
                    parent.getLines().put(child, line);
                    parent.topic.add(child.topic);
                    child.parent = parent;
                }
            } else {
                Line line = null;
                IStyle parentStyle = MainActivity.styleSheet.findStyle(child.topic.getStyleId());
                if (child.drawableShape.getBounds().left <= MainActivity.root.drawableShape.getBounds().centerX()) {
                    line = new Line(parentStyle.getProperty(Styles.LineClass), Integer.parseInt(parentStyle.getProperty(Styles.LineWidth).substring(0,parentStyle.getProperty(Styles.LineWidth).length() - 2)), new ColorDrawable(Integer.parseInt(parentStyle.getProperty(Styles.LineColor))),
                            new Point(child.getDrawableShape().getBounds().left,
                                    child.getDrawableShape().getBounds().top + (child.getDrawableShape().getBounds().bottom - child.getDrawableShape().getBounds().top) / 2),
                            new Point(parent.getDrawableShape().getBounds().right,
                                    parent.getDrawableShape().getBounds().top + (parent.getDrawableShape().getBounds().bottom - parent.getDrawableShape().getBounds().top) / 2), true);
                } else {
                    if (parentStyle != null) {
                    line = new Line(parentStyle.getProperty(Styles.LineClass), Integer.parseInt(parentStyle.getProperty(Styles.LineWidth).substring(0, parentStyle.getProperty(Styles.LineWidth).length() - 2)), new ColorDrawable(Integer.parseInt(parentStyle.getProperty(Styles.LineColor))),
                            new Point(child.getDrawableShape().getBounds().right,
                                    child.getDrawableShape().getBounds().top + (child.getDrawableShape().getBounds().bottom - child.getDrawableShape().getBounds().top) / 2),
                            new Point(parent.getDrawableShape().getBounds().left,
                                    parent.getDrawableShape().getBounds().top + (parent.getDrawableShape().getBounds().bottom - parent.getDrawableShape().getBounds().top) / 2), true);
                } else {
                        line = new Line(Styles.BRANCH_CONN_STRAIGHT, 2, new ColorDrawable(Color.GRAY),
                                new Point(child.getDrawableShape().getBounds().right,
                                        child.getDrawableShape().getBounds().top + (child.getDrawableShape().getBounds().bottom - child.getDrawableShape().getBounds().top) / 2),
                                new Point(parent.getDrawableShape().getBounds().left,
                                        parent.getDrawableShape().getBounds().top + (parent.getDrawableShape().getBounds().bottom - parent.getDrawableShape().getBounds().top) / 2), true);
                    }
                }
                if (parent.parent != null) {
                    parent.parent.getChildren().remove(child);
                    parent.parent.getLines().remove(child);
                }
                child.getChildren().add(parent);
                child.getLines().put(parent, line);
                child.topic.add(parent.topic);
                parent.parent = child;
            }
        } else {
           child.parent.topic.remove(child.topic);
//           MainActivity.root.topic.getChildren(ITopic.DETACHED).add(child.topic);
           child.parent.getLines().remove(child);
           child.parent = null;
        }
	}

    @Override
    public void undo() {
        if (box.parent != null) {
            box.parent.getChildren().remove(box);
            box.parent.getLines().remove(box);
            box.parent.topic.getChildren(ITopic.ATTACHED).remove(box.topic);
        }
            box.parent = pParent;
            pParent.topic.add(box.topic);
            pParent.getChildren().add(box);
            Line line;
            IStyle parentStyle = MainActivity.styleSheet.findStyle(pParent.topic.getStyleId());
            String shape = Styles.BRANCH_CONN_STRAIGHT;
            int width = 1;
            String color = "#C0C0C0";
            if (parentStyle != null) {
                if (parentStyle.getProperty(Styles.LineClass) != null) {
                    shape = parentStyle.getProperty(Styles.LineClass);
                }
                if (parentStyle.getProperty(Styles.LineWidth) != null) {
                    width = Integer.parseInt(parentStyle.getProperty(Styles.LineWidth).substring(0, parentStyle.getProperty(Styles.LineWidth).length() - 2));
                }
                if (parentStyle.getProperty(Styles.LineColor) != null) {
                    color = parentStyle.getProperty(Styles.LineColor);
                }
            }

            if (box.drawableShape.getBounds().left <= MainActivity.root.drawableShape.getBounds().centerX()) {
                line = new Line(shape, width, new ColorDrawable(Color.parseColor(color)),
                        new Point(pParent.getDrawableShape().getBounds().left,
                                pParent.getDrawableShape().getBounds().top + (pParent.getDrawableShape().getBounds().bottom - pParent.getDrawableShape().getBounds().top) / 2),
                        new Point(box.getDrawableShape().getBounds().right,
                                box.getDrawableShape().getBounds().top + (box.getDrawableShape().getBounds().bottom - box.getDrawableShape().getBounds().top) / 2), true);
            } else {
                line = new Line(shape, width, new ColorDrawable(Color.parseColor(color)),
                        new Point(pParent.getDrawableShape().getBounds().right,
                                pParent.getDrawableShape().getBounds().top + (pParent.getDrawableShape().getBounds().bottom - pParent.getDrawableShape().getBounds().top) / 2),
                        new Point(box.getDrawableShape().getBounds().left,
                                box.getDrawableShape().getBounds().top + (box.getDrawableShape().getBounds().bottom - box.getDrawableShape().getBounds().top) / 2), true);
            }
            pParent.getLines().put(box, line);

    }

    @Override
    public void redo() {
        execute(after);
    }


}
