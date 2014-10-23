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

package edu.agh.klaukold.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import edu.agh.R;
import edu.agh.idziak.WorkbookHandler;
import edu.agh.klaukold.commands.AddBox;
import edu.agh.klaukold.commands.EditBox;
import edu.agh.klaukold.commands.EditSheet;
import edu.agh.klaukold.commands.RemoveBox;
import edu.agh.klaukold.commands.RemoveLine;
import edu.agh.klaukold.common.Box;
import edu.agh.klaukold.common.Line;
import edu.agh.klaukold.common.Root;
import edu.agh.klaukold.common.Sheet;
import edu.agh.klaukold.enums.Actions;;
import edu.agh.klaukold.enums.Position;
import edu.agh.klaukold.interfaces.Command;
import edu.agh.klaukold.utilities.AsyncInvalidate;
import edu.agh.klaukold.utilities.Callback;
import edu.agh.klaukold.utilities.DialogFactory;
import edu.agh.klaukold.utilities.Utils;


import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RotateDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.ActionMode;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.xmind.core.ISheet;
import org.xmind.core.ITopic;
import org.xmind.core.IWorkbook;
import org.xmind.core.style.IStyle;
import org.xmind.core.style.IStyleSheet;
import org.xmind.ui.style.Styles;

public class MainActivity extends Activity {

    private GestureDetector gestureDetector;
    public static DrawView lay;
    public static ActionMode mActionMode;
    private boolean mIsScrolling = false;
    private GestureListener gestList = new GestureListener();
    public static Box root;
    public static Sheet sheet = new Sheet();
    public static Box boxEdited;
    public static boolean EDIT_CONN = false;
    public static LinkedList<Box> toEditBoxes = new LinkedList<Box>();
    public static Properties properties = new Properties();
    private static Pair<Box, Actions> pair;

    public static LinkedList<Command> commandsUndo = new LinkedList<Command>();
    public static LinkedList<Command> commandsRedo = new LinkedList<Command>();
    private static Menu menu;
    private static Resources res;


    public static int id = 1;

    private PointF mid = new PointF();
    private ScaleGestureDetector detector;

    public final static String BACKGROUNDCOLOR = "COLOR";
    public final static String INTENSIVITY = "INTENSIVITY";

    private String style;

    ///---------------------------------------
    public static ISheet sheet1;
    public static ITopic rootTopic;
    public static IWorkbook workbook;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lay = (DrawView) findViewById(R.id.myLay);
        res = getResources();
        WorkbookHandler handler = WorkbookHandler.createNewWorkbook();

        // XMind API ponizej
        workbook = handler.getWorkbook();

