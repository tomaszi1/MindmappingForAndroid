package edu.agh.klaukold.commands;

import android.graphics.drawable.ColorDrawable;

import org.xmind.core.style.IStyle;
import org.xmind.ui.style.Styles;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;

import edu.agh.klaukold.common.Box;
import edu.agh.klaukold.common.Line;
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
                b.topic.getParent().add(b.topic);
            }
            IStyle s = MainActivity.workbook.getStyleSheet().findStyle(b.topic.getParent().getStyleId());

            //todo wyszstko napisac od nowa
//            Line line = new Line(b.getParent().getLineStyle(), Integer.parseInt(s.getProperty(Styles.LineWidth)), new ColorDrawable(b.getParent().getLineColor()), new Point(), new Point(), true);
//            if (b.position == Position.LFET) {
//                line.setStart(new Point(b.getParent().getDrawableShape().getBounds().left, b.getParent().getDrawableShape().getBounds().centerY()));
//                line.setEnd(new Point(b.getDrawableShape().getBounds().right, b.getDrawableShape().getBounds().centerY()));
//            } else {
//                line.setStart(new Point( b.getParent().getDrawableShape().getBounds().right, b.getParent().getDrawableShape().getBounds().centerY()));
//                line.setEnd(new Point(b.getDrawableShape().getBounds().left, b.getDrawableShape().getBounds().centerY()));
//            }
//            b.getParent().getLines().put(b, line);
//            b.getParent().isExpendable = true;
        }
    }

    @Override
    public void redo() {
        execute(after);
    }
}
