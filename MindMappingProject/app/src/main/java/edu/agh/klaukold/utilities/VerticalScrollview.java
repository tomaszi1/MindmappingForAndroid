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

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class VerticalScrollview extends ScrollView {

    public VerticalScrollview(Context context) {
        super(context);
    }
    
    public VerticalScrollview(Context context, AttributeSet attrs) {
    	super(context, attrs);
    }
    
    public VerticalScrollview(Context context, AttributeSet attrs, int defStyle) {
    	super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                    Log.i("VerticalScrollview", "onInterceptTouchEvent: DOWN super false" );
                    super.onTouchEvent(ev);
                    break;

            case MotionEvent.ACTION_MOVE:
                    return false; // redirect MotionEvents to ourself

            case MotionEvent.ACTION_CANCEL:
                    Log.i("VerticalScrollview", "onInterceptTouchEvent: CANCEL super false" );
                    super.onTouchEvent(ev);
                    break;

            case MotionEvent.ACTION_UP:
                    Log.i("VerticalScrollview", "onInterceptTouchEvent: UP super false" );
                    return false;

            default: Log.i("VerticalScrollview", "onInterceptTouchEvent: " + action ); break;
        }

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        super.onTouchEvent(ev);
        Log.i("VerticalScrollview", "onTouchEvent. action: " + ev.getAction() );
         return true;
    }
}