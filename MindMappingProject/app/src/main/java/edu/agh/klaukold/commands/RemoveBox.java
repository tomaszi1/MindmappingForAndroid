package edu.agh.klaukold.commands;

import android.graphics.drawable.ColorDrawable;

import org.xmind.core.style.IStyle;
import org.xmind.ui.style.Styles;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;

import edu.agh.R;
import edu.agh.klaukold.common.Box;
import edu.agh.klaukold.common.Line;
import edu.agh.klaukold.common.Point;
import edu.agh.klaukold.gui.MainActivity;
import edu.agh.klaukold.interfaces.Command;

public class RemoveBox implements Command {
    Properties before;
    Properties after;
    public HashMap<Box, Line> boxes;

	@Override
	public void execute(Properties properties) {
		before = (Properties) properties.clone();
        before.put("boxes", ((HashMap<Box, Line>)properties.get("boxes")).clone());
        after = (Properties) properties.clone();
        boxes = (HashMap<Box, Line>) properties.get("boxes");
        for (Box box : boxes.keySet()) {
            if (!box.topic.isRoot()) {
              box.topic.getParent().getAllChildren().remove(box.topic);
              box.parent.getChildren().remove(box);
            }
            //todo usuniecie linii
            box.parent.getLines().remove(box);
        }

	}

    @Override
    public void undo() {
        HashMap<Box, Line> boxes1 = (HashMap<Box, Line>)before.get("boxes");
        for (Box b: boxes1.keySet()) {
            b.prepareDrawableShape();
            if (!b.topic.isRoot())
            {
                IStyle s = MainActivity.workbook.getStyleSheet().findStyle(b.topic.getParent().getStyleId());
                int width = 1;
                if (s != null && s.getProperty(Styles.LineWidth) != null) {
                    width = Integer.parseInt(s.getProperty(Styles.LineWidth).substring(0, s.getProperty(Styles.LineWidth).length() - 2));
                }
                int color = MainActivity.res.getColor(R.color.light_gray);
                if (s != null && s.getProperty(Styles.LineColor)!= null) {
                    color = Integer.parseInt(s.getProperty(Styles.LineColor));
                }
                String shape = null;
                if (s != null) {
                    shape = s.getProperty(Styles.LineClass);
                }
                Point start = null;
                Point end;
                if (b.getDrawableShape().getBounds().left <= MainActivity.root.getDrawableShape().getBounds().left) {
                    start = new Point(b.parent.getDrawableShape().getBounds().left, b.parent.getDrawableShape().getBounds().centerY());
                    end = new Point(b.getDrawableShape().getBounds().right, b.getDrawableShape().getBounds().centerY());
                } else {
                    start = new Point(b.parent.getDrawableShape().getBounds().right, b.parent.getDrawableShape().getBounds().centerY());
                    end = new Point(b.getDrawableShape().getBounds().left, b.getDrawableShape().getBounds().centerY());
                }
                Line line = new Line(shape, width, new ColorDrawable(color), start, end, true);
                b.parent.getLines().put(b, line);
                b.topic.getParent().add(b.topic);
                b.parent.addChild(b);
            }

        }
    }

    @Override
    public void redo() {
        execute(after);
    }
}
