/*
 This file is part of MindMap.

    MindMap is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    MindMap is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with MindMap; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/

package edu.agh.klaukold.utilities;

import android.app.Dialog;
import android.content.Context;

import edu.agh.R;


public class DialogFactory {
	
	public static Dialog newMapDialog(Context context) {
		Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.dialog_new);
		//dialog.setTitle(context.getResources().getString(R.string.dialog_newMap));
		return dialog;
	}



	public static Dialog boxContentDialog(Context context) {
		Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.dialog_content);
		dialog.setTitle(context.getResources().getString(R.string.dialog_content));
		dialog.setCancelable(false);
		return dialog;
	}
	

}
