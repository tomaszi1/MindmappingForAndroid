package edu.agh.klaukold.commands;

import android.graphics.drawable.ColorDrawable;

import java.util.Properties;

import edu.agh.klaukold.common.Sheet;
import edu.agh.klaukold.gui.MainActivity;
import edu.agh.klaukold.interfaces.Command;

public class EditSheet implements Command  {
    Sheet sheet;
    Properties properties1 = new Properties();
	@Override
	public void execute(Properties properties) {
        sheet = (Sheet) properties.get("sheet");
        try {
            properties1.put("sheet", sheet.SheetClone());
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        if (properties.containsKey("color")) {
            properties1.put("color", sheet.getColor());
            sheet.setColor((ColorDrawable)properties.get("color"));
        } else if (properties.containsKey("wallpaper")) {

        } else if (properties.containsKey("multiple_branch")) {

        }
        // TODO Auto-generated method stub
		
	}

    @Override
    public void undo() {
      //  sheet = (Sheet)properties1.get("sheet");
        if (properties1.containsKey("color")) {
            sheet.setColor((ColorDrawable)properties1.get("color"));
        }
    }

}