        sheet1 = workbook.getPrimarySheet();
        rootTopic = sheet1.getRootTopic();
        // Klasa przechowujaca wszystkie style. Wiele elementów może mieć ten sam styl.
        IStyleSheet styleSheet = workbook.getStyleSheet();
        // Tworzymy styl dla topica
        IStyle style1 = styleSheet.createStyle(IStyle.TOPIC);
        if (root == null) {
            root = new Root();
            Intent intent = getIntent();
            style = intent.getStringExtra(WelcomeScreen.STYLE);
            Resources res = getResources();
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x / 3;
            int height = size.y / 3;
            root.setPoint(new edu.agh.klaukold.common.Point(width, height));
            if (style.equals("Default")) {
                // Edytujemy styl (możliwe wartości masz na stronce UsingXMindAPI):
                style1.setProperty(Styles.TextColor, String.valueOf(res.getColor(R.color.black))); // trzeba podać kolor w formacie "0xffffff"
                style1.setProperty(Styles.FillColor, String.valueOf(res.getColor(R.color.blue)));
                style1.setProperty(Styles.ShapeClass, Styles.TOPIC_SHAPE_ROUNDEDRECT);
                style1.setProperty(Styles.FontSize, "13pt");
                style1.setProperty(Styles.TextAlign, Styles.ALIGN_CENTER);
                style1.setProperty(Styles.FontFamily, "Times New Roman");
                style1.setProperty(Styles.LineClass, Styles.BRANCH_CONN_STRAIGHT);
                style1.setProperty(Styles.LineWidth, "1pt");
                style1.setProperty(Styles.LineColor, String.valueOf(Color.rgb(128, 128, 128)));
                style1.setProperty(Styles.FontFamily, "Times New Roman");
                rootTopic.setTitleText("Central Topic");
                // Dodajemy styl do arkusza styli
                styleSheet.addStyle(style1, IStyleSheet.NORMAL_STYLES);
                // Nadajemy topikowi dany styl przez podanie ID
                rootTopic.setStyleId(style1.getId());
                root.topic = rootTopic;
                root.topic.setFolded(false);
                root.setDrawableShape((GradientDrawable) res.getDrawable(R.drawable.round_rect));
                IStyle style2 = styleSheet.createStyle(IStyle.SUMMARY);
                style2.setProperty(Styles.FillColor, String.valueOf(res.getColor(R.color.white)));
                styleSheet.addStyle(style2, IStyleSheet.NORMAL_STYLES);
                sheet1.setStyleId(style2.getId());
            } else if (style.equals("Classic")) {
                // Edytujemy styl (możliwe wartości masz na stronce UsingXMindAPI):
                style1.setProperty(Styles.TextColor, String.valueOf(res.getColor(R.color.black))); // trzeba podać kolor w formacie "0xffffff"
                style1.setProperty(Styles.FillColor, String.valueOf(res.getColor(R.color.lime_green)));
                style1.setProperty(Styles.ShapeClass, Styles.TOPIC_SHAPE_ELLIPSE);
                style1.setProperty(Styles.FontSize, "13pt");
                style1.setProperty(Styles.TextAlign, Styles.ALIGN_CENTER);
                style1.setProperty(Styles.FontFamily, "Times New Roman");
                style1.setProperty(Styles.LineClass, Styles.BRANCH_CONN_CURVE);
                style1.setProperty(Styles.LineWidth, "1pt");
                style1.setProperty(Styles.LineColor, String.valueOf(Color.rgb(128, 128, 128)));
                rootTopic.setTitleText("Central Topic");
                style1.setProperty(Styles.FontFamily, "Times New Roman");
                // Dodajemy styl do arkusza styli
                styleSheet.addStyle(style1, IStyleSheet.NORMAL_STYLES);
                // Nadajemy topikowi dany styl przez podanie ID
                rootTopic.setStyleId(style1.getId());
                root.topic = rootTopic;
                root.topic.setFolded(false);
                root.setDrawableShape((GradientDrawable) res.getDrawable(R.drawable.elipse));
                IStyle style2 = styleSheet.createStyle(IStyle.SUMMARY);
                style2.setProperty(Styles.FillColor, String.valueOf(res.getColor(R.color.light_yellow)));
                styleSheet.addStyle(style2, IStyleSheet.NORMAL_STYLES);
                sheet1.setStyleId(style2.getId());
            } else if (style.equals("Simple")) {
                style1.setProperty(Styles.TextColor, String.valueOf(res.getColor(R.color.black))); // trzeba podać kolor w formacie "0xffffff"
                style1.setProperty(Styles.FillColor, String.valueOf(res.getColor(R.color.white)));
                style1.setProperty(Styles.ShapeClass, Styles.TOPIC_SHAPE_ELLIPSE);
                style1.setProperty(Styles.FontSize, "13pt");
                style1.setProperty(Styles.TextAlign, Styles.ALIGN_CENTER);
                style1.setProperty(Styles.FontFamily, "Times New Roman");
                style1.setProperty(Styles.LineClass, Styles.BRANCH_CONN_CURVE);
                style1.setProperty(Styles.LineWidth, "1pt");
                style1.setProperty(Styles.LineColor, String.valueOf(Color.rgb(128, 128, 128)));
                rootTopic.setTitleText("Central Topic");
                style1.setProperty(Styles.FontFamily, "Times New Roman");
                // Dodajemy styl do arkusza styli
                styleSheet.addStyle(style1, IStyleSheet.NORMAL_STYLES);
                // Nadajemy topikowi dany styl przez podanie ID
                rootTopic.setStyleId(style1.getId());
                root.topic = rootTopic;
                root.topic.setFolded(false);
                root.setDrawableShape((GradientDrawable) res.getDrawable(R.drawable.elipse));
                IStyle style2 = styleSheet.createStyle(IStyle.SUMMARY);
                style2.setProperty(Styles.FillColor, String.valueOf(res.getColor(R.color.white)));
                styleSheet.addStyle(style2, IStyleSheet.NORMAL_STYLES);
                sheet1.setStyleId(style2.getId());
            } else if (style.equals("Business")) {
                style1.setProperty(Styles.TextColor, String.valueOf(res.getColor(R.color.black))); // trzeba podać kolor w formacie "0xffffff"
                style1.setProperty(Styles.FillColor, String.valueOf(res.getColor(R.color.light_yellow)));
                style1.setProperty(Styles.ShapeClass, Styles.TOPIC_SHAPE_ROUNDEDRECT);
                style1.setProperty(Styles.FontSize, "13pt");
                style1.setProperty(Styles.TextAlign, Styles.ALIGN_CENTER);
                style1.setProperty(Styles.FontFamily, "Times New Roman");
                style1.setProperty(Styles.LineClass, Styles.BRANCH_CONN_CURVE);
                style1.setProperty(Styles.LineWidth, "1pt");
                style1.setProperty(Styles.LineColor, String.valueOf(Color.rgb(128, 128, 128)));
                rootTopic.setTitleText("Central Topic");
                style1.setProperty(Styles.FontFamily, "Times New Roman");
                // Dodajemy styl do arkusza styli
                styleSheet.addStyle(style1, IStyleSheet.NORMAL_STYLES);
                // Nadajemy topikowi dany styl przez podanie ID
                rootTopic.setStyleId(style1.getId());
                root.topic = rootTopic;
                root.topic.setFolded(false);
                root.setDrawableShape((GradientDrawable) res.getDrawable(R.drawable.round_rect));
                IStyle style2 = styleSheet.createStyle(IStyle.SUMMARY);
                style2.setProperty(Styles.FillColor, String.valueOf(res.getColor(R.color.white)));
                styleSheet.addStyle(style2, IStyleSheet.NORMAL_STYLES);
                sheet1.setStyleId(style2.getId());
            } else if (style.equals("Academese")) {
                style1.setProperty(Styles.TextColor, String.valueOf(res.getColor(R.color.white))); // trzeba podać kolor w formacie "0xffffff"
                style1.setProperty(Styles.FillColor, String.valueOf(res.getColor(R.color.dark_gray)));
                style1.setProperty(Styles.ShapeClass, Styles.TOPIC_SHAPE_RECT);
                style1.setProperty(Styles.FontSize, "13pt");
                style1.setProperty(Styles.TextAlign, Styles.ALIGN_CENTER);
                style1.setProperty(Styles.FontFamily, "Times New Roman");
                style1.setProperty(Styles.LineClass, Styles.BRANCH_CONN_CURVE);
                style1.setProperty(Styles.LineWidth, "1pt");
                style1.setProperty(Styles.LineColor, String.valueOf(res.getColor(R.color.white)));
                rootTopic.setTitleText("Central Topic");
                style1.setProperty(Styles.FontFamily, "Times New Roman");
                // Dodajemy styl do arkusza styli
                styleSheet.addStyle(style1, IStyleSheet.NORMAL_STYLES);
                // Nadajemy topikowi dany styl przez podanie ID
                rootTopic.setStyleId(style1.getId());
                root.topic = rootTopic;
                root.topic.setFolded(false);
                root.setDrawableShape((GradientDrawable) res.getDrawable(R.drawable.rect));
                //todo dodac kolor tla
                IStyle style2 = styleSheet.createStyle(IStyle.SUMMARY);
                style2.setProperty(Styles.FillColor, String.valueOf(res.getColor(R.color.dark_gray)));
                styleSheet.addStyle(style2, IStyleSheet.NORMAL_STYLES);
                sheet1.setStyleId(style2.getId());
            }


//                root.setLineStyle(LineStyle.STRAIGHT);
//                root.setLineColor(Color.rgb(128, 128, 128));
//                root.setLineThickness(LineThickness.THINNEST);
//                sheet.setColor(new ColorDrawable(Color.WHITE));
//                sheet.setIntensivity(0);
            // root.setDrawableShape((RotateDrawable)res.getDrawable(R.drawable.diammond));
            //RorateDrawable dla diamond
            // root.setDrawableShape((GradientDrawable)res.getDrawable(R.drawable.rect));
            //TODO dopisac cechy stylu
        }
        // }
        //root.draw();
        gestureDetector = new GestureDetector(this, gestList);
//Object r =  findViewById(R.id.action_settings);
//        View.OnClickListener onClickListener = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, EditSheetScreen.class);
//                intent.putExtra(SHEET, sheet);
//                startActivity(intent);
//            }
//        };
        // settings.setOnClickListener(onClickListener);
        Utils.lay = lay;
        Utils.context = this;
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//		
        lay.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case (MotionEvent.ACTION_OUTSIDE):
                        return true;

