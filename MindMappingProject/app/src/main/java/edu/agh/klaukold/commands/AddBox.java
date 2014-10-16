package edu.agh.klaukold.commands;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;

import java.util.Properties;

import edu.agh.R;
import edu.agh.klaukold.common.Box;
import edu.agh.klaukold.common.Line;
import edu.agh.klaukold.common.Marker;
import edu.agh.klaukold.common.Note;
import edu.agh.klaukold.common.Point;
import edu.agh.klaukold.common.Root;
import edu.agh.klaukold.common.Text;
import edu.agh.klaukold.enums.Align;
import edu.agh.klaukold.enums.BlockShape;
import edu.agh.klaukold.enums.LineStyle;
import edu.agh.klaukold.enums.LineThickness;
import edu.agh.klaukold.enums.Position;
import edu.agh.klaukold.interfaces.Command;

public class AddBox implements Command {
    public Box box;
    Properties before;
    Properties after;
    Box parent;
    Line line;
    Root root;

	@Override
	public void execute(Properties properties) {
        before = (Properties)properties.clone();
        after = (Properties)properties.clone();
        parent = (Box)properties.get("box");
        box = (Box) properties.get("new_box");
        root = (Root) properties.get("root");
        String style = (String) properties.get("style");
        Resources res = (Resources) properties.get("res");
        Position position = Position.LFET;
		// TODO Auto-generated method stub
        if (parent instanceof Root) {
            if (((Root)parent).getLeftChildren().size() == ((Root)parent).getRightChildren().size()) {
                ((Root)parent).getLeftChildren().add(box);
                position = Position.LFET;
            } else {
                ((Root)parent).getRightChildren().add(box);
                position = Position.RIGHT;
            }
        } else {
            parent.addChild(box);
            position = parent.position;
        }
        //todo tylko probne
        box.setParent(parent);
        box.setHeight(root.getHeight() - 10);
        if (position == Position.RIGHT) {
            box.setPoint(new edu.agh.klaukold.common.Point(parent.getDrawableShape().getBounds().right + 30, parent.getDrawableShape().getBounds().top));
        } else {
            box.setPoint(new edu.agh.klaukold.common.Point(parent.getDrawableShape().getBounds().left - box.getWidth() - 10, parent.getDrawableShape().getBounds().top));
        }
        //todo inne style
        if (style.equals("Default")) {
            Text text = new Text();
            text.setAlign(Align.CENTER);
            text.setColor(new ColorDrawable(Color.BLACK));
            text.setSize(13);
            box.setShape(BlockShape.ROUNDED_RECTANGLE);
            int color = res.getColor(R.color.light_blue);
            box.setColor(new ColorDrawable(color));
            box.setText(text);
            box.setDrawableShape((GradientDrawable) res.getDrawable(R.drawable.round_rect));
            box.setLineStyle(LineStyle.STRAIGHT);
            box.setLineColor(Color.rgb(128, 128, 128));
            box.setLineThickness(LineThickness.THINNEST);
            box.position = position;
            box.prepareDrawableShape();
            if (position == Position.RIGHT) {
                line = new Line(parent.getLineStyle(), (int) parent.getLineThickness().getValue(), new ColorDrawable(parent.getLineColor()),
                        new Point(parent.getDrawableShape().getBounds().right,
                                parent.getDrawableShape().getBounds().top +  (parent.getDrawableShape().getBounds().bottom - parent.getDrawableShape().getBounds().top) / 2),
                        new Point(box.getDrawableShape().getBounds().left,
                                box.getDrawableShape().getBounds().top + (box.getDrawableShape().getBounds().bottom - box.getDrawableShape().getBounds().top) / 2), true);
            } else {
                line = new Line(parent.getLineStyle(), (int) parent.getLineThickness().getValue(), new ColorDrawable(parent.getLineColor()),
                        new Point(parent.getDrawableShape().getBounds().left,
                                parent.getDrawableShape().getBounds().top + (parent.getDrawableShape().getBounds().bottom - parent.getDrawableShape().getBounds().top) / 2),
                        new Point(box.getDrawableShape().getBounds().right,
                                box.getDrawableShape().getBounds().top +    (box.getDrawableShape().getBounds().bottom - box.getDrawableShape().getBounds().top) / 2), true);
            }
            line.position = box.position;
            parent.getLines().put(box,line);
        }
        parent.setSelected(false);
        parent.setExpanded(true);
        parent.isExpendable = true;
	}

    @Override
    public void undo() {
          if   ( root.getLeftChildren().contains(box)) {
              root.getLeftChildren().remove(box);
              root.getLines().remove(box);
          } else if   ( root.getRightChildren().contains(box)) {
              root.getRightChildren().remove(box);
              root.getLines().remove(box);
          }
         else {
            parent.getChildren().remove(box);
            parent.getLines().remove(box);
        }
        if (root.getRightChildren().size() == 0 && root.getLeftChildren().size() == 0) {
            root.isExpendable = false;
        }
        if (parent.getChildren().size() == 0) {
            parent.isExpendable = false;
        }
    }

    @Override
    public void redo() {
        execute(after);
    }

}
