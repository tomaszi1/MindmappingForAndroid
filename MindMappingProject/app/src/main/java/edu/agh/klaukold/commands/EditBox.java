package edu.agh.klaukold.commands;

import java.util.LinkedList;
import java.util.Properties;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import org.xmind.core.internal.Style;
import org.xmind.core.style.IStyle;
import org.xmind.core.style.IStyleSheet;
import org.xmind.ui.style.Styles;

import edu.agh.R;
import edu.agh.klaukold.common.Box;
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
        if (style == null) {
            style = MainActivity.workbook.getStyleSheet().createStyle(IStyle.TOPIC);
            box.topic.setStyleId(style.getId());
            MainActivity.styleSheet.addStyle(style, IStyleSheet.NORMAL_STYLES);
        }
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
        if (properties.containsKey("text_size")) {
            if (style.getProperty(Styles.FontSize) != null) {
                properties1.put("text_size", style.getProperty(Styles.FontSize));
            } else {
                properties1.put("text_size", Typeface.DEFAULT);
            }
            style.setProperty(Styles.FontSize, (String)properties.getProperty("text_size"));
        } else if (properties.containsKey("bold")) {
            properties1.put("bold", (String)properties.get("bold"));
            if (((String)properties.get("bold")).equals("true")) {
                style.setProperty(Styles.FontStyle, Styles.FONT_WEIGHT_BOLD);
                properties1.put("text_size", false);
            } else  {
                style.setProperty(Styles.FontStyle, "");
                properties1.put("text_size", true);
            }
        }
        else if (properties.containsKey("italic")) {

            if (((String)properties.get("italic")).equals("true")) {
                style.setProperty(Styles.FontStyle, Styles.FONT_STYLE_ITALIC);
                properties1.put("italic", "false");
            } else  {
                style.setProperty(Styles.FontStyle, "");
                properties1.put("italic", "true");
            }
        }  else if (properties.containsKey("strikeout")) {
            if (((String)properties.get("strikeout")).equals("true")) {
                style.setProperty(Styles.TextDecoration, Styles.TEXT_DECORATION_LINE_THROUGH);
                properties1.put("strikeout", "false");
            } else  {
                style.setProperty(Styles.TextDecoration, "");
                properties1.put("strikeout", true);
            }
        } else if (properties.containsKey("align")) {
            if (style.getProperty(Styles.TextAlign) != null) {
                properties1.put("align", style.getProperty(Styles.TextAlign));
            } else {
                properties1.put("align", Styles.ALIGN_CENTER);
            }
            style.setProperty(Styles.TextAlign, (String)properties.getProperty("align"));
        } else if (properties.containsKey("font")) {
            if (style.getProperty(Styles.FontFamily) != null) {
                properties1.put("font", style.getProperty(Styles.FontFamily));
            } else {
                properties1.put("font", "Times New Roman");
            }
            style.setProperty(Styles.FontFamily, (String)properties.get("font"));
        }
        else if (properties.containsKey("text_color")) {
            if (style.getProperty(Styles.TextColor) != null) {
                properties1.put("text_color", style.getProperty(Styles.TextColor));
            } else {
                properties1.put("text_color", Color.BLACK);
            }
            String color = (String)properties.get("text_color");
            style.setProperty(Styles.TextColor, String.valueOf(color));
            for (Box b : edited) {
                IStyle s = MainActivity.workbook.getStyleSheet().findStyle(b.topic.getStyleId());
                s.setProperty(Styles.TextColor, String.valueOf(color));
            }
        }
        else if (properties.containsKey("box_color")) {
            if (style.getProperty(Styles.FillColor) != null) {
                properties1.put("box_color", style.getProperty(Styles.FillColor));
            } else {
                properties1.put("box_color", MainActivity.res.getColor(R.color.light_blue));
            }
            String color = (String)properties.get("box_color");
            style.setProperty(Styles.FillColor,  Integer.toString(Integer.parseInt(color), 16) );
            for (Box b : edited) {
                IStyle s = MainActivity.workbook.getStyleSheet().findStyle(b.topic.getStyleId());
                s.setProperty(Styles.FillColor, String.valueOf(color));
            }
        } else if (properties.containsKey("box_text")) {
            properties1.put("box_text", box.topic.getTitleText());
            box.topic.setTitleText(properties.getProperty("box_text"));
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
            if (style.getProperty(Styles.LineColor) != null) {
                properties1.put("line_color", new ColorDrawable(Integer.parseInt(style.getProperty(Styles.LineColor))));
            } else {
                properties1.put("line_color", new ColorDrawable(Color.GRAY));
            }
            String color = (String)properties.get("line_color");
            style.setProperty(Styles.LineColor, color);
            for (Box b : box.getLines().keySet()) {
                box.getLines().get(b).setColor(new ColorDrawable(Integer.parseInt(color)));
            }
            for (Box b : edited) {
                IStyle s = MainActivity.workbook.getStyleSheet().findStyle(b.topic.getStyleId());
                s.setProperty(Styles.LineColor, color);
                for (Box b1 : b.getLines().keySet()) {
                  b.getLines().get(b1).setColor(new ColorDrawable(Integer.parseInt(color)));
                }
            }
        } else if (properties.containsKey("line_thickness")) {
            if (style.getProperty(Styles.LineWidth) != null) {
                properties1.put("line_thickness", style.getProperty(Styles.LineWidth));
            } else {
                properties1.put("line_thickness", "1dt");
            }
            String lt = (String) properties.get("line_thickness");
            style.setProperty(Styles.LineWidth, String.valueOf(lt));
            for (Box b : box.getLines().keySet()) {
                box.getLines().get(b).setThickness(Integer.parseInt(lt.substring(0, lt.length() - 2)));
            }
            for (Box b : edited) {
                IStyle s = MainActivity.workbook.getStyleSheet().findStyle(b.topic.getStyleId());
                s.setProperty(Styles.LineWidth, String.valueOf(lt));
                for (Box b1 : b.getLines().keySet()) {
                    b.getLines().get(b1).setThickness(Integer.parseInt(lt.substring(0, lt.length() - 2)));
                }
            }
        } else if (properties.containsKey("line_shape")) {
            if (style.getProperty(Styles.LineClass) != null) {
                properties1.put("line_shape", style.getProperty(Styles.LineClass));
            } else {
                properties1.put("line_shape", "");
            }
            String ls = (String) properties.get("line_shape");
            style.setProperty(Styles.LineClass, ls);
            for (Box b : box.getLines().keySet()) {
                IStyle s = MainActivity.workbook.getStyleSheet().findStyle(b.topic.getStyleId());
                if (s == null) {
                    s = MainActivity.styleSheet.createStyle(IStyle.TOPIC);
                    MainActivity.styleSheet.addStyle(s, IStyleSheet.NORMAL_STYLES);
                }
                s.setProperty(Styles.LineClass, ls);
            }
            for (Box b : edited) {
                IStyle s = MainActivity.workbook.getStyleSheet().findStyle(b.topic.getStyleId());
                s.setProperty(Styles.LineClass, ls);
                for (Box b1 : b.getLines().keySet()) {
                    IStyle s1 = MainActivity.workbook.getStyleSheet().findStyle(b1.topic.getStyleId());
                    if (s1 == null) {
                        s1 = MainActivity.styleSheet.createStyle(IStyle.TOPIC);
                        MainActivity.styleSheet.addStyle(s1, IStyleSheet.NORMAL_STYLES);
                    }
                    s1.setProperty(Styles.LineClass, ls);
                }
            }
        }
    }

    @Override
    public void undo() {
        IStyle style = MainActivity.workbook.getStyleSheet().findStyle(box.topic.getStyleId());
         if (properties1.containsKey("box_color")) {
            String color = (String)properties1.get("box_color");
            style.setProperty(Styles.FillColor, color);
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
        } else if (properties1.containsKey("text_size")) {
            style.setProperty(Styles.FontSize, (String) properties1.get("text_size"));
        }
         else if (properties1.containsKey("bold")) {
             if (((String)properties1.get("bold")).equals("true")) {
                 style.setProperty(Styles.FontStyle, Styles.FONT_WEIGHT_BOLD);
             } else  {
                 style.setProperty(Styles.FontStyle, "");
             }
         }
         else if (properties1.containsKey("italic")) {
             if (((String)properties1.get("italic")).equals("true")) {
                 style.setProperty(Styles.FontStyle, Styles.FONT_WEIGHT_BOLD);
             } else  {
                 style.setProperty(Styles.FontStyle, "");
             }
         } else if (properties1.containsKey("strikeout")) {
             if (((String)properties1.get("strikeout")).equals("true")) {
                 style.setProperty(Styles.TextDecoration, Styles.TEXT_DECORATION_LINE_THROUGH);
             } else  {
                 style.setProperty(Styles.TextDecoration, "");
             }
         } else if (properties1.containsKey("font")) {
             style.setProperty(Styles.FontFamily, (String)properties1.get("font"));
         } else if (properties1.containsKey("align")) {
             style.setProperty(Styles.TextAlign, (String)properties1.get("align"));
         } else if (properties1.containsKey("box_text")) {
             box.topic.setTitleText(properties1.getProperty("box_text"));
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
