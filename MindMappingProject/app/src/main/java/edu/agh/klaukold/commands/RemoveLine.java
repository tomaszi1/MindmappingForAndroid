package edu.agh.klaukold.commands;

import java.util.Properties;

import edu.agh.klaukold.common.Box;
import edu.agh.klaukold.common.Line;
import edu.agh.klaukold.common.Point;
import edu.agh.klaukold.enums.Position;
import edu.agh.klaukold.interfaces.Command;


public class RemoveLine implements Command {
    public Box box;
    Properties properties1;
    Properties after;
    Line line;
    @Override
    public void redo() {
            execute(after);
    }

    @Override
	public void execute(Properties properties) {
		properties1 =  (Properties)properties.clone();
        after = properties;
		box = (Box) properties.get("box");
        //todo napisac wszystko od nowa
      //  Box parent = box.getParent();
       // try {
        //    line = parent.getLines().get(box).Clone();
        //} catch (CloneNotSupportedException e) {
        //    e.printStackTrace();
       // }
       // parent.getLines().remove(box);
       // properties1.put("parent", parent);
	}

    @Override
    public void undo() {
        Box parent = (Box)properties1.get("parent");
        if (box.position == Position.LFET) {
            line.setStart(new Point(parent.getDrawableShape().getBounds().left, parent.getDrawableShape().getBounds().centerY()));
            line.setEnd(new Point(box.getDrawableShape().getBounds().right, box.getDrawableShape().getBounds().centerY()));
        } else {
            line.setStart(new Point(parent.getDrawableShape().getBounds().right, parent.getDrawableShape().getBounds().centerY()));
            line.setEnd(new Point(box.getDrawableShape().getBounds().left, box.getDrawableShape().getBounds().centerY()));
        }
        parent.getLines().put(box, line);
    }


}