                    case (MotionEvent.ACTION_UP):
                        //  gestList.click = false;
                        //if (mIsScrolling) {
//                            String txt = "default text";
//                            Text t = new Text();
//                            t.setText(txt);
//                            gestList.myRect.setText(t);
//
//                            gestList.myRect.setParent(gestList.clicked);
                        //gestList.myRect.setId(Utils.giveId()+"");

                        //editContent(gestList.myRect);

//						if(gestList.clicked.getId().equals(root.getId())) {
//							if(core.mid_x < gestList.myRect.rect.left) {
//								gestList.myRect.position = Position.RIGHT;
//								lay.updateRight = true;
//							} else {
//								gestList.myRect.position = Position.LEFT;
//								lay.updateLeft = true;
//							}

                        //root.addChild(gestList.myRect);
                        //	lay.addLine(gestList.clicked, gestList.myRect);
                        //	Utils.db.insertTopic(gestList.myRect);
                        //	Utils.db.updateCore(core);
                        //   } else {
//							if(gestList.clicked.position == Position.LEFT) {
//								gestList.myRect.position = Position.LEFT;
//								lay.updateLeft = true;
//							} else {
//								gestList.myRect.position = Position.RIGHT;
//								lay.updateRight = true;
                        //    }

                        //	gestList.clicked.addChild(gestList.myRect);
                        //	lay.addLine(gestList.clicked, gestList.myRect);
                        //Utils.db.insertTopic(gestList.myRect);
                        //   //Utils.db.updateTopic(gestList.clicked);
                        // lay.revalidate();
                        //   lay.invalidate();

                        //   mIsScrolling = false;

                        //    gestList.clicked = null;
                        //    gestList.myRect = new Box();

                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        // multitouch!! - touch down
                        int count = event.getPointerCount(); // Number of 'fingers' in this time

                        if (count > 1) {
                            Box b1 = Utils.whichBox(lay, event, 0);
                            Box b2 = Utils.whichBox(lay, event, 1);

                            //Rozpoznajemy czy przepinanie, czy rozpinanie, czy przesuwanie
//                            if (b1 != null && b2 != null) {
//                                if (b1.getPoint().compareTo(b2.getPoint()) == 0) {
//                                    if (b1.getPoint().compareTo(root.getPoint()) == 0) {
//                                        mIsScrolling = false;
//                                        return true;
//                                    }
//
//                                    if (mActionMode == null) {
//                                       // mActionMode = startActionMode(moveCallback);
//                                        mActionMode.setTitle("Move");
//                                    }
//
//                                    if (mActionMode.getTitle().toString().equalsIgnoreCase("move")) {
//                                     //   boolean b = b1.isSelected();
//
////                                        if (b) {
////                                           // moveCallback.removeObserver();
////                                        } else {
////                                            //moveCallback.setObserver(b1);
////                                        }//
//
//                                        lay.revalidate();
//                                        lay.invalidate();
//                                    }
//
//                                    mIsScrolling = false;
//                                    return true;
//                                }
//
//                                if(Utils.changeParent(b1, b2)) {
//                                    lay.revalidate();
//                                    lay.invalidate();
//                                }

//                                mIsScrolling = false;
//                                return true;
//                            } else if (b1 != null && b2 == null) {
//                                if (mActionMode == null) {
//                                    Utils.unlink(b1, Utils.getCoordsInView(lay, event, 1));
//                                    lay.revalidate();
//                                    lay.invalidate();
//                                }
//
//                                mIsScrolling = false;
//                                return true;
//                            } else {
//                                return detector.onTouchEvent(event);
//                            }
                        }
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        if (event.getPointerCount() > 1) {
                            return detector.onTouchEvent(event);
                        }

                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (event.getPointerCount() > 1) {
                            return detector.onTouchEvent(event);
                        }
                        break;
                    default:
                        break;
                }

                boolean response = gestureDetector.onTouchEvent(event);
                lay.requestFocus();
                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(lay.getApplicationWindowToken(), 0);

                return response;
            }
        });
