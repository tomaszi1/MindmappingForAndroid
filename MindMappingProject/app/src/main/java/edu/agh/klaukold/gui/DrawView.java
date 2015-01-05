package edu.agh.klaukold.gui;

import java.util.List;

import edu.agh.R;
import edu.agh.klaukold.common.Box;
import edu.agh.klaukold.common.Line;
import edu.agh.klaukold.common.Point;
import edu.agh.klaukold.enums.Position;
import edu.agh.klaukold.utilities.Utils;

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
import android.util.AttributeSet;
import android.util.Pair;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.AbsListView;

import org.xmind.core.INotes;
import org.xmind.core.IPlainNotesContent;
import org.xmind.core.IRelationship;
import org.xmind.core.style.IStyle;
import org.xmind.ui.style.Styles;

public class DrawView extends SurfaceView implements SurfaceHolder.Callback {
    private DrawingThread drawingThread;
    private Paint paint = new Paint();
    public List<Line> lines;
    public float transx, transy, zoomx = 1, zoomy = 1;
    private int[] position = new int[2];
    private Canvas canvas;
    public int left = 0;
    public int right = 0;
    public static int LUheight = 0;
    public static int LDHehight = 0;
    public static int RUheight = 0;
    public static int RDHehight = 0;
    public static int count = 0;
    boolean UL = true;
    boolean UR = true;
    public SurfaceHolder holder;
    boolean first = true;

    public DrawView(Context context) {
        super(context);
        this.context = context;
        init();

    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        getLocationInWindow(position);
        setWillNotDraw(false);
        holder = getHolder();          // Holder is now the internal/private mSurfaceHolder inherit
        // from the SurfaceView class, which is from an anonymous
        // class implementing SurfaceHolder interface.
        holder.addCallback(this);
        canvas = new Canvas();
    }

    public Context context;

    private boolean revalidate = true;

    public void revalidate() {
        revalidate = true;

    }

    @Override
    public void onDraw(Canvas canvas) {
        this.canvas = canvas;
        //przesunięcie mapy i wyskalowanie
        this.canvas.translate(transx, transy);
        this.canvas.scale(zoomx, zoomy);

        Box root = MainActivity.root;
        if (root == null) {
            return;
        }
        drawBox(root);
        if (first && MainActivity.style.equals("ReadyMap")) {
            for (int i=0; i<10; i++) {
                Utils.calculateAll();
            }
            first = false;
        }
        for (Box box : root.getChildren()) {
            fireDrawChildren(box, this.canvas);
        }



    }

    private float getLongest(String[] array) {
        float i = 0;

        for (String k : array) {
            if (paint.measureText(k) > i) {
                i = paint.measureText(k);
            }
        }
        return i;
    }

    private void fireDrawChildren(Box box, Canvas canvas) {
        drawBox(box);
        for (Box child : box.getChildren()) {
            drawBox(child);
            fireDrawChildren(child, canvas);
        }
    }


    private void setOffsets(Box parent) {
        if (!parent.getChildren().isEmpty()) {
            setOffsets(parent.getChildren(), parent);
            for (Box box : parent.getChildren()) {
                //setOffsets(position, box);
            }
        }
    }

    //wyjustowanie względem rodzica
    private void setOffsets(List<Box> list, Box parent) {
        Box first = list.get(0);
        int diff = first.getDrawableShape().getBounds().top - parent.getDrawableShape().getBounds().top;
        first.getDrawableShape().getBounds().top -= diff;
        first.getDrawableShape().getBounds().bottom -= diff;

        for (int x = 1; x < list.size(); x++) {
            first = list.get(x);
            diff = first.getDrawableShape().getBounds().top - (list.get(x - 1).getDrawableShape().getBounds().bottom + 20);
            first.getDrawableShape().getBounds().top -= diff;
            first.getDrawableShape().getBounds().bottom -= diff;
        }

        int offset = 0;
        int size = list.size();

        first = list.get(size / 2);
        offset = first.getDrawableShape().getBounds().top - (parent.getDrawableShape().getBounds().top + parent.getDrawableShape().getBounds().bottom) / 2;

        for (Box b : list) {
            b.getDrawableShape().getBounds().top -= offset;
            b.getDrawableShape().getBounds().bottom -= offset;
            updateBoxWithText(b);
        }
    }


