package edu.agh.klaukold.commands;

import android.graphics.drawable.ColorDrawable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;

import edu.agh.klaukold.common.Box;
import edu.agh.klaukold.common.Line;
import edu.agh.klaukold.common.Marker;
import edu.agh.klaukold.common.Note;
import edu.agh.klaukold.common.Point;
import edu.agh.klaukold.common.Root;
import edu.agh.klaukold.enums.Position;
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
            if (box.getParent() instanceof Root) {
              if  (((Root) box.getParent()).getLeftChildren().contains(box)) {
                  ((Root) box.getParent()).getLeftChildren().remove(box);
              } else {
                  ((Root) box.getParent()).getRightChildren().remove(box);
              }
            } else {
                box.getParent().getChildren().remove(box);
            }
            box.getParent().getLines().remove(box);
            if (box.getParent().getChildren().size() == 0) {
                box.getParent().isExpendable = false;
            }
        }
	}

    @Override
    public void undo() {
        HashMap<Box, Line> boxes1 = (HashMap<Box, Line>)before.get("boxes");
        for (Box b: boxes1.keySet()) {
            b.prepareDrawableShape();
            if (b.getParent() instanceof Root) {
                if (((Root) b.getParent()).getLeftChildren().size() == ((Root) b.getParent()).getLeftChildren().size()) {
                    ((Root) b.getParent()).getLeftChildren().add(b);
                } else {
                    ((Root) b.getParent()).getRightChildren().add(b);
                }
            } else {
                b.getParent().getChildren().add(b);
            }
            Line line = new Line(b.getParent().getLineStyle(),(int) b.getParent().getLineThickness().getValue(), new ColorDrawable(b.getParent().getLineColor()), new Point(), new Point(), true);
            if (b.position == Position.LFET) {
                line.setStart(new Point(b.getParent().getDrawableShape().getBounds().left, b.getParent().getDrawableShape().getBounds().centerY()));
                line.setEnd(new Point(b.getDrawableShape().getBounds().right, b.getDrawableShape().getBounds().centerY()));
            } else {
                line.setStart(new Point( b.getParent().getDrawableShape().getBounds().right, b.getParent().getDrawableShape().getBounds().centerY()));
                line.setEnd(new Point(b.getDrawableShape().getBounds().left, b.getDrawableShape().getBounds().centerY()));
            }
            b.getParent().getLines().put(b, line);
            b.getParent().isExpendable = true;
        }
    }

    @Override
    public void redo() {
        execute(before);
    }
}
