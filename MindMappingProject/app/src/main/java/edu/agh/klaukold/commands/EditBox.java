package edu.agh.klaukold.commands;

import java.util.LinkedList;
import java.util.Properties;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import org.xmind.core.internal.Style;
import org.xmind.core.style.IStyle;
import org.xmind.ui.style.Styles;

import edu.agh.klaukold.common.Box;
import edu.agh.klaukold.common.Text;
import edu.agh.klaukold.gui.MainActivity;
import edu.agh.klaukold.interfaces.Command;

public class EditBox implements Command{
    @Override
    public void redo() {
        execute(after);
    }

    public Box box;
    private Properties properties1;
    private Properties after;
    public LinkedList<Box> edited = new LinkedList<Box>();
    private LinkedList<Box> before = new LinkedList<Box>();
    @Override
    public void execute(Properties properties){
        properties1 = (Properties)properties.clone();
        after = (Properties)properties.clone();
        box = (Box) properties.get("box");
        IStyle style = MainActivity.workbook.getStyleSheet().findStyle(box.topic.getStyleId());
        if (properties.containsKey("boxes")) {
            edited = (LinkedList<Box>) properties.get("boxes");
            for (Box b : edited) {
                try {
                    before.add(b.BoxClone());
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            }
        }
        if (properties.containsKey("box_text")) {
            Text text = (Text) properties.get("box_text");
            try {
                 properties1.put("text", box.getText().TextClone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            box.setText(text);
            for (Box b : edited) {
                b.getText().setText(text);
            }
        } else if (properties.containsKey("color")) {
            properties1.put("color", style.getProperty(Styles.FillColor));
            ColorDrawable color = (ColorDrawable)properties.get("color");
            style.setProperty(Styles.FillColor, String.valueOf(color));
            for (Box b : edited) {
                IStyle s = MainActivity.workbook.getStyleSheet().findStyle(b.topic.getStyleId());
                s.setProperty(Styles.FillColor, String.valueOf(color));
            }
        } else if (properties.containsKey("shape")) {
            properties1.put("shape", box.getDrawableShape());
            String shape = (String)properties.get("shape");
            style.setProperty(Styles.ShapeClass, shape);
            MainActivity.changeShape(box);
            box.prepareDrawableShape();
            for (Box b : edited) {
                IStyle s = MainActivity.workbook.getStyleSheet().findStyle(b.topic.getStyleId());
                s.setProperty(Styles.ShapeClass, shape);
                MainActivity.changeShape(b);
                b.prepareDrawableShape();
            }
        } else if (properties.containsKey("line_color")) {
            properties1.put("line_color", new ColorDrawable(Integer.parseInt(style.getProperty(Styles.LineColor))));
            ColorDrawable color = (ColorDrawable)properties.get("line_color");
            style.setProperty(Styles.LineColor, String.valueOf(color.getColor()));
            for (Box b : box.getLines().keySet()) {
                box.getLines().get(b).setColor(color);
            }
            for (Box b : edited) {
                IStyle s = MainActivity.workbook.getStyleSheet().findStyle(b.topic.getStyleId());
                s.setProperty(Styles.LineColor, String.valueOf(color.getColor()));
                for (Box b1 : b.getLines().keySet()) {
                    b.getLines().get(b1).setColor(color);
                }
            }
        } else if (properties.containsKey("line_thickness")) {
            properties1.put("line_thickness", style.getProperty(Styles.LineWidth));
            int lt = (Integer) properties.get("line_thickness");
            style.setProperty(Styles.LineWidth, String.valueOf(lt));
            for (Box b : box.getLines().keySet()) {
                box.getLines().get(b).setThickness(lt);
            }
            for (Box b : edited) {
                IStyle s = MainActivity.workbook.getStyleSheet().findStyle(b.topic.getStyleId());
                s.setProperty(Styles.LineWidth, String.valueOf(lt));
                for (Box b1 : b.getLines().keySet()) {
                    box.getLines().get(b).setThickness(lt);
                }
            }
        } else if (properties.containsKey("line_shape")) {
            properties1.put("line_shape", style.getProperty(Styles.LineClass));
            String ls = (String) properties.get("line_shape");
            style.setProperty(Styles.LineClass, ls);
            for (Box b : box.getLines().keySet()) {
                IStyle s = MainActivity.workbook.getStyleSheet().findStyle(b.topic.getStyleId());
                s.setProperty(Styles.LineClass, ls);
            }
            for (Box b : edited) {
                IStyle s = MainActivity.workbook.getStyleSheet().findStyle(b.topic.getStyleId());
                s.setProperty(Styles.LineClass, ls);
                for (Box b1 : b.getLines().keySet()) {
                    IStyle s1 = MainActivity.workbook.getStyleSheet().findStyle(b1.topic.getStyleId());
                    s1.setProperty(Styles.LineClass, ls);
                }
            }
        }
    }

    @Override
    public void undo() {
        IStyle style = MainActivity.workbook.getStyleSheet().findStyle(box.topic.getStyleId());
        if (properties1.containsKey("text")) {
            Text text = (Text) properties1.get("text");
            box.setText(text);
        } else if (properties1.containsKey("color")) {
            ColorDrawable color = (ColorDrawable)properties1.get("color");
            style.setProperty(Styles.FillColor, String.valueOf(color));
        } else if  (properties1.containsKey("shape")) {
            Drawable shape = (Drawable)properties1.get("shape");
            box.setDrawableShape(shape);
            box.prepareDrawableShape();
        } else if (properties1.containsKey("line_color")) {
            int color = ((ColorDrawable) properties1.get("line_color")).getColor();
           // box.setLineColor(color);
            style.setProperty(Styles.LineColor, String.valueOf(color));
        } else if (properties1.containsKey("line_thickness")) {
            int lt = (Integer) properties1.get("line_thickness");
            style.setProperty(Styles.LineWidth, String.valueOf(lt));
        }
        if (edited.size() != 0) {
            int i =0;
            for (Box b : edited) {
                b.updateBox(before.get(i));
                i++;
            }
        }
    }



}
