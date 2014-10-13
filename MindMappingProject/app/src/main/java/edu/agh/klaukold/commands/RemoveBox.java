package edu.agh.klaukold.commands;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;

import edu.agh.klaukold.common.Box;
import edu.agh.klaukold.common.Line;
import edu.agh.klaukold.common.Marker;
import edu.agh.klaukold.common.Note;
import edu.agh.klaukold.common.Point;
import edu.agh.klaukold.enums.Position;
import edu.agh.klaukold.interfaces.Command;

public class RemoveBox implements Command {
    Properties before;
    Properties after;
    public HashMap<Box, Line> boxes;

	@Override
	public void execute(Properties properties) {
		before = (Properties) properties.clone();
        after = (Properties) properties.clone();
        boxes = (HashMap<Box, Line>) properties.get("boxes");
        for (Box box : boxes.keySet()) {
            box.getParent().getChildren().remove(box);
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
            b.getParent().getChildren().add(b);
            if (b.position == Position.LFET) {
                b.getParent().getLines().get(b).setStart(new Point( b.getParent().getDrawableShape().getBounds().left, b.getParent().getDrawableShape().getBounds().centerY()));
            } else {
                b.getParent().getLines().get(b).setStart(new Point( b.getParent().getDrawableShape().getBounds().right, b.getParent().getDrawableShape().getBounds().centerY()));
            }

            b.getParent().getLines().put(b, b.getParent().getLines().get(b));
            b.getParent().isExpendable = true;
        }
    }

    @Override
    public void redo() {
        execute(before);
    }
}