//		
        //menu domyślne
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.show();
//
        detector = new ScaleGestureDetector(this, new SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                lay.setPivotX(mid.x);
                lay.setPivotY(mid.y);
                lay.zoomx *= detector.getScaleFactor();
                lay.zoomy *= detector.getScaleFactor();
                lay.revalidate();
                lay.invalidate();
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        IStyle style = MainActivity.workbook.getStyleSheet().findStyle(sheet1.getStyleId());
        lay.setBackgroundColor(Integer.parseInt(style.getProperty(Styles.FillColor)));
    }

    //tutaj rozpoznajemy przytrzymanie, jedno kliknięcie, dwa kliknięcia
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        Box myRect = new Box();
        Box clicked;
        boolean click = false;

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            if (MainActivity.EDIT_CONN) {
                Box box = Utils.whichLine(lay, event, 0);
                if (box != null) {
                    properties.put("box", box);
                    RemoveLine removeLine = new RemoveLine();
                    removeLine.execute(properties);
                    MainActivity.addCommendUndo(removeLine);
                    properties = new Properties();
                    Callback call = new Callback() {
                        @Override
                        public void execute() {
//                            RemoveLine removeLine = new RemoveLine();
//                            removeLine.execute(properties);
//                            MainActivity.addCommendUndo(removeLine);
//                            properties = new Properties();
                        }
                    };
                    try {
                        AsyncInvalidate async = new AsyncInvalidate(MainActivity.this);
                        async.setCallback(call);
                        async.execute();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
            pair = Utils.whichBoxAction(lay, event);
            Box box = Utils.whichBox(lay, event);
            if (box != null) {
                box.isSelected = true;
                MainActivity.boxEdited = box;
                if (MainActivity.boxEdited != null) {
                    MainActivity.toEditBoxes.add(box);
                }
                lay.invalidate();
                menu.getItem(2).setVisible(true);
                menu.getItem(3).setVisible(true);
            } else if (box == null) {

                root.isSelected = false;

                for (int i = 0; i < root.getChildren().size(); i++) {
                    fireUnSelect(root.getChildren().get(i));
                }
                MainActivity.toEditBoxes = new LinkedList<Box>();
                menu.getItem(2).setVisible(false);
                menu.getItem(3).setVisible(false);
                lay.invalidate();
            }
            if (pair != null) {

                if (pair.second == Actions.ADD_BOX) {
                    boxEdited = pair.first;
                    Box box1 = new Box();
                    AddBox addBox = new AddBox();
                    Properties properties = new Properties();
                    properties.put("box", MainActivity.boxEdited);
                    properties.put("new_box", box1);
                    properties.put("root", root);
                    properties.put("res", res);
                    properties.put("style", style);
                    addBox.execute(properties);
                    MainActivity.addCommendUndo(addBox);
                    editContent(box1);
                    lay.revalidate();
                    lay.invalidate();

//                    Intent intent = new Intent(MainActivity.this, EditBoxScreen.class);
//                    intent.putExtra(EditBoxScreen.BOX_COLOR, pair.first.getColor().getColor());
//                    intent.putExtra(EditBoxScreen.TEXT_COLOR, pair.first.getText().getColor().getColor());
//                    intent.putExtra(EditBoxScreen.LINE_SHAPE, pair.first.getShape());
//                    intent.putExtra(EditBoxScreen.LINE_COLOR, pair.first.getLineColor());
//                    intent.putExtra(EditBoxScreen.LINE_SHAPE, pair.first.getLineStyle());
//                    intent.putExtra(EditBoxScreen.BOX_SHAPE, pair.first.getShape());
//                    intent.putExtra(EditBoxScreen.LINE_THICKNESS, pair.first.getLineThickness());
//                    startActivity(intent);
//                   // lay.revalidate();
//                   // lay.invalidate();
                } else if (pair.second == Actions.NEW_NOTE) {

                } else if (pair.second == Actions.NEW_MARKER) {

                } else if (pair.second == Actions.COLLAPSE) {
                    Callback call = new Callback() {
                        @Override
                        public void execute() {
                            Utils.fireSetVisible(pair.first, true);
                        }
                    };
                    try {
                        AsyncInvalidate async = new AsyncInvalidate(MainActivity.this);
                        async.setCallback(call);
                        async.execute();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                } else if (pair.second == Actions.EXPAND) {
                    Callback call = new Callback() {
                        @Override
                        public void execute() {
                            Utils.fireSetVisible(pair.first, false);
                        }
                    };
                    try {
                        AsyncInvalidate async = new AsyncInvalidate(MainActivity.this);
                        async.setCallback(call);
                        async.execute();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
//            if(mActionMode == null && clicked != null) {
//            	if(clicked.isVisible()) {
//            		//clicked.changeDescendantsVisibility();
//            	}
//            	clicked = null;
//            	lay.revalidate();
//            	lay.invalidate();
//            }
//            
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
//            if (!click || Utils.whichBox(lay, e) == null) {
//                return;
//            }
//
//            if (mActionMode == null) {
//                mActionMode = startActionMode(moveCallback);
//                mActionMode.setTitle("Move");
//            }
//
//            if (mActionMode.getTitle().toString().equalsIgnoreCase("move")) {
//                boolean b = clicked.isSelected();
//                if (b) {
//                    //callback.removeObserver(clicked);
//                    moveCallback.removeObserver();
//                } else {
//                    moveCallback.setObserver(clicked);
//                }
//
//                lay.invalidate();
//            }

        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (mActionMode != null) {
//                if (clicked != null && click) {
//                    int newx = (int) (e2.getX() - e1.getX());
//                    int newy = (int) (e2.getY() - e1.getY());
//
//                    newx /= lay.zoomx;
//                    newy /= lay.zoomy;
//
//                    newx = (int) -distanceX;
//                    newy = (int) -distanceY;
//
//                    clicked.setPoint(new edu.agh.klaukold.common.Point(newx, newy));
//                    if (clicked.getLines().size() > 0) {
//                        for (Box box : clicked.getLines().keySet()) {
//                            if (box.position == Position.LFET) {
//                                clicked.getLines().get(box).setStart(new edu.agh.klaukold.common.Point(newx, newy));
//                            } else {
//                                clicked.getLines().get(box).setStart(new edu.agh.klaukold.common.Point(newx + clicked.getWidth(), newy + clicked.getHeight()));
//                            }
//                        }
//                    }
//                    if (clicked.getParent() != null) {
//                        if (clicked.position == Position.LFET) {
//                            clicked.getParent().getLines().get(clicked).setEnd(new edu.agh.klaukold.common.Point(newx, newy));
//                        } else {
//                            clicked.getParent().getLines().get(clicked).setEnd(new edu.agh.klaukold.common.Point(newx + clicked.getDrawableShape().getBounds().left, newy + clicked.getHeight()));
//                        }
//                    }
//                    Utils.moveChildX(clicked, newx);
//                    Utils.moveChildY(clicked, newy);
//                    lay.revalidate();
//                    lay.invalidate();
//                    return true;
//                }
            } else if (click && clicked != null) {
                mIsScrolling = true;
                int newx = (int) (e2.getX() - lay.transx);
                int newy = (int) (e2.getY() - lay.transy);

                newx /= lay.zoomx;
                newy /= lay.zoomy;

                clicked.setPoint(new edu.agh.klaukold.common.Point(newx, newy));
                clicked.setPoint(new edu.agh.klaukold.common.Point(newx, newy));
                clicked.prepareDrawableShape();
                if (clicked.getLines().size() > 0) {
                    for (Box box : clicked.getLines().keySet()) {
                        if (box.position == Position.LFET) {
                            clicked.getLines().get(box).setStart(new edu.agh.klaukold.common.Point(clicked.getDrawableShape().getBounds().left, clicked.getDrawableShape().getBounds().top + clicked.getHeight() / 2));
                        } else {
                            clicked.getLines().get(box).setStart(new edu.agh.klaukold.common.Point(clicked.getDrawableShape().getBounds().right, clicked.getDrawableShape().getBounds().top + clicked.getHeight() / 2));
                        }
                    }
                }
                //to napisac wszystko od nowa zwiazanego z polaczeniami
                if (clicked.parent != null) {
                    if (clicked.parent.getLines().get(clicked) != null) {
                        if (clicked.parent.topic.isRoot()) {
                            if (clicked.position == Position.LFET) {
                                clicked.parent.getLines().get(clicked).setEnd(new edu.agh.klaukold.common.Point(clicked.getDrawableShape().getBounds().right, clicked.getDrawableShape().getBounds().top + clicked.getHeight() / 2));
                            } else {
                                clicked.parent.getLines().get(clicked).setEnd(new edu.agh.klaukold.common.Point(clicked.getDrawableShape().getBounds().left, clicked.getDrawableShape().getBounds().top + clicked.getHeight() / 2));
                            }
                        } else {
                            if (clicked.position == Position.LFET) {
                                clicked.parent.getLines().get(clicked).setEnd(new edu.agh.klaukold.common.Point(clicked.getDrawableShape().getBounds().left, clicked.getDrawableShape().getBounds().top + clicked.getHeight() / 2));
                            } else {
                                clicked.parent.getLines().get(clicked).setEnd(new edu.agh.klaukold.common.Point(clicked.getDrawableShape().getBounds().right, clicked.getDrawableShape().getBounds().top + clicked.getHeight() / 2));
                            }
                        }
                    }
                }
//                LinkedList<Box> boxes = Utils.allBoxes();
//                if (MainActivity.EDIT_CONN) {
//                    for (Box b : boxes) {
//                        if (clicked.getDrawableShape().getBounds().intersect(b.getDrawableShape().getBounds())) {
//                            clicked.setParent(b);
//                            b.addChild(clicked);
//                            b.getLines().put(clicked, new Line(b.getLineStyle(), (int) b.getLineThickness().getValue(), b.getColor(), new edu.agh.klaukold.common.Point(0, 0), new edu.agh.klaukold.common.Point(0, 0), true));
//                        }
//                    }
//                }
//                if (clicked.parent != null) {
//                    if (clicked.parent.getLines().get(clicked) != null) {
//                        if (clicked.parent.topic.isRoot()) {
//                            if (clicked.position == Position.LFET) {
//                                clicked.parent.getLines().get(clicked).setEnd(new edu.agh.klaukold.common.Point(clicked.getDrawableShape().getBounds().right, clicked.getDrawableShape().getBounds().top + clicked.getHeight() / 2));
//                            } else {
//                                clicked.parent.getLines().get(clicked).setEnd(new edu.agh.klaukold.common.Point(clicked.getDrawableShape().getBounds().left, clicked.getDrawableShape().getBounds().top + clicked.getHeight() / 2));
//                            }
//                        } else {
//                            if (clicked.position == Position.LFET) {
//                                clicked.parent.getLines().get(clicked).setEnd(new edu.agh.klaukold.common.Point(clicked.getDrawableShape().getBounds().left, clicked.getDrawableShape().getBounds().top + clicked.getHeight() / 2));
//                            } else {
//                                clicked.parent.getLines().get(clicked).setEnd(new edu.agh.klaukold.common.Point(clicked.getDrawableShape().getBounds().right, clicked.getDrawableShape().getBounds().top + clicked.getHeight() / 2));
//                            }
//                        }
//                    }
//                }
                lay.revalidate();
                lay.invalidate();
                //Rect r = new Rect(newx, newy, newx + 100, newy + 50);

                //myRect.rect.set();
                return false;
            }

            lay.transx -= distanceX;
            lay.transy -= distanceY;
            lay.invalidate();
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            if (mActionMode != null && mActionMode.getTitle().toString().equalsIgnoreCase("move")) {
                if (Utils.whichBox(lay, e) == clicked) {
                    click = true;
                }
            } else {
                clicked = Utils.whichBox(lay, e);
                if (clicked != null) {
                    click = true;
                }
            }
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (mActionMode != null && mActionMode.getTitle().toString().equalsIgnoreCase("move")) {
                return true;
            }

            if (Utils.whichBox(lay, e) != null) {
                editContent(Utils.whichBox(lay, e));
                return true;
            }
//
//	        final Dialog dialog = DialogFactory.boxContentDialog(MainActivity.this);
//	        final EditText et = (EditText) dialog.findViewById(R.id.editText);
//	        et.requestFocus();

            //final Box myClicked = new Box();
            // myClicked.create(Utils.getCoordsInView(lay, e, 0));
            // myClicked.getText().setText("default text");

//	        if(root.getMidX() < (myClicked.getDrawableShape().getBounds().left + myClicked.getDrawableShape().getBounds().right)/2) {
//	        //	myClicked.position = Position.RIGHT;
//				lay.updateRight = true;
//			} else {
//			//	myClicked.position = Position.LEFT;
//				lay.updateLeft = true;
//			}

            // myClicked.setId(Utils.giveId()+"");
            // MainActivity.root.addChild(myClicked);

            //myClicked.setTimestamp(new Date().getTime());

            // lay.triggerLastDetachMove();
            // lay.revalidate();

//	        try {
//	        	Callback call = new Callback() {
//					@Override
//					public void execute() {
//						//.db.insertTopic(myClicked);
//				        //Utils.db.updateCore(core);
//					}
//				};
//
//				AsyncInvalidate async = new AsyncInvalidate(MainActivity.this);
//				async.setCallback(call);
//				async.execute();
//			} catch (Exception e1) {
//				e1.printStackTrace();
//			}

            // editContent(myClicked);
            return true;
        }
    }

    private class DeleteBoxCallback implements ActionMode.Callback {
        List<Box> observers = new ArrayList<Box>();

        public void addObserver(Box box) {
            if (!observers.contains(box)) {
                box.isSelected = (true);
                observers.add(box);
            }
        }

        public void removeObserver(Box box) {
            observers.remove(box);
            box.isSelected = (false);
            if (observers.isEmpty()) {
                mActionMode.finish();
            }
        }

        private void notifyObservers() {
            for (Box box : observers) {
                box.isSelected = (false);
            }
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // inflate contextual menu
            mode.getMenuInflater().inflate(R.menu.context_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//            switch (item.getItemId()) {
//            	case R.id.menu_delete:
//
//            	for(Box v: observers) {
//            		if(v instanceof Core) {
//            			continue;
//            		}
//
//            		//lay.deleteHimAndChildren(v);
//            		//Utils.db.deleteChild(v.getId());
//            	}
//
//                mode.finish(); // Action picked, so close the CAB
//                return true;
//            default:
//                return false;
//            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
//            // remove selection
            notifyObservers();
            observers.clear();

            lay.revalidate();
            lay.invalidate();
            mActionMode = null;
        }
    }

    //   private class MoveBoxCallback implements ActionMode.Callback {
//        Box observer;
//
//        public void setObserver(Box box) {
//            if (observer == null && !(box instanceof Root)) {
//                observer = box;
//                observer.setSelected(true);
//            }
//        }
//
//        public void removeObserver() {
//            mActionMode.finish();
//        }
//
//        private void notifyObserver() {
//            if (observer != null) {
//                observer.setSelected(false);
//                determinePosition();
//            }
//        }
//
//        private void determinePosition() {
//            if (observer.getParent() instanceof Root) {
//                Root core = (Root) observer.getParent();
//
//                if (observer.getPoint() != null) {
//                    observer.getPoint().x = observer.getDrawableShape().getBounds().left;
//                    observer.getPoint().y = observer.getDrawableShape().getBounds().top;
////                    if ((observer.getDrawableShape().getBounds().left + observer.getDrawableShape().getBounds().right) / 2 < core.getPoint().x/2) {
////                         observer.position = Position.LEFT;
////                        ;
////                    } else {
////                       // observer.position = Position.RIGHT;
////                    }
//                    Utils.propagatePosition(observer, observer.getPoint());
//                    return;
//                }
//
//                core.getRightChildren().remove(observer);
//                core.getLeftChildren().remove(observer);
//
//                if ((core.getPoint().x / 2) < observer.getDrawableShape().getBounds().left) {
//                    Utils.propagatePosition(observer, core.getPoint());
//                    lay.updateRight = true;
//
//                    int ind = core.getRightChildren().size();
//
//                    for (int i = 0; i < core.getRightChildren().size(); i++) {
//                        if (core.getRightChildren().get(i).getDrawableShape().getBounds().top > observer.getDrawableShape().getBounds().top) {
//                            ind = i;
//                            break;
//                        }
//                    }
//
//                    core.getRightChildren().add(ind, observer);
//                } else {
//                    // Utils.propagatePosition(observer, Position.LEFT);
//                    Utils.propagatePosition(observer, observer.getPoint());
//                    lay.updateLeft = true;
//
//                    int ind = core.getLeftChildren().size();
//
//                    for (int i = 0; i < core.getLeftChildren().size(); i++) {
//                        if (core.getLeftChildren().get(i).getDrawableShape().getBounds().top > observer.getDrawableShape().getBounds().top) {
//                            ind = i;
//                            break;
//                        }
//                    }
//
//                    core.getLeftChildren().add(ind, observer);
//                }
//            } else {
//                List<ITopic> siblings = observer.getParent().getChildren();
//                siblings.remove(observer);
//
//                int ind = siblings.size();
//
//                for (int i = 0; i < siblings.size(); i++) {
//                    if (siblings.get(i).getDrawableShape().getBounds().top > observer.getDrawableShape().getBounds().top) {
//                        ind = i;
//                        break;
//                    }
//                }
//
//                siblings.add(ind, observer);
//            }
//        }
//
//        @Override
//        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//            return true;
//        }
//
//        @Override
//        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//            return false;
//        }
//
//        @Override
//        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//            switch (item.getItemId()) {
//                case R.menu.map_menu:
//
//                    mode.finish();
//                    return true;
//                default:
//                    return false;
//            }
//        }
//
//        @Override
//        public void onDestroyActionMode(ActionMode mode) {
//            notifyObserver();
//            observer = null;
//
//            lay.revalidate();
//            lay.invalidate();
//
//            mActionMode = null;
//        }
    //   }

    private void editContent(final Box myClicked) {
        final Dialog dialog = DialogFactory.boxContentDialog(MainActivity.this);
        final Button btn = (Button) dialog.findViewById(R.id.dialogButtonOK);
        final EditText et = (EditText) dialog.findViewById(R.id.editText);
        et.requestFocus();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Callback call = null;

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et.getWindowToken(), 0);


//                try {
//                    text = (Text) myClicked.getText().TextClone();
//                } catch (CloneNotSupportedException e) {
//                    e.printStackTrace();
//                }
//                text.setText(et.getText().toString());
                String text = (et.getText().toString());
//                Text text = myClicked.getText();
//                text.setText(et.getText().toString());
                //myClicked.getText().setText(et.getText().toString());
                EditBox editBox = new EditBox();
                Properties properties = new Properties();
                properties.put("box", myClicked);
                properties.put("box_text", text);
                editBox.execute(properties);
                addCommendUndo(editBox);
                MainActivity.menu.getItem(4).setVisible(true);
                if (myClicked instanceof Root) {
                    call = new Callback() {
                        @Override
                        public void execute() {
                            lay.updateBox(myClicked);
                        }
                    };
                } else {
                    call = new Callback() {
                        @Override
                        public void execute() {
                            lay.updateBox(myClicked);

                        }
                    };
                }

                dialog.dismiss();
                try {
                    AsyncInvalidate async = new AsyncInvalidate(MainActivity.this);
                    async.setCallback(call);
                    async.execute();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });

        final int MAX_LINES = 3;

        //ogranicza do 3 linii widok w zawartości bloczka
        et.addTextChangedListener(new TextWatcher() {
            private int lines;

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                lines = Utils.countLines(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                int counter = Utils.countLines(s.toString());

                int diff = lines - counter;
                if (diff > 0) {
                    //w gore
                    if (counter < MAX_LINES - 1 && et.getLayoutParams().height > 75) {
                        LinearLayout.LayoutParams buttonLayoutParams = (LinearLayout.LayoutParams) btn.getLayoutParams();
                        buttonLayoutParams.setMargins(buttonLayoutParams.leftMargin, buttonLayoutParams.topMargin - 30,
                                buttonLayoutParams.rightMargin, buttonLayoutParams.bottomMargin);
                        btn.setLayoutParams(buttonLayoutParams);
                        et.getLayoutParams().height -= 30;
                    }
                } else if (diff < 0) {
                    //w dol
                    if (counter < MAX_LINES && et.getLayoutParams().height < 135) {
                        LinearLayout.LayoutParams buttonLayoutParams = (LinearLayout.LayoutParams) btn.getLayoutParams();
                        buttonLayoutParams.setMargins(buttonLayoutParams.leftMargin, buttonLayoutParams.topMargin + 30,
                                buttonLayoutParams.rightMargin, buttonLayoutParams.bottomMargin);
                        btn.setLayoutParams(buttonLayoutParams);
                        et.getLayoutParams().height += 30;
                    }
                }
            }
        });

        et.setText(myClicked.topic.getTitleText());
        int k = Utils.countLines(et.getText().toString());
        int ile = Math.min(MAX_LINES - 1, k);

        et.getLayoutParams().height = 75 + ile * 30;
        LinearLayout.LayoutParams buttonLayoutParams = (LinearLayout.LayoutParams) btn.getLayoutParams();
        buttonLayoutParams.setMargins(buttonLayoutParams.leftMargin,
                buttonLayoutParams.topMargin + 30 * ((k < 2) ? 0 : (k == 2) ? ile - 1 : ile),
                buttonLayoutParams.rightMargin, buttonLayoutParams.bottomMargin);
        btn.setLayoutParams(buttonLayoutParams);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);

        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_menu, menu);
        menu.getItem(1).setVisible(true);
        menu.getItem(2).setVisible(false);
        menu.getItem(3).setVisible(false);
        menu.getItem(4).setVisible(false);
        menu.getItem(5).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }
    //}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(MainActivity.this, EditSheetScreen.class);
                intent.putExtra(BACKGROUNDCOLOR, sheet.getColor().getColor());
                intent.putExtra(INTENSIVITY, sheet.getIntensivity());
                startActivity(intent);
                //               commandsUndo.getFirst().undo();
//                lay.updateBox(root);
//                lay.revalidate();
//                lay.invalidate();
                return true;
            case R.id.action_undo:
                if (commandsUndo.size() == 1) {
                    commandsUndo.getFirst().undo();
                    if (commandsUndo.getFirst() instanceof EditBox) {
                        Callback call = new Callback() {
                            @Override
                            public void execute() {
                                lay.updateBox(((EditBox) commandsUndo.getFirst()).box);
                                for (Box b : ((EditBox) commandsUndo.getLast()).edited) {
                                    lay.updateBox(b);
                                }
                            }
                        };
                        try {
                            AsyncInvalidate async = new AsyncInvalidate(MainActivity.this);
                            async.setCallback(call);
                            async.execute();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    } else if (commandsUndo.getFirst() instanceof EditSheet) {
                        lay.setBackgroundColor(sheet.getColor().getColor());
                    } else if (commandsUndo.getFirst() instanceof AddBox || commandsUndo.getFirst() instanceof RemoveLine) {
                        Callback call = new Callback() {
                            @Override
                            public void execute() {
                            }
                        };
                        try {
                            AsyncInvalidate async = new AsyncInvalidate(MainActivity.this);
                            async.setCallback(call);
                            async.execute();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                        //lay.revalidate();
                        //lay.invalidate();
                    }
                    commandsRedo.add(commandsUndo.getFirst());
                    commandsUndo.removeFirst();
                    menu.getItem(5).setVisible(true);
                    menu.getItem(4).setVisible(false);
                    // menu.getItem(5).setVisible(true);
                } else {
                    commandsUndo.getLast().undo();
                    if (commandsUndo.getLast() instanceof EditBox) {

                       // Callback call = new Callback() {
                            //@Override
                           // public void execute() {
                                lay.updateBox(((EditBox) commandsUndo.getLast()).box);
                                for (Box b : ((EditBox) commandsUndo.getLast()).edited) {
                                    lay.updateBox(b);
                                }
                       //     }
                       // };
//                        try {
//                            AsyncInvalidate async = new AsyncInvalidate(MainActivity.this);
//                            async.setCallback(call);
//                            async.execute();
//                        } catch (Exception e1) {
//                            e1.printStackTrace();
//                        }
                        lay.revalidate();
                         lay.invalidate();
                    } else if (commandsUndo.getLast() instanceof EditSheet) {
                        lay.setBackgroundColor(sheet.getColor().getColor());
                    } else if (commandsUndo.getLast() instanceof AddBox || commandsUndo.getLast() instanceof RemoveLine) {
                        Callback call = new Callback() {
                            @Override
                            public void execute() {
                            }
                        };
                        try {
                            AsyncInvalidate async = new AsyncInvalidate(MainActivity.this);
                            async.setCallback(call);
                            async.execute();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                    commandsRedo.add(commandsUndo.getLast());
                    menu.getItem(5).setVisible(true);
                    commandsUndo.removeLast();
                }
                return true;
            case R.id.action_new:
                IStyle boxEditedStyle = workbook.getStyleSheet().findStyle(boxEdited.topic.getStyleId());
                Intent intent1 = new Intent(MainActivity.this, EditBoxScreen.class);
                intent1.putExtra(EditBoxScreen.BOX_COLOR, boxEditedStyle.getProperty(Styles.FillColor));
                intent1.putExtra(EditBoxScreen.TEXT_COLOR, boxEdited.topic.getTitleText());
                intent1.putExtra(EditBoxScreen.LINE_SHAPE, boxEditedStyle.getProperty(Styles.LineClass));
                intent1.putExtra(EditBoxScreen.LINE_COLOR, boxEditedStyle.getProperty(Styles.LineColor));
                intent1.putExtra(EditBoxScreen.BOX_SHAPE, boxEditedStyle.getProperty(Styles.ShapeClass));
                intent1.putExtra(EditBoxScreen.LINE_THICKNESS, boxEditedStyle.getProperty(Styles.LineWidth));
                startActivity(intent1);
                // lay.revalidate();
                // lay.invalidate();
//                Box box = new Box();
//                AddBox addBox = new AddBox();
//                Properties properties = new Properties();
//                properties.put("box", MainActivity.boxEdited);
//                properties.put("new_box", box);
//                properties.put("root", root);
//                properties.put("res", res);
//                properties.put("style", style);
//                addBox.execute(properties);
//                MainActivity.addCommendUndo(addBox);

//                if (boxEdited instanceof Root) {
//                    if (root.getLeftChildren().size() == root.getRightChildren().size()) {
//                        root.getLeftChildren().add(box);
//                    } else {
//                        root.getRightChildren().add(box);
//                    }
//                } else {
//                    boxEdited.addChild(box);
//                }
//                box.setParent(boxEdited);
//                box.setHeight(root.getHeight() - 10);
//                box.setPoint(new edu.agh.klaukold.common.Point(boxEdited.getDrawableShape().getBounds().right + 30, boxEdited.getDrawableShape().getBounds().top));
//                if (style.equals("Default")) {
//                    Text text = new Text();
//                    text.setAlign(Align.CENTER);
//                    text.setColor(new ColorDrawable(Color.BLACK));
//                    text.setSize(13);
//                    box.setShape(BlockShape.ROUNDED_RECTANGLE);
//                    int color = res.getColor(R.color.light_blue);
//                    box.setColor(new ColorDrawable(color));
//                    box.setText(text);
//                    box.setDrawableShape((GradientDrawable) res.getDrawable(R.drawable.round_rect));
//                    box.setLineStyle(LineStyle.STRAIGHT);
//                    box.setLineColor(Color.rgb(128, 128, 128));
//                    box.setLineThickness(LineThickness.THINNEST);
//                }
                lay.revalidate();
                lay.invalidate();
                return true;
            case R.id.new_line:
                if (!EDIT_CONN) {
                    MainActivity.EDIT_CONN = true;
                } else {
                    MainActivity.EDIT_CONN = false;
                }
                lay.revalidate();
                lay.invalidate();
                return true;
            case R.id.action_redo:
                if (commandsRedo.size() == 1) {
                    commandsRedo.getFirst().redo();
                    if (commandsRedo.getFirst() instanceof EditBox) {
                        Callback call = new Callback() {
                            @Override
                            public void execute() {
                                lay.updateBox(((EditBox) commandsRedo.getFirst()).box);
                                for (Box b : ((EditBox) commandsRedo.getLast()).edited) {
                                    lay.updateBox(b);
                                }
                            }
                        };
                        try {
                            AsyncInvalidate async = new AsyncInvalidate(MainActivity.this);
                            async.setCallback(call);
                            async.execute();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
//                        lay.updateBox(((EditBox) commandsRedo.getFirst()).box);
//                        for (Box b : ((EditBox) commandsRedo.getLast()).edited) {
//                            lay.updateBox(b);
//                        }
//                        lay.revalidate();
//                        lay.invalidate();

                    } else if (commandsRedo.getFirst() instanceof EditSheet) {
                        lay.setBackgroundColor(sheet.getColor().getColor());
                    } else if (commandsRedo.getFirst() instanceof AddBox || commandsRedo.getFirst() instanceof RemoveLine) {
                        Callback call = new Callback() {
                            @Override
                            public void execute() {
                            }
                        };
                        try {
                            AsyncInvalidate async = new AsyncInvalidate(MainActivity.this);
                            async.setCallback(call);
                            async.execute();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
//                        lay.revalidate();
//                        lay.invalidate();
                    }
                    commandsUndo.add(commandsRedo.getFirst());
                    commandsRedo.removeFirst();
                    menu.getItem(4).setVisible(true);
                    menu.getItem(5).setVisible(false);
                } else {
                    commandsRedo.getLast().redo();
                    if (commandsRedo.getLast() instanceof EditBox) {
                        Callback call = new Callback() {
                            @Override
                            public void execute() {
                                lay.updateBox(((EditBox) commandsRedo.getLast()).box);
                                for (Box b : ((EditBox) commandsRedo.getLast()).edited) {
                                    lay.updateBox(b);
                                }
                            }
                        };
                        try {
                            AsyncInvalidate async = new AsyncInvalidate(MainActivity.this);
                            async.setCallback(call);
                            async.execute();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
//                        lay.updateBox(((EditBox) commandsRedo.getLast()).box);
//                        for (Box b : ((EditBox) commandsRedo.getLast()).edited) {
//                            lay.updateBox(b);
//                        }
//                        lay.revalidate();
//                        lay.invalidate();
                    } else if (commandsRedo.getLast() instanceof EditSheet) {
                        lay.setBackgroundColor(sheet.getColor().getColor());
                    } else if (commandsRedo.getLast() instanceof AddBox || commandsRedo.getLast() instanceof RemoveLine) {
                        Callback call = new Callback() {
                            @Override
                            public void execute() {
                            }
                        };
                        try {
                            AsyncInvalidate async = new AsyncInvalidate(MainActivity.this);
                            async.setCallback(call);
                            async.execute();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                    commandsUndo.add(commandsRedo.getLast());
                    menu.getItem(4).setVisible(true);
                    commandsRedo.removeLast();
                }
                return true;
            case R.id.action_trash:
                RemoveBox removeBox = new RemoveBox();
                Properties properties = new Properties();
                HashMap<Box, Line> boxes = new HashMap<Box, Line>();
                //todo usuwanie boxow i linii
                if (!(boxEdited instanceof Root)) {
                    //  boxes.put(MainActivity.boxEdited, MainActivity.boxEdited.getParent().getLines().get(MainActivity.boxEdited));
                }
                for (Box b : MainActivity.toEditBoxes) {
                    if (!(b instanceof Root)) {
                        //   boxes.put(b, b.getParent().getLines().get(b));
                        if (!(boxEdited instanceof Root)) {
                            //   boxes.put(MainActivity.boxEdited, MainActivity.boxEdited.getParent().getLines().get(MainActivity.boxEdited));
                        }
                        if (boxes.size() > 0) {
                            properties.put("boxes", boxes);
                            removeBox.execute(properties);
                            MainActivity.addCommendUndo(removeBox);
                        }
                        return true;

                    }
                    //  return true;
                }
            default:
                return super.onContextItemSelected(item);
        }
    }

    public static void addCommendUndo(Command command) {
        if (commandsUndo.size() == 10) {
            commandsUndo.removeFirst();
        }
        commandsUndo.add(command);
        menu.getItem(4).setVisible(true);
        lay.revalidate();
        lay.invalidate();
        if (boxEdited != null && command instanceof EditBox) {
            lay.updateBox(boxEdited);
        }
        //    lay.updateText();
    }

    public static void changeShape(Box box) {
        IStyle s = workbook.getStyleSheet().findStyle(box.topic.getStyleId());
        if (s.getProperty(Styles.ShapeClass).equals(Styles.TOPIC_SHAPE_DIAMOND)) {
            box.setDrawableShape((RotateDrawable) res.getDrawable(R.drawable.diammond));
        } else if (s.getProperty(Styles.ShapeClass).equals(Styles.TOPIC_SHAPE_UNDERLINE)) {
            box.setDrawableShape((GradientDrawable) res.getDrawable(R.drawable.no_border));
        } else if (s.getProperty(Styles.ShapeClass).equals(Styles.TOPIC_SHAPE_NO_BORDER)) {
            box.setDrawableShape((GradientDrawable) res.getDrawable(R.drawable.no_border));
        } else if (s.getProperty(Styles.ShapeClass).equals(Styles.TOPIC_SHAPE_ELLIPSE)) {
            box.setDrawableShape((GradientDrawable) res.getDrawable(R.drawable.elipse));
        } else if (s.getProperty(Styles.ShapeClass).equals(Styles.TOPIC_SHAPE_RECT)) {
            box.setDrawableShape((GradientDrawable) res.getDrawable(R.drawable.rect));
        } else if (s.getProperty(Styles.ShapeClass).equals(Styles.TOPIC_SHAPE_ROUNDEDRECT)) {
            box.setDrawableShape((GradientDrawable) res.getDrawable(R.drawable.round_rect));
        }
    }

    private void fireUnSelect(Box box) {
        box.isSelected = (false);
        for (Box b : box.getChildren()) {
            box.isSelected = (false);
            fireUnSelect(b);
        }
    }
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        root.clear();
//        root = null;
//    }
}

