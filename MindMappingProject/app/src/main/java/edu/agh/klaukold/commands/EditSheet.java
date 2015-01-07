package edu.agh.klaukold.commands;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;

import org.xmind.core.ISheet;
import org.xmind.core.style.IStyle;
import org.xmind.ui.style.Styles;

import java.util.Properties;
import edu.agh.klaukold.gui.MainActivity;
import edu.agh.klaukold.interfaces.Command;

public class EditSheet implements Command  {
    ISheet sheet;
    Properties properties1 = new Properties();
    Properties after;
	@Override
	public void execute(Properties properties) {
        after = (Properties) properties.clone();
        sheet = (ISheet) properties.get("sheet");
//        try {
//            properties1.put("sheet", sheet.SheetClone());
//        } catch (CloneNotSupportedException e) {
//            e.printStackTrace();
//        }
        if (properties.containsKey("color")) {
            if (MainActivity.styleSheet.findStyle(sheet.getStyleId()) != null && MainActivity.styleSheet.findStyle(sheet.getStyleId()).getProperty(Styles.FillColor) != null) {
                properties1.put("color", MainActivity.styleSheet.findStyle(sheet.getStyleId()).getProperty(Styles.FillColor));
                Log.w("", MainActivity.styleSheet.findStyle(sheet.getStyleId()).getProperty(Styles.FillColor));
            } else {
                properties1.put("color", "#FFFFFF");
            }

            ColorDrawable c = (ColorDrawable)properties.get("color");
            String colorHex = "#" + (!Integer.toString(Integer.valueOf(Color.red(c.getColor())), 16).equals("0") ? Integer.toString(Integer.valueOf(Color.red(c.getColor())), 16) : "00")
                    + (!Integer.toString(Integer.valueOf(Color.green(c.getColor())), 16).equals("0") ? Integer.toString(Integer.valueOf(Color.green(c.getColor())), 16) : "00" )
                    + (!Integer.toString(Integer.valueOf(Color.blue(c.getColor())), 16).equals("0") ? Integer.toString(Integer.valueOf(Color.blue(c.getColor())), 16) : "00" );
            if (sheet.getStyleId() != null && MainActivity.styleSheet.findStyle(sheet.getStyleId()) != null) {
                MainActivity.styleSheet.findStyle(sheet.getStyleId()).setProperty(Styles.FillColor,colorHex);
            } else {
                IStyle style = MainActivity.styleSheet.createStyle(IStyle.MAP);
                style.setProperty(Styles.FillColor, colorHex);
                sheet.setStyleId(style.getId());
            }
        }
		
	}

    @Override
    public void undo() {
      //  sheet = (Sheet)properties1.get("sheet");
        if (properties1.containsKey("color")) {
            String c = (String)properties1.get("color");
            MainActivity.styleSheet.findStyle(sheet.getStyleId()).setProperty(Styles.FillColor, String.valueOf(c));
        }
    }

    @Override
    public void redo() {
        execute(after);
    }

}
