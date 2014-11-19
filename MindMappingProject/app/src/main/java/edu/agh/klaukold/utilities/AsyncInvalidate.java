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

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class AsyncInvalidate extends AsyncTask<Void, Void, Void> {
	private Callback call;
	private Context context;
	
	ProgressDialog dialog;
	
	public AsyncInvalidate(Context context) {
		this.context = context;
	}
	
	@Override
    protected void onPreExecute() {
       dialog = ProgressDialog.show(context, "Loading", "Please wait...", true);
    }
	
	public void setCallback(Callback call) {
		this.call = call;
	}

	@Override
	protected Void doInBackground(Void... params) {
		call.execute();
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		if(Utils.lay != null) {
			Utils.lay.invalidate();
			//Utils.lay.postInvalidate();
		}
		
		dialog.dismiss();
    }
}
