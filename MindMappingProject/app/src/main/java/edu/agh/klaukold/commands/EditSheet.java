package edu.agh.klaukold.commands;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import org.xmind.core.ISheet;
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
            properties1.put("color", sheet.getTheme().getProperty(Styles.FillColor));
            ColorDrawable c = (ColorDrawable)properties.get("color");
            sheet.getTheme().setProperty(Styles.FillColor, String.valueOf(c.getColor()));
        } else if (properties.containsKey("wallpaper")) {

        } else if (properties.containsKey("multiple_branch")) {

        }
        // TODO Auto-generated method stub
		
	}

    @Override
    public void undo() {
      //  sheet = (Sheet)properties1.get("sheet");
        if (properties1.containsKey("color")) {
            String c = (String)properties1.get("color");
            sheet.getTheme().setProperty(Styles.FillColor, String.valueOf(c));
        }
    }

    @Override
    public void redo() {
        execute(after);
    }

}