    public void drawBox(Box box) {
        if (box.topic.getParent() == null || !box.topic.getParent().isFolded()) {
            IStyle style = MainActivity.workbook.getStyleSheet().findStyle(box.topic.getStyleId());
            int color  = MainActivity.res.getColor(R.color.gray);
            if (style != null && style.getProperty(Styles.LineColor) != null) {
                color = Color.parseColor(style.getProperty(Styles.LineColor));
            }
            int width = 1;
            if (style != null &&  style.getProperty(Styles.LineWidth) != null) {
                width = Integer.parseInt(remove2LastChars(style.getProperty(Styles.LineWidth)));
            }
            if (style != null && style.getProperty(Styles.ShapeClass) == Styles.TOPIC_SHAPE_DIAMOND) {
                    ((GradientDrawable) ((RotateDrawable) box.getDrawableShape()).getDrawable()).setStroke(width, color);
            } else if (style == null || (style.getProperty(Styles.ShapeClass) != Styles.TOPIC_SHAPE_UNDERLINE && style.getProperty(Styles.ShapeClass) != Styles.TOPIC_SHAPE_NO_BORDER)) {
                ((GradientDrawable) box.getDrawableShape()).setStroke(width, color);
            }
          if (!box.topic.isFolded()) {
                showLines(box);
                if (MainActivity.style.equals("ReadyMap")) {
                    showLines(MainActivity.root);
                }
                if (box.relationships.size() > 0) {
                    for (IRelationship r : box.relationships.keySet()) {
                        drawRelationship(box, r);
                    }
                }
            }
//            if (box.point == null || (box.topic.getParent() != null && !box.topic.getParent().isRoot())) {
//                calculatePosition(box);
//            }
            box.prepareDrawableShape();
            calculateBoxSize(box);
            box.getDrawableShape().draw(canvas);
            drawText(box);
            if (style != null && style.getProperty(Styles.ShapeClass) != null && style.getProperty(Styles.ShapeClass).equals(Styles.TOPIC_SHAPE_UNDERLINE)) {
                Path path = new Path();
                path.moveTo(box.getDrawableShape().getBounds().left, box.getDrawableShape().getBounds().bottom);
                path.lineTo(box.getDrawableShape().getBounds().right, box.getDrawableShape().getBounds().bottom);
                Paint paint1 = new Paint();
                if (style.getProperty(Styles.LineColor) != null) {
                    paint1.setColor(Color.parseColor(style.getProperty(Styles.LineColor)));
                } else {
                    paint1.setColor(Color.GRAY);
                }
                if (style.getProperty(Styles.LineWidth) != null) {
                    paint1.setStrokeWidth(Integer.parseInt(remove2LastChars(style.getProperty(Styles.LineWidth))));
                }
                paint1.setStyle(Paint.Style.STROKE);
                this.canvas.drawPath(path, paint1);
            }

            if (box.topic.getNotes().getContent(INotes.PLAIN) != null && !((IPlainNotesContent) box.topic.getNotes().getContent(INotes.PLAIN)).getTextContent().equals("")) {
                box.newNote = context.getResources().getDrawable(R.drawable.ic_action_view_as_list);
                box.newNote.setBounds(box.getDrawableShape().getBounds().left, box.getDrawableShape().getBounds().top - 5 - ((BitmapDrawable) box.newNote).getBitmap().getHeight(),
                        box.getDrawableShape().getBounds().left + ((BitmapDrawable) box.newNote).getBitmap().getWidth(), box.getDrawableShape().getBounds().top - 5);
                box.newNote.draw(this.canvas);
            }
            if (box.isSelected) {
                showAction(box);
            }

        }
    }

