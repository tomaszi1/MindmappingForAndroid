package edu.agh.klaukold.commands;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;

import org.xmind.core.ITopic;
import org.xmind.core.ITopicPath;
import org.xmind.core.style.IStyle;
import org.xmind.core.style.IStyleSheet;
import org.xmind.ui.style.Styles;

import java.util.Properties;

import edu.agh.R;
import edu.agh.klaukold.common.Box;
import edu.agh.klaukold.common.Line;
import edu.agh.klaukold.common.Point;
import edu.agh.klaukold.common.Root;
import edu.agh.klaukold.enums.Position;
import edu.agh.klaukold.gui.MainActivity;
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
        box.parent = parent;
        ITopic topic = MainActivity.workbook.createTopic();
        IStyleSheet styleSheet = MainActivity.workbook.getStyleSheet();
        // Tworzymy styl dla topica
        IStyle topicStyle = styleSheet.createStyle(IStyle.TOPIC);
        topic.setStyleId(topicStyle.getId());
        box.topic = topic;
        parent.topic.add(box.topic);
     //   root = (Root) properties.get("root");
        String style = (String) properties.get("style");
        Resources res = (Resources) properties.get("res");
        Position position = Position.LFET;
		// TODO Auto-generated method stub
        if (parent.topic.isRoot()) {
            if (parent.topic.getChildren(ITopic.ATTACHED).size()%2 == 0) {
                position = Position.RIGHT;
            } else {
                position = Position.LFET;
            }
        } else {
            position = parent.position;
        }
        parent.addChild(box);
        box.setHeight(100);
        if (position == Position.RIGHT) {
            box.setPoint(new edu.agh.klaukold.common.Point(parent.getDrawableShape().getBounds().right + 30, parent.getDrawableShape().getBounds().top));
        } else {
            box.setPoint(new edu.agh.klaukold.common.Point(parent.getDrawableShape().getBounds().left - box.getWidth() - 10, parent.getDrawableShape().getBounds().top));
        }
        topicStyle.setProperty(Styles.FontFamily, "Times New Roman");
        if (parent.topic.isRoot() && style.equals("Default")) {
            topicStyle.setProperty(Styles.FontSize, "13pt");
            topicStyle.setProperty(Styles.TextColor, String.valueOf(Color.BLACK));
            topicStyle.setProperty(Styles.TextAlign, Styles.ALIGN_CENTER);
            topicStyle.setProperty(Styles.ShapeClass, Styles.TOPIC_SHAPE_ROUNDEDRECT);
            int color = res.getColor(R.color.white);
            topicStyle.setProperty(Styles.FillColor, String.valueOf(color));
            box.setDrawableShape((GradientDrawable) res.getDrawable(R.drawable.round_rect));
            topicStyle.setProperty(Styles.LineClass, Styles.BRANCH_CONN_STRAIGHT);
            topicStyle.setProperty(Styles.LineColor, String.valueOf(Color.rgb(128, 128, 128)));
            topicStyle.setProperty(Styles.LineWidth, "1pt");
        } else if (parent.topic.isRoot() && style.equals("Classic")) {
            topicStyle.setProperty(Styles.FontSize, "13pt");
            topicStyle.setProperty(Styles.TextColor, String.valueOf(Color.BLACK));
            topicStyle.setProperty(Styles.TextAlign, Styles.ALIGN_CENTER);
            topicStyle.setProperty(Styles.ShapeClass, Styles.TOPIC_SHAPE_ROUNDEDRECT);
            topicStyle.setProperty(Styles.LineClass, Styles.BRANCH_CONN_STRAIGHT);
            topicStyle.setProperty(Styles.LineColor, String.valueOf(Color.rgb(128, 128, 128)));
            topicStyle.setProperty(Styles.LineWidth, "1pt");
            box.setDrawableShape((GradientDrawable) res.getDrawable(R.drawable.round_rect));
        } else if (!parent.topic.isRoot() || style.equals("Simple")) {
            topicStyle.setProperty(Styles.FontSize, "13pt");
            topicStyle.setProperty(Styles.TextColor, String.valueOf(Color.BLACK));
            topicStyle.setProperty(Styles.TextAlign, Styles.ALIGN_CENTER);
            topicStyle.setProperty(Styles.FillColor, String.valueOf(Color.WHITE));
            topicStyle.setProperty(Styles.ShapeClass, Styles.TOPIC_SHAPE_UNDERLINE);
            box.setDrawableShape((GradientDrawable) res.getDrawable(R.drawable.underline));
            topicStyle.setProperty(Styles.LineClass, Styles.BRANCH_CONN_STRAIGHT);
            topicStyle.setProperty(Styles.LineColor, String.valueOf(Color.rgb(128, 128, 128)));
            topicStyle.setProperty(Styles.LineWidth, "1pt");
        } else if (parent.topic.isRoot() && style.equals("Business")) {
            topicStyle.setProperty(Styles.FontSize, "13pt");
            topicStyle.setProperty(Styles.TextColor, String.valueOf(Color.BLACK));
            topicStyle.setProperty(Styles.TextAlign, Styles.ALIGN_CENTER);
            topicStyle.setProperty(Styles.ShapeClass, Styles.TOPIC_SHAPE_ROUNDEDRECT);
            int color = res.getColor(R.color.white);
            topicStyle.setProperty(Styles.FillColor, String.valueOf(color));
            box.setDrawableShape((GradientDrawable) res.getDrawable(R.drawable.rect));
            topicStyle.setProperty(Styles.LineClass, Styles.BRANCH_CONN_STRAIGHT);
            topicStyle.setProperty(Styles.LineColor, String.valueOf(Color.rgb(128, 128, 128)));
            topicStyle.setProperty(Styles.LineWidth, "1pt");
        } else if (parent.topic.isRoot() && style.equals("Academese")) {
            topicStyle.setProperty(Styles.FontSize, "13pt");
            topicStyle.setProperty(Styles.TextColor, String.valueOf(Color.BLACK));
            topicStyle.setProperty(Styles.TextAlign, Styles.ALIGN_CENTER);
            topicStyle.setProperty(Styles.ShapeClass, Styles.TOPIC_SHAPE_ELLIPSE);
            int color = res.getColor(R.color.dark_gray);
            topicStyle.setProperty(Styles.FillColor, String.valueOf(color));
            box.setDrawableShape((GradientDrawable) res.getDrawable(R.drawable.elipse));
            topicStyle.setProperty(Styles.LineClass, Styles.BRANCH_CONN_STRAIGHT);
            topicStyle.setProperty(Styles.LineColor, String.valueOf(Color.rgb(128, 128, 128)));
            topicStyle.setProperty(Styles.LineWidth, "1pt");
        }
        box.position = position;
        box.prepareDrawableShape();
        IStyle partentStyle = MainActivity.workbook.getStyleSheet().findStyle(parent.topic.getStyleId());
        if (position == Position.RIGHT) {
            line = new Line(partentStyle.getProperty(Styles.LineClass), Integer.parseInt(partentStyle.getProperty(Styles.LineWidth).substring(0,partentStyle.getProperty(Styles.LineWidth).length() - 2)), new ColorDrawable(Integer.parseInt(partentStyle.getProperty(Styles.LineColor))),
                    new Point(parent.getDrawableShape().getBounds().right,
                            parent.getDrawableShape().getBounds().top + (parent.getDrawableShape().getBounds().bottom - parent.getDrawableShape().getBounds().top) / 2),
                    new Point(box.getDrawableShape().getBounds().left,
                            box.getDrawableShape().getBounds().top + (box.getDrawableShape().getBounds().bottom - box.getDrawableShape().getBounds().top) / 2), true);
        } else {
            line = new Line(partentStyle.getProperty(Styles.LineClass), Integer.parseInt(partentStyle.getProperty(Styles.LineWidth).substring(0,partentStyle.getProperty(Styles.LineWidth).length() - 2)), new ColorDrawable(Integer.parseInt(partentStyle.getProperty(Styles.LineColor))),
                    new Point(parent.getDrawableShape().getBounds().left,
                            parent.getDrawableShape().getBounds().top + (parent.getDrawableShape().getBounds().bottom - parent.getDrawableShape().getBounds().top) / 2),
                    new Point(box.getDrawableShape().getBounds().right,
                            box.getDrawableShape().getBounds().top + (box.getDrawableShape().getBounds().bottom - box.getDrawableShape().getBounds().top) / 2), true);
        }
        line.position = box.position;
        parent.getLines().put(box, line);
