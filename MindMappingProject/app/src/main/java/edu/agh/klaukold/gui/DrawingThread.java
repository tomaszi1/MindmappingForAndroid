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
    private Paint paint = new Paint();
    public List<Line> lines;
    public float transx, transy, zoomx = 1, zoomy = 1;
    private int[] position = new int[2];
    public Canvas canvas = new Canvas();
    public int left = 0;
    public int right = 0;
    public static int LUheight = 0;
    public static int LDHehight = 0;
    public static int RUheight = 0;
    public static int RDHehight = 0;
    public static int count = 0;
    boolean UL = true;
    boolean UR = true;
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
        try {
            canvas = holder.lockCanvas();
            synchronized (holder) {
                lay.draw(canvas);

            }
        } finally {
            if (canvas != null) {
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }


}
