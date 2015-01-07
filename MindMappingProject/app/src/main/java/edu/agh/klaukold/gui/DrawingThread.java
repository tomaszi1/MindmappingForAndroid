package edu.agh.klaukold.gui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RotateDrawable;
import android.view.SurfaceHolder;

import org.xmind.core.INotes;
import org.xmind.core.IPlainNotesContent;
import org.xmind.core.style.IStyle;
import org.xmind.ui.style.Styles;

import java.util.List;

import edu.agh.R;
import edu.agh.klaukold.common.Box;
import edu.agh.klaukold.common.Line;
import edu.agh.klaukold.common.Point;
import edu.agh.klaukold.enums.Position;

/**
 * Created by Klaudia on 2014-11-19.
 */
public class DrawingThread extends Thread {
    public SurfaceHolder holder;
    public Canvas canvas = new Canvas();
    Context context;
    private int mCanvasWidth;
    private int mCanvasHeight;
    private boolean running = false;
    private DrawView lay;

    public DrawingThread(SurfaceHolder holder, DrawView lay) {
        this.holder = holder;
        this.lay = lay;
    }

    public void setRunning(boolean b) {
        running = b;
    }

    public void setSurfaceSize(int width, int height) {
        synchronized (holder) {
            mCanvasWidth = width;
            mCanvasHeight = height;
        }
    }


    @Override
    public void run() {
        // PAINT
        while (running) {
            if(!holder.getSurface().isValid())
                continue;

            canvas = null;
            try {
                canvas = holder.lockCanvas(null);
               if (canvas  != null) {
                    synchronized (holder) {
                       if (lay.boxTomove == null) {
                           lay.Mydraw(canvas);
                       } else {
                           lay.drawBox(lay.boxTomove);
                       }
                        lay.postInvalidate();
                    }
               }
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

}