    public void showAction(Box box) {
        if (box.topic.getNotes().getContent(INotes.PLAIN) != null && !((IPlainNotesContent) box.topic.getNotes().getContent(INotes.PLAIN)).getTextContent().equals("")) {
            box.newNote = context.getResources().getDrawable(R.drawable.ic_action_view_as_list);
            box.newNote.setBounds(box.getDrawableShape().getBounds().left, box.getDrawableShape().getBounds().top - 5 - ((BitmapDrawable) box.newNote).getBitmap().getHeight(),
                    box.getDrawableShape().getBounds().left + ((BitmapDrawable) box.newNote).getBitmap().getWidth(), box.getDrawableShape().getBounds().top - 5);
            box.newNote.draw(this.canvas);
        } else {
            box.addNote = context.getResources().getDrawable(R.drawable.ic_action_new_event);
            box.addNote.setBounds(box.getDrawableShape().getBounds().left, box.getDrawableShape().getBounds().top - 5 - ((BitmapDrawable) box.addNote).getBitmap().getHeight(),
                    box.getDrawableShape().getBounds().left + ((BitmapDrawable) box.addNote).getBitmap().getWidth(), box.getDrawableShape().getBounds().top - 5);
            box.addNote.draw(canvas);
        }
        box.addBox = context.getResources().getDrawable(R.drawable.ic_action_new);
        box.addBox.setBounds(box.getDrawableShape().getBounds().left + 5 + ((BitmapDrawable) box.addBox).getBitmap().getWidth(), box.getDrawableShape().getBounds().top - 5 - ((BitmapDrawable) box.addBox).getBitmap().getHeight(),
                box.getDrawableShape().getBounds().left + 5 + ((BitmapDrawable) box.addBox).getBitmap().getWidth() + ((BitmapDrawable) box.addBox).getBitmap().getWidth(), box.getDrawableShape().getBounds().top - 5);
        box.addBox.draw(this.canvas);
        if (!(box.topic.isRoot()) && box.topic.getAllChildren().size() > 0) {
            if (!box.topic.isFolded()) {
                box.collapseAction = context.getResources().getDrawable(R.drawable.ic_action_collapse);
                box.collapseAction.setBounds(box.getDrawableShape().getBounds().left + 5 + ((BitmapDrawable) box.addBox).getBitmap().getWidth() + ((BitmapDrawable) box.addBox).getBitmap().getWidth() + 5, box.getDrawableShape().getBounds().top - 5 - ((BitmapDrawable) box.collapseAction).getBitmap().getHeight(), box.getDrawableShape().getBounds().left + 5 + ((BitmapDrawable) box.addBox).getBitmap().getWidth() + ((BitmapDrawable) box.addBox).getBitmap().getWidth() + ((BitmapDrawable) box.collapseAction).getBitmap().getWidth(), box.getDrawableShape().getBounds().top - 5);
                box.collapseAction.draw(this.canvas);
            } else {
                box.expandAction = context.getResources().getDrawable(R.drawable.ic_action_expand);
                box.expandAction.setBounds(box.getDrawableShape().getBounds().left + 5 + ((BitmapDrawable) box.addBox).getBitmap().getWidth() + ((BitmapDrawable) box.addBox).getBitmap().getWidth() + 5, box.getDrawableShape().getBounds().top - 5 - ((BitmapDrawable) box.expandAction).getBitmap().getHeight(), box.getDrawableShape().getBounds().left + 5 + ((BitmapDrawable) box.addBox).getBitmap().getWidth() + ((BitmapDrawable) box.addBox).getBitmap().getWidth() + ((BitmapDrawable) box.expandAction).getBitmap().getWidth(), box.getDrawableShape().getBounds().top - 5);
                box.expandAction.draw(this.canvas);
            }
        }
    }
    private void drawText(Box box) {
        IStyle style = MainActivity.workbook.getStyleSheet().findStyle(box.topic.getStyleId());
        paint = new Paint();
        if (box != null) {
            float x = 0;
            float y = 0;
            String s = box.topic.getTitleText();
            String[] parts = s.split("\n");
            float f = getLongest(parts);
            Typeface font = Typeface.DEFAULT;
            if (style != null) {
                if (style.getProperty(Styles.TextColor) != null) {
                    paint.setColor(Color.parseColor(style.getProperty(Styles.TextColor)));
                } else {
                    paint.setColor(Color.BLACK);
                }
                if (style.getProperty(Styles.FontSize) != null) {
                    paint.setTextSize(Integer.parseInt(remove2LastChars(style.getProperty(Styles.FontSize))));
                } else {
                    paint.setTextSize(13);
                }
                if (style.getProperty(Styles.FontFamily) == null || style.getProperty(Styles.FontFamily).equals("Times New Roman")) {
                    font = Typeface.SERIF;
                } else if (style.getProperty(Styles.FontFamily).equals("Arial")) {
                    font = Typeface.SANS_SERIF;
                } else if (style.getProperty(Styles.FontFamily).equals("Courier New")) {
                    font = Typeface.MONOSPACE;
                }
                if (style.getProperty(Styles.FontStyle) == Styles.FONT_STYLE_ITALIC && style.getProperty(Styles.FontStyle) == Styles.FONT_WEIGHT_BOLD) {
                    Typeface tf = Typeface.create(Typeface.SERIF, Typeface.BOLD_ITALIC);
                    paint.setTypeface(tf);
                    // paint.setFakeBoldText(true);
                } else if (style.getProperty(Styles.FontStyle) == Styles.FONT_STYLE_ITALIC) {
                    Typeface tf = Typeface.create(Typeface.SERIF, Typeface.ITALIC);
                    paint.setTypeface(tf);
                } else if (style.getProperty(Styles.FontStyle) == Styles.FONT_WEIGHT_BOLD) {
                    Typeface tf = Typeface.create(font, Typeface.BOLD);
                    paint.setTypeface(tf);

                } else if (style.getProperty(Styles.TextAlign) != null && style.getProperty(Styles.TextAlign).equals(Styles.ALIGN_LEFT) && style.getProperty(Styles.TextDecoration) != null && style.getProperty(Styles.TextDecoration).equals(Styles.TEXT_DECORATION_LINE_THROUGH)) {
                    Typeface tf = Typeface.create(font, Typeface.NORMAL);
                    paint.setTypeface(tf);
                    paint.setStrokeWidth(2);
                    paint.setStrikeThruText(true);
                } else {
                    // Typeface tf = Typeface.create(box.getText().typeface);
                    paint.setTypeface(font);
                }
                if (style.getProperty(Styles.TextAlign) == null || style.getProperty(Styles.TextAlign).equals(Styles.ALIGN_CENTER)) {
                    paint.setTextAlign(Paint.Align.CENTER);
                } else if (style.getProperty(Styles.TextAlign).equals(Styles.ALIGN_RIGHT)) {
                    paint.setTextAlign(Paint.Align.RIGHT);
                } else if (style.getProperty(Styles.TextAlign).equals(Styles.ALIGN_LEFT)) {
                    paint.setTextAlign(Paint.Align.LEFT);
                }
            } else {
                paint.setColor(Color.BLACK);
                paint.setTextSize(13);
                font = Typeface.MONOSPACE;
                paint.setTextAlign(Paint.Align.CENTER);
            }


            for (int j = 0; j < parts.length; j++) {
                String str = parts[j];
                float start = 0.4f * box.getDrawableShape().getBounds().height() / (parts.length);

                if (parts.length == 1) {
                    Rect rectText = new Rect();
                    paint.getTextBounds(box.topic.getTitleText(), 0, box.topic.getTitleText().length(), rectText);
                    if (paint.getTextAlign() == Paint.Align.CENTER) {
                        x = box.getDrawableShape().getBounds().left + rectText.width() / 2 + (box.getDrawableShape().getBounds().width() - rectText.width()) / 2;
                        //  }
                    } else if (paint.getTextAlign() == Paint.Align.RIGHT) {
                        x = (box.getDrawableShape().getBounds().right - 10);
                    } else if (paint.getTextAlign() == Paint.Align.LEFT) {
                        x = (box.getDrawableShape().getBounds().left + 10);
                        y = box.getDrawableShape().getBounds().top + rectText.height();
                    }
                    y = box.getDrawableShape().getBounds().centerY();
                    this.canvas.drawText(str, x, y, paint);

                } else {
                    if (paint.getTextAlign() == Paint.Align.CENTER) {
                        if (style != null && style.getProperty(Styles.ShapeClass).equals(Styles.TOPIC_SHAPE_DIAMOND)) {
                            x = box.getDrawableShape().getBounds().left + box.getWidth() / 2;
                            y = box.getDrawableShape().getBounds().top + (box.getDrawableShape().getBounds().height()) / (parts.length) * (j) + start + paint.getTextSize() / 2;
                        } else if (style != null && style.getProperty(Styles.ShapeClass).equals(Styles.TOPIC_SHAPE_ELLIPSE)) {
                            if (j == 0) {
                                y = box.getDrawableShape().getBounds().top + paint.getTextSize() + paint.getTextSize() / 2;
                            } else {
                                y = box.getDrawableShape().getBounds().top + paint.getTextSize() * (j + 1) + paint.getTextSize() / 2;
                                // y = box.getDrawableShape().getBounds().top + (((box.getDrawableShape().getBounds().height()) / ((parts.length) * paint.getTextSize())) * (j + 1)) + 20;
                            }
                            //  start = 0.2f * box.getDrawableShape().getBounds().height() / (parts.length);
                            x = box.getDrawableShape().getBounds().left + box.getWidth() / 2;
                            // y = box.getDrawableShape().getBounds().top + (box.getDrawableShape().getBounds().height()) / (parts.length) * (j) + start + paint.getTextSize();
                        } else {
                            x = box.getDrawableShape().getBounds().left + box.getWidth() / 2;
                            //y = box.getDrawableShape().getBounds().top + (box.getDrawableShape().getBounds().height()) / (parts.length) * (j) * paint.getTextSize();
                            if (j == 0) {
                                y = box.getDrawableShape().getBounds().top + paint.getTextSize();

                            } else {
                                y = box.getDrawableShape().getBounds().top + paint.getTextSize() * (j + 1) + paint.getTextSize() / 2;
                                // y = box.getDrawableShape().getBounds().top + (((box.getDrawableShape().getBounds().height()) / ((parts.length) * paint.getTextSize())) * (j + 1)) + 20;
                            }
                        }
                    } else if (paint.getTextAlign() == Paint.Align.RIGHT) {
                        if (style != null && style.getProperty(Styles.ShapeClass).equals(Styles.TOPIC_SHAPE_DIAMOND)) {
                            x = (box.getDrawableShape().getBounds().right - (box.getWidth() - f) / 2);
                            y = box.getDrawableShape().getBounds().top + (box.getDrawableShape().getBounds().height()) / (parts.length) * (j) + start + paint.getTextSize() / 2;
                        } else if (style != null && style.getProperty(Styles.ShapeClass).equals(Styles.TOPIC_SHAPE_ELLIPSE)) {
                            start = 0.2f * box.getDrawableShape().getBounds().height() / (parts.length);
                            x = (box.getDrawableShape().getBounds().right - (box.getWidth() - f) / 2);
                            y = box.getDrawableShape().getBounds().top + (box.getDrawableShape().getBounds().height()) / (parts.length) * (j) + start + paint.getTextSize();
                        } else {
                            x = (box.getDrawableShape().getBounds().right - (box.getWidth() - f) / 2);
                            y = box.getDrawableShape().getBounds().top + (box.getDrawableShape().getBounds().height()) / (parts.length) * (j) + start + paint.getTextSize() / 2;
                        }

                    } else if (paint.getTextAlign() == Paint.Align.LEFT) {
                        if (style != null && style.getProperty(Styles.ShapeClass).equals(Styles.TOPIC_SHAPE_DIAMOND)) {
                            x = (box.getDrawableShape().getBounds().left + (box.getWidth() - f) / 2);
                            y = box.getDrawableShape().getBounds().top + (box.getDrawableShape().getBounds().height()) / (parts.length) * (j) + start + paint.getTextSize() / 2;
                        } else if (style != null && style.getProperty(Styles.ShapeClass).equals(Styles.TOPIC_SHAPE_ELLIPSE)) {
                            start = 0.2f * box.getDrawableShape().getBounds().height() / (parts.length);
                            x = (box.getDrawableShape().getBounds().left + (box.getWidth() - f) / 2);
                            y = box.getDrawableShape().getBounds().top + (box.getDrawableShape().getBounds().height()) / (parts.length) * (j) + start + paint.getTextSize();
                        } else {
                            x = (box.getDrawableShape().getBounds().left + (box.getWidth() - f) / 2);
                            y = box.getDrawableShape().getBounds().top + (box.getDrawableShape().getBounds().height()) / (parts.length) * (j) + start + paint.getTextSize() / 2;
                        }
                    }
                    this.canvas.drawText(str, x, y, paint);
                }
            }
        }
    }

