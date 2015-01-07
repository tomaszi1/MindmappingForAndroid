package edu.agh.klaukold.commands;

import java.util.LinkedList;
import java.util.Properties;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

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
        box.calculate = false;
        IStyle style = MainActivity.workbook.getStyleSheet().findStyle(box.topic.getStyleId());
        if (style == null) {
            style = MainActivity.workbook.getStyleSheet().createStyle(IStyle.TOPIC);
            box.topic.setStyleId(style.getId());
            MainActivity.styleSheet.addStyle(style, IStyleSheet.NORMAL_STYLES);
        }
        if (properties.containsKey("boxes")) {
            edited = (LinkedList<Box>) properties.get("boxes");
            for (Box b : edited) {
                IStyle style1 = MainActivity.workbook.getStyleSheet().findStyle(b.topic.getStyleId());
                if (style1 == null) {
                    style1 = MainActivity.workbook.getStyleSheet().createStyle(IStyle.TOPIC);
                    b.topic.setStyleId(style1.getId());
                    MainActivity.styleSheet.addStyle(style1, IStyleSheet.NORMAL_STYLES);
                    b.calculate = false;
            }
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
                properties1.put("text_size", "8dt");
            }
            style.setProperty(Styles.FontSize, (String)properties.getProperty("text_size"));
            for (Box b : edited) {
                MainActivity.workbook.getStyleSheet().findStyle(b.topic.getStyleId()).setProperty(Styles.FontSize, (String) properties.getProperty("text_size"));
            }
        } else if (properties.containsKey("bold")) {
            properties1.put("bold", (String)properties.get("bold"));
            if (((String)properties.get("bold")).equals("true")) {
                style.setProperty(Styles.FontStyle, Styles.FONT_WEIGHT_BOLD);
                properties1.put("bold", "false");
                for (Box b : edited) {
                    MainActivity.workbook.getStyleSheet().findStyle(b.topic.getStyleId()).setProperty(Styles.FontStyle, Styles.FONT_WEIGHT_BOLD);
                }
            } else  {
                style.setProperty(Styles.FontStyle, "");
                properties1.put("bold", "true");
                for (Box b : edited) {
                    MainActivity.workbook.getStyleSheet().findStyle(b.topic.getStyleId()).setProperty(Styles.FontStyle, "");
                }
            }

        }
        else if (properties.containsKey("italic")) {

            if (((String)properties.get("italic")).equals("true")) {
                style.setProperty(Styles.FontStyle, Styles.FONT_STYLE_ITALIC);
                properties1.put("italic", "false");
                for (Box b : edited) {
                    MainActivity.workbook.getStyleSheet().findStyle(b.topic.getStyleId()).setProperty(Styles.FontStyle, Styles.FONT_STYLE_ITALIC);
                }
            } else  {
                style.setProperty(Styles.FontStyle, "");
                properties1.put("italic", "true");
                for (Box b : edited) {
                    MainActivity.workbook.getStyleSheet().findStyle(b.topic.getStyleId()).setProperty(Styles.FontStyle, "");
                }
            }
        }  else if (properties.containsKey("strikeout")) {
            if (((String)properties.get("strikeout")).equals("true")) {
                style.setProperty(Styles.TextDecoration, Styles.TEXT_DECORATION_LINE_THROUGH);
                properties1.put("strikeout", "false");
                for (Box b : edited) {
                    MainActivity.workbook.getStyleSheet().findStyle(b.topic.getStyleId()).setProperty(Styles.TextDecoration, Styles.TEXT_DECORATION_LINE_THROUGH);
                }
            } else  {
                style.setProperty(Styles.TextDecoration, "");
                properties1.put("strikeout", true);
                for (Box b : edited) {
                    MainActivity.workbook.getStyleSheet().findStyle(b.topic.getStyleId()).setProperty(Styles.TextDecoration, "");
                }
            }
        } else if (properties.containsKey("align")) {
            if (style.getProperty(Styles.TextAlign) != null) {
                properties1.put("align", style.getProperty(Styles.TextAlign));
            } else {
                properties1.put("align", Styles.ALIGN_CENTER);
            }
            style.setProperty(Styles.TextAlign, (String)properties.get("align"));
            for (Box b : edited) {
                MainActivity.workbook.getStyleSheet().findStyle(b.topic.getStyleId()).setProperty(Styles.TextAlign, (String) properties.get("align"));
            }
        } else if (properties.containsKey("font")) {
            if (style.getProperty(Styles.FontFamily) != null) {
                properties1.put("font", style.getProperty(Styles.FontFamily));
            } else {
                properties1.put("font", "Times New Roman");
            }
            style.setProperty(Styles.FontFamily, (String)properties.get("font"));
            for (Box b : edited) {
                MainActivity.workbook.getStyleSheet().findStyle(b.topic.getStyleId()).setProperty(Styles.FontFamily, (String) properties.get("font"));
            }
        }
        else if (properties.containsKey("text_color")) {
            if (style.getProperty(Styles.TextColor) != null) {
                properties1.put("text_color", style.getProperty(Styles.TextColor));
            } else {
                properties1.put("text_color", "#000000");
            }
            String color = (String)properties.get("text_color");
            String colorHex = "#" + (!Integer.toString(Integer.valueOf(Color.red(Integer.parseInt(color))), 16).equals("0") ? Integer.toString(Integer.valueOf(Color.red(Integer.parseInt(color))), 16) : "00")
                    + (!Integer.toString(Integer.valueOf(Color.green(Integer.parseInt(color))), 16).equals("0") ? Integer.toString(Integer.valueOf(Color.green(Integer.parseInt(color))), 16) : "00" )
                    + (!Integer.toString(Integer.valueOf(Color.blue(Integer.parseInt(color))), 16).equals("0") ? Integer.toString(Integer.valueOf(Color.blue(Integer.parseInt(color))), 16) : "00" );
            style.setProperty(Styles.TextColor, String.valueOf(colorHex));
            for (Box b : edited) {
                IStyle s = MainActivity.workbook.getStyleSheet().findStyle(b.topic.getStyleId());
                s.setProperty(Styles.TextColor, String.valueOf(colorHex));
            }
        }
        else if (properties.containsKey("box_color")) {
            if (style.getProperty(Styles.FillColor) != null) {
                properties1.put("box_color", style.getProperty(Styles.FillColor));
            } else {
                properties1.put("box_color", "#CCE5FF");
            }
            String color = ((String)properties.get("box_color"));
            String colorHex = "#" + (!Integer.toString(Integer.valueOf(Color.red(Integer.parseInt(color))), 16).equals("0") ? Integer.toString(Integer.valueOf(Color.red(Integer.parseInt(color))), 16) : "00")
                    + (!Integer.toString(Integer.valueOf(Color.green(Integer.parseInt(color))), 16).equals("0") ? Integer.toString(Integer.valueOf(Color.green(Integer.parseInt(color))), 16) : "00" )
                    + (!Integer.toString(Integer.valueOf(Color.blue(Integer.parseInt(color))), 16).equals("0") ? Integer.toString(Integer.valueOf(Color.blue(Integer.parseInt(color))), 16) : "00" );
                    //.replace("-", "#");
            Log.w("colorHex", colorHex);
            Log.w("colorint", color);
            style.setProperty(Styles.FillColor,  colorHex);
            for (Box b : edited) {
                IStyle s = MainActivity.workbook.getStyleSheet().findStyle(b.topic.getStyleId());
                s.setProperty(Styles.FillColor, String.valueOf(colorHex));
            }
        } else if (properties.containsKey("box_text")) {
            properties1.put("box_text", box.topic.getTitleText());
            box.topic.setTitleText(properties.get("box_text").toString());
            for (Box b : edited) {
                b.topic.setTitleText(properties.get("box_text").toString());
            }
        } else if (properties.containsKey("shape")) {
            properties1.put("shape", box.getDrawableShape());
            if (style.getProperty(Styles.ShapeClass) != null) {
                properties1.put("shape_name", style.getProperty(Styles.ShapeClass));
            } else {
                properties1.put("shape_name", Styles.TOPIC_SHAPE_ROUNDEDRECT);
            }
            String shape = (String)properties.get("shape");
            style.setProperty(Styles.ShapeClass, shape);
            MainActivity.changeShape(box);
            box.prepareDrawableShape();style.setProperty(Styles.ShapeClass, shape);
            for (Box b : edited) {
                IStyle s = MainActivity.workbook.getStyleSheet().findStyle(b.topic.getStyleId());
                s.setProperty(Styles.ShapeClass, shape);
                MainActivity.changeShape(b);
                b.prepareDrawableShape();
            }
        } else if (properties.containsKey("line_color")) {
            if (style.getProperty(Styles.LineColor) != null) {
                properties1.put("line_color", new ColorDrawable(Color.parseColor(style.getProperty(Styles.LineColor))));
            } else {
                properties1.put("line_color", new ColorDrawable(Color.GRAY));
            }
            String color = (String)properties.get("line_color");
            String colorHex =  "#" + (!Integer.toString(Integer.valueOf(Color.red(Integer.parseInt(color))), 16).equals("0") ? Integer.toString(Integer.valueOf(Color.red(Integer.parseInt(color))), 16) : "00")
                    + (!Integer.toString(Integer.valueOf(Color.green(Integer.parseInt(color))), 16).equals("0") ? Integer.toString(Integer.valueOf(Color.green(Integer.parseInt(color))), 16) : "00" )
                    + (!Integer.toString(Integer.valueOf(Color.blue(Integer.parseInt(color))), 16).equals("0") ? Integer.toString(Integer.valueOf(Color.blue(Integer.parseInt(color))), 16) : "00" );
            style.setProperty(Styles.LineColor, colorHex);
            for (Box b : box.getLines().keySet()) {
                box.getLines().get(b).setColor(new ColorDrawable(Integer.parseInt(color)));
            }
            for (Box b : edited) {
                IStyle s = MainActivity.workbook.getStyleSheet().findStyle(b.topic.getStyleId());
                s.setProperty(Styles.LineColor, colorHex);
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
                properties1.put("line_shape", Styles.BRANCH_CONN_STRAIGHT);
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
                box.getLines().get(b).shape =  (String) properties.get("line_shape");
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
                    b.getLines().get(b1).shape = (String) properties.get("line_shape");
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
            style.setProperty(Styles.ShapeClass, (String)properties1.getProperty("shape_name"));
            box.prepareDrawableShape();
        } else if (properties1.containsKey("line_color")) {
            int color = ((ColorDrawable) properties1.get("line_color")).getColor();
             String colorHex =  "#" + (!Integer.toString(Integer.valueOf(Color.red(color)), 16).equals("0") ? Integer.toString(Integer.valueOf(Color.red(color)), 16) : "00")
                     + (!Integer.toString(Integer.valueOf(Color.green(color)), 16).equals("0") ? Integer.toString(Integer.valueOf(Color.green(color)), 16) : "00" )
                     + (!Integer.toString(Integer.valueOf(Color.blue(color)), 16).equals("0") ? Integer.toString(Integer.valueOf(Color.blue(color)), 16) : "00" );
            style.setProperty(Styles.LineColor, String.valueOf(colorHex));
        } else if (properties1.containsKey("line_thickness")) {
            String lt = (String) properties1.get("line_thickness");
            style.setProperty(Styles.LineWidth, lt);
        } else if (properties1.containsKey("text_size")) {
            style.setProperty(Styles.FontSize, (String) properties1.get("text_size"));
        }
         else if (properties1.containsKey("bold")) {
             if (  ((String)properties1.get("bold")).equals("true") ) {
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
         } else if (properties1.containsKey("text_color")) {
             style.setProperty(Styles.TextColor, (String)properties1.get("text_color"));
         } else if (properties1.containsKey("line_shape")) {
             style.setProperty(Styles.LineClass, (String)properties1.get("line_shape"));
             for (Box b : box.getLines().keySet()) {
                 IStyle s = MainActivity.workbook.getStyleSheet().findStyle(b.topic.getStyleId());
                 if (s == null) {
                     s = MainActivity.styleSheet.createStyle(IStyle.TOPIC);
                     MainActivity.styleSheet.addStyle(s, IStyleSheet.NORMAL_STYLES);
                 }
                 s.setProperty(Styles.LineClass, (String)properties1.get("line_shape"));
                 box.getLines().get(b).shape =  (String) properties1.get("line_shape");
             }
             for (Box b : edited) {
                 IStyle s = MainActivity.workbook.getStyleSheet().findStyle(b.topic.getStyleId());
                 s.setProperty(Styles.LineClass, (String) properties1.get("line_shape"));
                 for (Box b1 : b.getLines().keySet()) {
                     IStyle s1 = MainActivity.workbook.getStyleSheet().findStyle(b1.topic.getStyleId());
                     if (s1 == null) {
                         s1 = MainActivity.styleSheet.createStyle(IStyle.TOPIC);
                         MainActivity.styleSheet.addStyle(s1, IStyleSheet.NORMAL_STYLES);
                     }
                     s1.setProperty(Styles.LineClass, (String) properties1.get("line_shape"));
                     b.getLines().get(b1).shape = (String) properties1.get("line_shape");
                 }
             }
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