//=======
//            box.setLineStyle(LineStyle.STRAIGHT);
//            box.setLineColor(Color.rgb(128, 128, 128));
//            box.setLineThickness(LineThickness.THINNEST);
//            box.position = position;
//            box.prepareDrawableShape();
//            if (position == Position.RIGHT) {
//                line = new Line(parent.getLineStyle(), (int) parent.getLineThickness().getValue(), new ColorDrawable(parent.getLineColor()),
//                        new Point(parent.getDrawableShape().getBounds().right,
//                                parent.getDrawableShape().getBounds().top +  (parent.getDrawableShape().getBounds().bottom - parent.getDrawableShape().getBounds().top) / 2),
//                        new Point(box.getDrawableShape().getBounds().left,
//                                box.getDrawableShape().getBounds().top + (box.getDrawableShape().getBounds().bottom - box.getDrawableShape().getBounds().top) / 2), true);
//            } else {
//                line = new Line(parent.getLineStyle(), (int) parent.getLineThickness().getValue(), new ColorDrawable(parent.getLineColor()),
//                        new Point(parent.getDrawableShape().getBounds().left,
//                                parent.getDrawableShape().getBounds().top + (parent.getDrawableShape().getBounds().bottom - parent.getDrawableShape().getBounds().top) / 2),
//                        new Point(box.getDrawableShape().getBounds().right,
//                                box.getDrawableShape().getBounds().top +    (box.getDrawableShape().getBounds().bottom - box.getDrawableShape().getBounds().top) / 2), true);
//            }
//            line.position = box.position;
//            parent.getLines().put(box,line);
//        }
//        parent.setSelected(false);
//>>>>>>> dfef7fd21da727269753511592462b2ad0d98f65
//        parent.setExpanded(true);
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