    public void calculateBoxSize(Box box) {
        String s = box.topic.getTitleText();
        String[] parts = s.split("\n");
        String font = null;
        if (MainActivity.workbook.getStyleSheet().findStyle(box.topic.getStyleId()) != null) {
            font = MainActivity.workbook.getStyleSheet().findStyle(box.topic.getStyleId()).getProperty(Styles.FontSize);
        }
        if (font == null) {
            font = "13dp";
        }
        float f = getLongest(parts);
        drawText(box);
        Rect rect = new Rect();
        if (MainActivity.workbook.getStyleSheet().findStyle(box.topic.getStyleId()) != null && MainActivity.workbook.getStyleSheet().findStyle(box.topic.getStyleId()).getProperty(Styles.FontSize) != null) {
            paint.setTextSize((float) Integer.parseInt(remove2LastChars(MainActivity.workbook.getStyleSheet().findStyle(box.topic.getStyleId()).getProperty(Styles.FontSize))));
        } else {
            paint.setTextSize(13);
        }
        paint.getTextBounds(s, 0, s.length(), rect);
        if (parts.length == 1) {
            int w = Math.abs(rect.right - rect.left) + Math.abs(rect.right - rect.left) / 2;
            if (w > box.getWidth()) {
                box.setWidth(w);
            }
        } else {
            int width = 0;
            for (String sPart : parts) {
                paint.getTextBounds(sPart, 0, sPart.length(), rect);
                if (Math.abs((rect.right - rect.left)) > width) {
                    width = Math.abs(rect.right - rect.left);
                }
            }
            if (width > box.getWidth()) {
                box.setWidth(width + Integer.parseInt(remove2LastChars(font)));
            }

            int h = (rect.bottom - rect.top) * parts.length + (Integer.parseInt(remove2LastChars(font)) / 2 * parts.length);
            if (h > 100) {
                box.setHeight(h);
            }
            if (MainActivity.workbook.getStyleSheet().findStyle(box.topic.getId()) != null && MainActivity.workbook.getStyleSheet().findStyle(box.topic.getId()).equals(Styles.TOPIC_SHAPE_ELLIPSE)) {
                if (parts.length > 1) {
                    box.setHeight((int) (box.getHeight() * Math.sqrt(2)));
                }
            }
        }
        int right_old = box.drawableShape.getBounds().right;
        int left_old = box.drawableShape.getBounds().left;
        int botom_old = box.drawableShape.getBounds().bottom;
        box.prepareDrawableShape();
        if (!box.topic.isRoot() && box.drawableShape.getBounds().left > MainActivity.root.drawableShape.getBounds().centerX() && (left_old != box.drawableShape.getBounds().left || botom_old != box.drawableShape.getBounds().bottom) && box.topic.getParent().isRoot()) {
            box.parent.getLines().get(box).setEnd(new Point(box.getDrawableShape().getBounds().left, box.getDrawableShape().getBounds().centerY()));
        } else if (!box.topic.isRoot() && (right_old != box.drawableShape.getBounds().right || botom_old != box.drawableShape.getBounds().bottom) && box.topic.getParent().isRoot()) {
            box.drawableShape.getBounds().left = box.drawableShape.getBounds().right - right_old;
            box.prepareDrawableShape();
            if (box.parent.getLines().get(box) != null) {
                box.parent.getLines().get(box).setEnd(new Point(box.getDrawableShape().getBounds().right, box.getDrawableShape().getBounds().centerY()));
            }
        }
        if (box.isSelected) {
            box.setActiveColor();
        }

    }

