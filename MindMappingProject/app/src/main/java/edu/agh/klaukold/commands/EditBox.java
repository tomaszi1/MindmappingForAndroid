package edu.agh.klaukold.commands;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import android.graphics.Color;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import edu.agh.klaukold.common.Box;
import edu.agh.klaukold.common.Line;
import edu.agh.klaukold.common.Marker;
import edu.agh.klaukold.common.Note;
import edu.agh.klaukold.common.Text;
import edu.agh.klaukold.enums.BlockShape;
import edu.agh.klaukold.enums.LineStyle;
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
        }
    }

    @Override
    public void undo() {
        if (properties1.containsKey("text")) {
            Text text = (Text) properties1.get("text");
            box.setText(text);
        }
    }



}
