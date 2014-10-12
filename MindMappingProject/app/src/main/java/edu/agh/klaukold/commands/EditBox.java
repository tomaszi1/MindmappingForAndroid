package edu.agh.klaukold.commands;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.sax.RootElement;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import edu.agh.klaukold.common.Box;
import edu.agh.klaukold.common.Line;
import edu.agh.klaukold.common.Marker;
import edu.agh.klaukold.common.Note;
import edu.agh.klaukold.common.Root;
import edu.agh.klaukold.common.Text;
import edu.agh.klaukold.enums.BlockShape;
import edu.agh.klaukold.enums.LineStyle;
import edu.agh.klaukold.enums.LineThickness;
import edu.agh.klaukold.gui.MainActivity;
import edu.agh.klaukold.interfaces.Command;

public class EditBox implements Command{
    public Box box;
    Properties properties1;
    @Override
    public void execute(Properties properties){
        properties1 = (Properties)properties.clone();
        box = (Box) properties.get("box");
        if (properties.containsKey("box_text")) {
            Text text = (Text) properties.get("box_text");
            try {
                 properties1.put("text", box.getText().TextClone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            box.setText(text);
        } else if (properties.containsKey("color")) {
            properties1.put("color", box.getColor());
            ColorDrawable color = (ColorDrawable)properties.get("color");
            box.setColor(color);
        } else if (properties.containsKey("shape")) {
            properties1.put("shape", box.getDrawableShape());
            BlockShape shape = (BlockShape)properties.get("shape");
            box.setShape(shape);
            MainActivity.changeShape(box);
            box.prepareDrawableShape();
        } else if (properties.containsKey("line_color")) {
            properties1.put("line_color", new ColorDrawable(box.getLineColor()));
            ColorDrawable color = (ColorDrawable)properties.get("line_color");
            box.setLineColor(color.getColor());
            for (Box b : box.getLines().keySet()) {
                box.getLines().get(b).setColor(color);
            }
        } else if (properties.containsKey("line_thickness")) {
            properties1.put("line_thickness", box.getLineThickness());
            LineThickness lt = (LineThickness) properties.get("line_thickness");
            box.setLineThickness(lt);
            for (Box b : box.getLines().keySet()) {
                box.getLines().get(b).setThickness((int) lt.getValue());
            }
        } else if (properties.containsKey("line_shape")) {
            properties1.put("line_shape", box.getLineThickness());
            LineStyle ls = (LineStyle) properties.get("line_shape");
            box.setLineStyle(ls);
            for (Box b : box.getLines().keySet()) {
                box.getLines().get(b).setShape(ls);
            }
        }
    }

    @Override
    public void undo() {
        if (properties1.containsKey("text")) {
            Text text = (Text) properties1.get("text");
            box.setText(text);
        } else if (properties1.containsKey("color")) {
            ColorDrawable color = (ColorDrawable)properties1.get("color");
            box.setColor(color);
        } else if  (properties1.containsKey("shape")) {
            Drawable shape = (Drawable)properties1.get("shape");
            box.setDrawableShape(shape);
            box.prepareDrawableShape();
        } else if (properties1.containsKey("line_color")) {
            int color = ((ColorDrawable) properties1.get("line_color")).getColor();
            box.setLineColor(color);
        } else if (properties1.containsKey("line_thickness")) {
            LineThickness lt = (LineThickness) properties1.get("line_thickness");
            box.setLineThickness(lt);
        }
    }



}