    public void updateBoxWithText(Box box) {
        calculateBoxSize(box);
        box.drawableShape.draw(canvas);
        drawText(box);

    }

    public void showLines(Box box) {
        for (Box b1 : box.getLines().keySet()) {
            Line x = box.getLines().get(b1);
           // if (x == null || !x.isVisible()) {
            if (x == null) {
                continue;
            } else {
                Paint paint = new Paint();
                paint.setColor(x.getColor().getColor());
                paint.setStrokeWidth(x.getThickness() + 1);
                paint.setStyle(Paint.Style.STROKE);
                x.box = box;
                x.preparePath();
                x.box = null;
                canvas.drawPath(x.getPath(), paint);
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (drawingThread == null) {
            drawingThread = new DrawingThread(holder, this);
            drawingThread.setRunning(true);
            drawingThread.setSurfaceSize(width, height);
            drawingThread.context = context;
            drawingThread.start();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        if (drawingThread != null) {
            drawingThread.setRunning(false);
            while (retry) {
                try {
                    drawingThread.join();
                    retry = false;
                } catch (InterruptedException e) {
                }
            }
        }
    }

    private class EndlessScrollListener implements AbsListView.OnScrollListener {

        private int visibleThreshold = 5;
        private int currentPage = 0;
        private int previousTotal = 0;
        private boolean loading = true;

        public EndlessScrollListener() {
        }

        public EndlessScrollListener(int visibleThreshold) {
            this.visibleThreshold = visibleThreshold;
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                    currentPage++;
                }
            }
            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                // I load the next page of gigs using a background task,
                // but you can call any function here.
                //new LoadGigsTask().execute(currentPage + 1);
                loading = true;
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }

    }


    private String remove2LastChars(String str) {
        return str.substring(0, str.length() - 2);
    }


    private void calculatePosition(Box b) {
        IStyle parentStyle = MainActivity.workbook.getStyleSheet().findStyle(b.parent.topic.getStyleId());
        String shape = null;
        String width = null;
        String color = null;
        if (parentStyle != null) {
            shape = parentStyle.getProperty(Styles.LineClass);
            width = parentStyle.getProperty(Styles.LineWidth);
            if (width == null) {
                width = "1";
            } else {
               width =  width.substring(0, parentStyle.getProperty(Styles.LineWidth).length() - 2);
            }
            color = parentStyle.getProperty(Styles.LineColor);
            if (color == null) {
                color = "#A0A0A0";
            }
        } else {
            width = "1";
            color = "#A0A0A0";
        }
        if (b.topic.getParent().isRoot()) {
            int off = 1;
            if (count % 8 == 0) {
                off = count / 8 + 1;
            }
            if (left < right) {
                if (!(right % 2 == 0) && RDHehight == RUheight) {
                    b.point = new Point(b.parent.getDrawableShape().getBounds().right + off * 50, b.parent.getDrawableShape().getBounds().centerY() - b.getHeight() / 2);
                    if (b.parent.getLines().get(b) != null) {
                        b.parent.getLines().get(b).setStart(new Point(b.parent.getDrawableShape().getBounds().right, b.parent.getDrawableShape().getBounds().centerY()));
                        b.parent.getLines().get(b).setEnd(new Point(b.parent.getDrawableShape().getBounds().right + 50, b.parent.getDrawableShape().getBounds().centerY()));
                    } else {

                        Line l;
                        l = new Line(shape, Integer.parseInt(width), new ColorDrawable(Color.parseColor(color)),
                                new Point(b.parent.getDrawableShape().getBounds().right, b.parent.getDrawableShape().getBounds().centerY()),
                                new Point(b.parent.getDrawableShape().getBounds().right + off * 50 + b.getWidth(), b.parent.getDrawableShape().getBounds().centerY()), true);
                        b.parent.getLines().put(b, l);

                    }
                    int h = 0;
                    for (int i = 0; i < b.getChildren().size(); i++) {
                        h += b.getChildren().get(i).getHeight() + 35;
                    }
                    RUheight -= (b.getHeight() / 2 + h);
                    RDHehight += b.getHeight() / 2 + h;
                } else {
                    if (UR) {
                        b.point = new Point(b.parent.getDrawableShape().getBounds().right + off * 50, RUheight - 40 - b.getHeight());
                        if (b.parent.getLines().get(b) != null) {
                            b.parent.getLines().get(b).setStart(new Point(b.parent.getDrawableShape().getBounds().right, b.parent.getDrawableShape().getBounds().centerY()));
                            b.parent.getLines().get(b).setEnd(new Point(b.parent.getDrawableShape().getBounds().right + 50, RUheight - b.getHeight() / 2));
                        } else {
                            Line l = null;
                            l = new Line(shape, Integer.parseInt(width), new ColorDrawable(Color.parseColor(color)),
                                    new Point(b.parent.getDrawableShape().getBounds().right, b.parent.getDrawableShape().getBounds().centerY()),
                                    new Point(b.parent.getDrawableShape().getBounds().right + off * 50, RUheight - b.getHeight() / 2), true);
                            l.off = 10;
                            b.parent.getLines().put(b, l);

                        }
                        int h = 0;
                        for (int i = 0; i < b.getChildren().size(); i++) {
                            h += b.getChildren().get(i).getHeight() + 35;
                        }
                        RUheight -= (b.getHeight() + 20 + h);

                        UR = false;
                    } else {
                        b.point = new Point(b.parent.getDrawableShape().getBounds().right + off * 50, RDHehight + 40);
                        if (b.parent.getLines().get(b) != null) {
                            b.parent.getLines().get(b).setStart(new Point(b.parent.getDrawableShape().getBounds().right, b.parent.getDrawableShape().getBounds().centerY()));
                            b.parent.getLines().get(b).setEnd(new Point(b.parent.getDrawableShape().getBounds().right + 50, RDHehight + b.getHeight()));
                        } else {
                            Line l = null;
                            l = new Line(shape, Integer.parseInt(width), new ColorDrawable(Color.parseColor(color)),
                                    new Point(b.parent.getDrawableShape().getBounds().right, b.parent.getDrawableShape().getBounds().centerY()),
                                    new Point(b.parent.getDrawableShape().getBounds().right + off * 50, RDHehight + b.getHeight()), true);
                            b.parent.getLines().put(b, l);

                        }
                        int h = 0;
                        for (int i = 0; i < b.getChildren().size(); i++) {
                            h += b.getChildren().get(i).getHeight() + 35;
                        }
                        RDHehight += b.getHeight() + 20 + h;
                        UR = true;
                    }
                }
                b.position = Position.RIGHT;
                right--;
            } else {
                if (!(left % 2 == 0) && LDHehight == LUheight) {
                    b.point = new Point(b.parent.getDrawableShape().getBounds().left - off * 50 - b.getWidth(), b.parent.getDrawableShape().getBounds().centerY() - b.getHeight() / 2);
                    if (b.parent.getLines().get(b) != null) {
                        b.parent.getLines().get(b).setStart(new Point(b.parent.getDrawableShape().getBounds().left, b.parent.getDrawableShape().getBounds().centerY()));
                        b.parent.getLines().get(b).setEnd(new Point(b.parent.getDrawableShape().getBounds().left - 50 - b.getWidth(), b.parent.getDrawableShape().getBounds().centerY()));
                    } else {
                        b.parent.getLines().put(b, new Line(shape, Integer.parseInt(width), new ColorDrawable(Color.parseColor(color)),
                                new Point(b.parent.getDrawableShape().getBounds().left, b.parent.getDrawableShape().getBounds().centerY()),
                                new Point(b.parent.getDrawableShape().getBounds().left - off * 50, b.parent.getDrawableShape().getBounds().centerY()), true));
                    }
                    int h = 0;
                    for (int i = 0; i < b.getChildren().size(); i++) {
                        h += b.getChildren().get(i).getHeight() + 35;
                    }
                    LUheight -= (b.getHeight() / 2 + h);
                    LDHehight += b.getHeight() / 2 + h;
                } else {
                    if (UL) {
                        b.point = new Point(b.parent.getDrawableShape().getBounds().left - off * 50 - b.getWidth(), LUheight - 40 - b.getHeight());
                        if (b.parent.getLines().get(b) != null) {
                            b.parent.getLines().get(b).setStart(new Point(b.parent.getDrawableShape().getBounds().left, b.parent.getDrawableShape().getBounds().centerY()));
                            b.parent.getLines().get(b).setEnd(new Point(b.parent.getDrawableShape().getBounds().left - 50 - b.getHeight(), LUheight - b.getHeight()));
                        } else {
                            Line l = null;
                            l = new Line(shape, Integer.parseInt(width), new ColorDrawable(Color.parseColor(color)),
                                    new Point(b.parent.getDrawableShape().getBounds().left, b.parent.getDrawableShape().getBounds().centerY()),
                                    new Point(b.parent.getDrawableShape().getBounds().left - off * 50, LUheight - b.getHeight()), true);
                            l.off = 10;
                            b.parent.getLines().put(b, l);

                        }
                        int h = 0;
                        for (int i = 0; i < b.getChildren().size(); i++) {
                            h += b.getChildren().get(i).getHeight() + 35;
                        }
                        LUheight -= (b.getHeight() + 20 + h);
                        UL = false;
                    } else {
                        b.point = new Point(b.parent.getDrawableShape().getBounds().left - off * 50 - b.getWidth(), LDHehight + 40);
                        if (b.parent.getLines().get(b) != null) {
                            b.parent.getLines().get(b).setStart(new Point(b.parent.getDrawableShape().getBounds().right, b.parent.getDrawableShape().getBounds().centerY()));
                            b.parent.getLines().get(b).setEnd(new Point(b.parent.getDrawableShape().getBounds().right + 50, LDHehight + b.getHeight()));
                        } else {
                            Line l;
                            l = new Line(shape, Integer.parseInt(width), new ColorDrawable(Color.parseColor(color)),
                                    new Point(b.parent.getDrawableShape().getBounds().left, b.parent.getDrawableShape().getBounds().centerY()),
                                    new Point(b.parent.getDrawableShape().getBounds().left - off * 50, LDHehight + b.getHeight() / 2), true);
                            b.parent.getLines().put(b, l);

                        }
                        int h = 0;
                        for (int i = 0; i < b.getChildren().size(); i++) {
                            h += b.getChildren().get(i).getHeight() + 35;
                        }
                        LDHehight += b.getHeight() + 20 + h;
                        UL = true;
                    }
                }
                b.position = Position.LFET;
                left--;
            }
        } else {
            if (b.parent.drawableShape.getBounds().left <= MainActivity.root.drawableShape.getBounds().centerX()) {
                // b.position = Position.LFET;
                if (b.parent.getChildren().size() == 1) {
                    b.point = new Point(b.parent.getDrawableShape().getBounds().left - 20 - b.getWidth(), b.parent.getDrawableShape().getBounds().centerY() - b.getHeight() / 2);
                    if (b.parent.getLines().get(b) != null) {
                        b.parent.getLines().get(b).setStart(new Point(b.parent.getDrawableShape().getBounds().left, b.parent.getDrawableShape().getBounds().centerY()));
                        b.parent.getLines().get(b).setEnd(new Point(b.parent.getDrawableShape().getBounds().left - 20 - b.getWidth(), b.parent.getDrawableShape().getBounds().centerY()));
                    } else {
                        Line l = null;
                        l = new Line(shape, Integer.parseInt(width), new ColorDrawable(Color.parseColor(color)),
                                new Point(b.parent.getDrawableShape().getBounds().left, b.parent.getDrawableShape().getBounds().centerY()),
                                new Point(b.parent.getDrawableShape().getBounds().left - 20 - b.getWidth(), b.parent.getDrawableShape().getBounds().centerY()), true);
                        l.off = 10;
                        b.parent.getLines().put(b, l);

                    }
                } else {
                    int h = 0;
                    int start = 0;
                    int fStart = 0;
                    for (int i = 0; i < b.parent.getChildren().size(); i++) {
                        if (b.parent.getChildren().get(i).compareTo(b) == 0) {
                            start = h;
                        }
                        h += b.parent.getChildren().get(i).getHeight() + 35;
                    }
                    fStart = b.parent.getDrawableShape().getBounds().centerY() - h / 2;
                    b.point = new Point(b.parent.getDrawableShape().getBounds().left - 20 - b.getWidth(), fStart + start);
                    if (b.parent.getLines().get(b) != null) {
                        b.parent.getLines().get(b).setStart(new Point(b.parent.getDrawableShape().getBounds().left, b.parent.getDrawableShape().getBounds().centerY()));
                        b.parent.getLines().get(b).setEnd(new Point(b.parent.getDrawableShape().getBounds().left - 20, fStart + h / 2));
                    } else {
                        Line l = null;
                        l = new Line(shape, Integer.parseInt(width), new ColorDrawable(Color.parseColor(color)),
                                new Point(b.parent.getDrawableShape().getBounds().left, b.parent.getDrawableShape().getBounds().centerY()),
                                new Point(b.parent.getDrawableShape().getBounds().left - 20 - b.getWidth(), fStart + h / 2), true);
                        l.off = 10;
                        b.parent.getLines().put(b, l);

                    }
                }
            } else {
                //    b.position = Position.RIGHT;
                if (b.parent.getChildren().size() == 1) {
                    b.point = new Point(b.parent.getDrawableShape().getBounds().right + 20, b.parent.getDrawableShape().getBounds().centerY() - b.getHeight() / 2);
                    if (b.parent.getLines().get(b) != null) {
                        b.parent.getLines().get(b).setStart(new Point(b.parent.getDrawableShape().getBounds().right, b.parent.getDrawableShape().getBounds().centerY()));
                        b.parent.getLines().get(b).setEnd(new Point(b.parent.getDrawableShape().getBounds().right + 20, b.parent.getDrawableShape().getBounds().centerY()));
                    } else {
                        Line l = null;
                        l = new Line(shape, Integer.parseInt(width), new ColorDrawable(Color.parseColor(color)),
                                new Point(b.parent.getDrawableShape().getBounds().right, b.parent.getDrawableShape().getBounds().centerY()),
                                new Point(b.parent.getDrawableShape().getBounds().right + 20, b.parent.getDrawableShape().getBounds().centerY()), true);
                        l.off = 10;
                        b.parent.getLines().put(b, l);

                    }
                } else {
                    int h = 0;
                    int start = 0;
                    int fStart = 0;
                    for (int i = 0; i < b.parent.getChildren().size(); i++) {
                        if (b.parent.getChildren().get(i).compareTo(b) == 0) {
                            start = h;
                        }
                        h += b.parent.getChildren().get(i).getHeight() + 35;
                    }
                    fStart = b.parent.getDrawableShape().getBounds().centerY() - h / 2;
                    b.point = new Point(b.parent.getDrawableShape().getBounds().right + 20, fStart + start);
                    if (b.parent.getLines().get(b) != null) {
                        b.parent.getLines().get(b).setStart(new Point(b.parent.getDrawableShape().getBounds().right, b.parent.getDrawableShape().getBounds().centerY()));
                        b.parent.getLines().get(b).setEnd(new Point(b.parent.getDrawableShape().getBounds().right + 20, fStart + h / 2));
                    } else {
                        Line l = null;
                        l = new Line(shape, Integer.parseInt(width), new ColorDrawable(Color.parseColor(color)),
                                new Point(b.parent.getDrawableShape().getBounds().right, b.parent.getDrawableShape().getBounds().centerY()),
                                new Point(b.parent.getDrawableShape().getBounds().right + 20, fStart + h / 2), true);
                        l.off = 10;
                        b.parent.getLines().put(b, l);

                    }
                }
            }
        }
    }

    public void drawRelationship(Box box1, IRelationship rel) {
        Path path = new Path();
        PathEffect effect = null;
        Box box2 = box1.relationships.get(rel);
        if (box1.drawableShape.getBounds().left > box2.drawableShape.getBounds().left) {
            path.moveTo(box2.getDrawableShape().getBounds().right, box2.getDrawableShape().getBounds().centerY());
            path.lineTo(box1.getDrawableShape().getBounds().left, box1.getDrawableShape().getBounds().centerY());

            effect = new PathDashPathEffect(
                    makeConvexArrow2(24.0f, 14.0f),    // "stamp"
                    36.0f,                            // advance, or distance between two stamps
                    0.0f,                             // phase, or offset before the first stamp
                    PathDashPathEffect.Style.ROTATE); // how to transform each stamp
        } else {
            path.moveTo(box1.getDrawableShape().getBounds().right, box1.getDrawableShape().getBounds().centerY());
            path.lineTo(box2.getDrawableShape().getBounds().left, box2.getDrawableShape().getBounds().centerY());
            effect = new PathDashPathEffect(
                    makeConvexArrow(24.0f, 14.0f),    // "stamp"
                    36.0f,                            // advance, or distance between two stamps
                    0.0f,                             // phase, or offset before the first stamp
                    PathDashPathEffect.Style.ROTATE); // how to transform each stamp
        }
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.STROKE);
        paint.setPathEffect(effect);
        canvas.drawPath(path, paint);
        Paint paint1 = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(13);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawTextOnPath(rel.getTitleText(), path, 20, 20, paint1);
        if (MainActivity.allRelationship.containsKey(path)) {
            MainActivity.allRelationship.remove(path);
        }
        MainActivity.allRelationship.put(path, new Pair(rel, box1));
    }

    private Path makeConvexArrow(float length, float height) {
        Path p = new Path();
        p.moveTo(0.0f, -height / 4.0f);;
        p.lineTo(length, 0.0f);
        p.lineTo(0.0f, height / 4.0f);
        p.lineTo(0.0f + height / 8.0f, 0.0f);
        p.close();
        return p;
    }

    private Path makeConvexArrow2(float length, float height) {
        Path p = new Path();
        p.moveTo(0.0f, -height / 4.0f);
        p.lineTo(-length, 0.0f);
        p.lineTo(0.0f, height / 4.0f);
        p.lineTo(0.0f + height / 8.0f, 0.0f);
        p.close();
        return p;
    }
}