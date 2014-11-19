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
import java.util.Set;

import edu.agh.R;
import edu.agh.idziak.WorkbookHandler;
import edu.agh.klaukold.commands.AddBox;
import edu.agh.klaukold.commands.AddLine;
import edu.agh.klaukold.commands.AddNote;
import edu.agh.klaukold.commands.AddRelationship;
import edu.agh.klaukold.commands.EditBox;
import edu.agh.klaukold.commands.EditSheet;
import edu.agh.klaukold.commands.RemoveBox;
import edu.agh.klaukold.commands.RemoveLine;
import edu.agh.klaukold.commands.RemoveRelationship;
import edu.agh.klaukold.common.Box;
import edu.agh.klaukold.common.Line;
import edu.agh.klaukold.enums.Actions;
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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RotateDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.xmind.core.INotes;
import org.xmind.core.IPlainNotesContent;
import org.xmind.core.IRelationship;
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
    public static Box boxEdited;
    public static boolean EDIT_CONN = false;
    public static LinkedList<Box> toEditBoxes = new LinkedList<Box>();
    public static Properties properties = new Properties();
    private Pair<Box, Actions> pair;

    public static LinkedList<Command> commandsUndo = new LinkedList<Command>();
    public LinkedList<Command> commandsRedo = new LinkedList<Command>();
    private static Menu menu;


    public static int id = 1;

    private PointF mid = new PointF();
    private ScaleGestureDetector detector;

    public final static String BACKGROUNDCOLOR = "COLOR";
    public static int width;
    public static int height;

    public static String style;

    ///---------------------------------------
    public static ISheet sheet1;
    public static ITopic rootTopic;
    public static IWorkbook workbook;
    public static IStyleSheet styleSheet;
    public static Resources res;
    public static  IStyle style1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        workbook = WelcomeScreen.workbook;
        res = getResources();
        if (root == null) {
            WorkbookHandler handler = WorkbookHandler.createNewWorkbook();
            style1 = null;

            if (workbook == null) {
                workbook = handler.getWorkbook();
                styleSheet = workbook.getStyleSheet();
                style1 = styleSheet.createStyle(IStyle.TOPIC);
            }
            styleSheet = workbook.getStyleSheet();
            sheet1 = workbook.getPrimarySheet();
            rootTopic = sheet1.getRootTopic();
            root = new Box();
            Intent intent = getIntent();
            style = intent.getStringExtra(WelcomeScreen.STYLE);
            res = getResources();
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            width = size.x / 3;
            height = size.y / 3;
            root.setPoint(new edu.agh.klaukold.common.Point(width, height));
            lay = (DrawView) findViewById(R.id.myLay);
            lay.context = this;
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
                IStyle style2 = styleSheet.createStyle(IStyle.THEME);
                style2.setProperty(Styles.FillColor, String.valueOf(res.getColor(R.color.white)));
                styleSheet.addStyle(style2, IStyleSheet.NORMAL_STYLES);
                sheet1.setThemeId(style2.getId());;
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
                IStyle style2 = styleSheet.createStyle(IStyle.THEME);
                style2.setProperty(Styles.FillColor, String.valueOf(res.getColor(R.color.light_yellow)));
                styleSheet.addStyle(style2, IStyleSheet.NORMAL_STYLES);
                sheet1.setThemeId(style2.getId());
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
                IStyle style2 = styleSheet.createStyle(IStyle.THEME);
                style2.setProperty(Styles.FillColor, String.valueOf(res.getColor(R.color.white)));
                styleSheet.addStyle(style2, IStyleSheet.NORMAL_STYLES);
                sheet1.setThemeId(style2.getId());
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
                IStyle style2 = styleSheet.createStyle(IStyle.THEME);
                style2.setProperty(Styles.FillColor, String.valueOf(res.getColor(R.color.white)));
                styleSheet.addStyle(style2, IStyleSheet.NORMAL_STYLES);
                sheet1.setThemeId(style2.getId());
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
                IStyle style2 = styleSheet.createStyle(IStyle.THEME);
                style2.setProperty(Styles.FillColor, String.valueOf(res.getColor(R.color.dark_gray)));
                styleSheet.addStyle(style2, IStyleSheet.NORMAL_STYLES);
                sheet1.setThemeId(style2.getId());
            } else if (style.equals("ReadyMap")) {
                        root.setPoint(new edu.agh.klaukold.common.Point(width, height));
                        //style1 = workbook.getStyleSheet().findStyle(rootTopic.getStyleId());
                        final HashMap<String, Box> boxes = new HashMap<String, Box>();
                        root.setDrawableShape((GradientDrawable) res.getDrawable(R.drawable.round_rect));
                        root.topic = rootTopic;
                        root.topic.setFolded(false);

                        boxes.put(root.topic.getId(), root);

                        for (ITopic t : root.topic.getAllChildren()) {
                            Box b = new Box();
                            b.topic = t;
                            boxes.put(root.topic.getId(), root);
                            b.setDrawableShape((GradientDrawable) res.getDrawable(R.drawable.round_rect));
                            b.parent = root;
                            root.addChild(b);
                            Utils.fireAddSubtopic(b, boxes);
                            boxes.put(t.getId(), b);
                    }
                Utils.findRelationships(boxes);
            }

        }
        gestureDetector = new GestureDetector(this, gestList);
        Utils.lay = lay;
        if (lay != null) {
            lay.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getActionMasked()) {
                        case (MotionEvent.ACTION_OUTSIDE):
                            return true;

                        case (MotionEvent.ACTION_UP):

                            break;
                        case MotionEvent.ACTION_POINTER_DOWN:
//                                            // multitouch!! - touch down
//                                            int count = event.getPointerCount(); // Number of 'fingers' in this time
//
//                                            if (count > 1) {
//                                                Box b1 = Utils.whichBox(lay, event, 0);
//                                                Box b2 = Utils.whichBox(lay, event, 1);
//                                            }
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
        }
        Utils.context = this;
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
                //    lay.revalidate();
                //    lay.invalidate();
                return true;
            }
        });

        lay.holder = lay.getHolder();
        lay.holder.addCallback(lay);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (lay != null && sheet1 != null && sheet1.getTheme() != null && sheet1.getTheme().getProperty(Styles.FillColor) != null) {
            lay.setBackgroundColor(Integer.parseInt(sheet1.getTheme().getProperty(Styles.FillColor)));
        }
    }

    //tutaj rozpoznajemy przytrzymanie, jedno kliknięcie, dwa kliknięcia
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        Box clicked;
        boolean click = false;

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            pair = Utils.whichBoxAction(lay, event);
            Box box = Utils.whichBox(lay, event);
            if (box != null) {
                box.isSelected = true;
                lay.invalidate();
                //lay.invalidate(box.drawableShape.getBounds().left, box.drawableShape.getBounds().top, box.drawableShape.getBounds().right, box.drawableShape.getBounds().bottom);
                MainActivity.boxEdited = box;
                if (MainActivity.boxEdited != null && !MainActivity.toEditBoxes.contains(box)) {
                    MainActivity.toEditBoxes.add(box);
                }
                if (MainActivity.toEditBoxes.size() == 2) {
                    menu.getItem(4).setVisible(true);
                    menu.getItem(1).setVisible(true);
                } else if (MainActivity.toEditBoxes.size() == 1) {
                    if (!MainActivity.toEditBoxes.getFirst().topic.isRoot()) {
                        menu.getItem(1).setVisible(true);
                    }
                    menu.getItem(4).setVisible(false);
                } else {
                    menu.getItem(4).setVisible(false);
                    menu.getItem(1).setVisible(false);
                }

                menu.getItem(2).setVisible(true);
                if (!box.topic.isRoot()) {
                    menu.getItem(3).setVisible(true);
                }
            } else if (box == null) {

                menu.getItem(1).setVisible(false);
                menu.getItem(4).setVisible(false);
                root.isSelected = false;

                for (int i = 0; i < MainActivity.toEditBoxes.size(); i++) {
                    MainActivity.toEditBoxes.get(i).isSelected = false;
                    lay.invalidate();
                  //  lay.invalidate(MainActivity.toEditBoxes.get(i).drawableShape.getBounds().left, MainActivity.toEditBoxes.get(i).drawableShape.getBounds().top, MainActivity.toEditBoxes.get(i).drawableShape.getBounds().right, MainActivity.toEditBoxes.get(i).drawableShape.getBounds().bottom);
                }
                menu.getItem(2).setVisible(false);
                menu.getItem(3).setVisible(false);
                MainActivity.toEditBoxes.clear();
              //  lay.invalidate();
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
                   lay.invalidateDrawable(box1.drawableShape);
                } else if (pair.second == Actions.ADD_NOTE) {
                    final Dialog dialog = DialogFactory.boxContentDialog(MainActivity.this);
                    final Button btn = (Button) dialog.findViewById(R.id.dialogButtonOK);
                    final EditText et = (EditText) dialog.findViewById(R.id.editText);
                    et.requestFocus();
                    final Button btn2 = (Button) dialog.findViewById(R.id.button2);
                    btn2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Callback call = null;

                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(et.getWindowToken(), 0);

                            String text = (et.getText().toString());
                            AddNote addNote = new AddNote();
                            Properties properties = new Properties();
                            properties.put("box", pair.first);
                            properties.put("text", text);
                            addNote.execute(properties);
                            addCommendUndo(addNote);
                            MainActivity.menu.getItem(5).setVisible(true);
                            call = new Callback() {
                                @Override
                                public void execute() {
                                    lay.updateBoxWithText(pair.first);

                                }
                            };

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
                                    btn2.setLayoutParams(buttonLayoutParams);
                                    et.getLayoutParams().height -= 30;
                                }
                            } else if (diff < 0) {
                                //w dol
                                if (counter < MAX_LINES && et.getLayoutParams().height < 135) {
                                    LinearLayout.LayoutParams buttonLayoutParams = (LinearLayout.LayoutParams) btn.getLayoutParams();
                                    buttonLayoutParams.setMargins(buttonLayoutParams.leftMargin, buttonLayoutParams.topMargin + 30,
                                            buttonLayoutParams.rightMargin, buttonLayoutParams.bottomMargin);
                                    btn.setLayoutParams(buttonLayoutParams);
                                    btn2.setLayoutParams(buttonLayoutParams);
                                    et.getLayoutParams().height += 30;
                                }
                            }
                        }
                    });

//                    et.setText(pair.first.topic.getNotes().getContent(INotes.PLAIN).getFormat());
                    int k = Utils.countLines(et.getText().toString());
                    int ile = Math.min(MAX_LINES - 1, k);

                    et.getLayoutParams().height = 75 + ile * 30;
                    LinearLayout.LayoutParams buttonLayoutParams = (LinearLayout.LayoutParams) btn.getLayoutParams();
                    buttonLayoutParams.setMargins(buttonLayoutParams.leftMargin,
                            buttonLayoutParams.topMargin + 30 * ((k < 2) ? 0 : (k == 2) ? ile - 1 : ile),
                            buttonLayoutParams.rightMargin, buttonLayoutParams.bottomMargin);
                    btn.setLayoutParams(buttonLayoutParams);
                    btn2.setLayoutParams(buttonLayoutParams);

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);

                    dialog.show();

                } else if (pair.second == Actions.NEW_NOTE) {
                    final Dialog dialog = DialogFactory.boxContentDialog(MainActivity.this);
                    final Button btn = (Button) dialog.findViewById(R.id.dialogButtonOK);
                    final EditText et = (EditText) dialog.findViewById(R.id.editText);
                    final Button btn2 = (Button) dialog.findViewById(R.id.button2);
                    btn2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    et.requestFocus();

                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Callback call = null;

                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(et.getWindowToken(), 0);

                            String text = (et.getText().toString());
                            AddNote addNote = new AddNote();
                            Properties properties = new Properties();
                            properties.put("box", pair.first);
                            properties.put("text", text);
                            addNote.execute(properties);
                            addCommendUndo(addNote);
                            MainActivity.menu.getItem(5).setVisible(true);
                            call = new Callback() {
                                @Override
                                public void execute() {
                                    lay.updateBoxWithText(pair.first);

                                }
                            };

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
                                    btn2.setLayoutParams(buttonLayoutParams);
                                    et.getLayoutParams().height -= 30;
                                }
                            } else if (diff < 0) {
                                //w dol
                                if (counter < MAX_LINES && et.getLayoutParams().height < 135) {
                                    LinearLayout.LayoutParams buttonLayoutParams = (LinearLayout.LayoutParams) btn.getLayoutParams();
                                    buttonLayoutParams.setMargins(buttonLayoutParams.leftMargin, buttonLayoutParams.topMargin + 30,
                                            buttonLayoutParams.rightMargin, buttonLayoutParams.bottomMargin);
                                    btn.setLayoutParams(buttonLayoutParams);
                                    btn2.setLayoutParams(buttonLayoutParams);
                                    et.getLayoutParams().height += 30;
                                }
                            }
                        }
                    });

                    et.setText(((IPlainNotesContent) pair.first.topic.getNotes().getContent(INotes.PLAIN)).getTextContent());
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
            if (click && clicked != null && clicked.topic.getParent() == null && !clicked.topic.isRoot()) {
                mIsScrolling = true;
                int newx = (int) (e2.getX() - lay.transx);
                int newy = (int) (e2.getY() - lay.transy);

                newx /= lay.zoomx;
                newy /= lay.zoomy;

                clicked.setPoint(new edu.agh.klaukold.common.Point(newx, newy));
                clicked.setPoint(new edu.agh.klaukold.common.Point(newx, newy));
                clicked.prepareDrawableShape();
                lay.revalidate();
                lay.invalidate();
                return false;
            }
           else  if (click && clicked != null && (clicked.topic.isRoot()  || clicked.topic.getParent().isRoot())) {
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
                        if (!clicked.topic.isRoot()) {
                            if (clicked.drawableShape.getBounds().left <= root.drawableShape.getBounds().centerX()) {
                                clicked.getLines().get(box).setStart(new edu.agh.klaukold.common.Point(clicked.getDrawableShape().getBounds().left, clicked.getDrawableShape().getBounds().top + clicked.getHeight() / 2));
                            } else {
                                clicked.getLines().get(box).setStart(new edu.agh.klaukold.common.Point(clicked.getDrawableShape().getBounds().right, clicked.getDrawableShape().getBounds().top + clicked.getHeight() / 2));
                            }
                        } else {
                            if (box.drawableShape.getBounds().left <= root.drawableShape.getBounds().centerX()) {
                                clicked.getLines().get(box).setStart(new edu.agh.klaukold.common.Point(clicked.getDrawableShape().getBounds().left, clicked.getDrawableShape().getBounds().top + clicked.getHeight() / 2));
                            } else {
                                clicked.getLines().get(box).setStart(new edu.agh.klaukold.common.Point(clicked.getDrawableShape().getBounds().right, clicked.getDrawableShape().getBounds().top + clicked.getHeight() / 2));
                            }
                        }
                    }
                }
                //to napisac wszystko od nowa zwiazanego z polaczeniami
                if (clicked.parent != null) {
                    if (clicked.parent.getLines().get(clicked) != null) {
                        if (clicked.drawableShape.getBounds().left <= root.drawableShape.getBounds().centerX()) {
                            clicked.parent.getLines().get(clicked).setStart(new edu.agh.klaukold.common.Point(clicked.parent.getDrawableShape().getBounds().left, clicked.parent.getDrawableShape().getBounds().centerY()));
                            clicked.parent.getLines().get(clicked).setEnd(new edu.agh.klaukold.common.Point(clicked.getDrawableShape().getBounds().right, clicked.getDrawableShape().getBounds().top + clicked.getHeight() / 2));
                        } else {
                            clicked.parent.getLines().get(clicked).setEnd(new edu.agh.klaukold.common.Point(clicked.getDrawableShape().getBounds().left, clicked.getDrawableShape().getBounds().top + clicked.getHeight() / 2));
                            clicked.parent.getLines().get(clicked).setStart(new edu.agh.klaukold.common.Point(clicked.parent.getDrawableShape().getBounds().right, clicked.parent.getDrawableShape().getBounds().centerY()));
                        }

//                                else {
//                                    if (clicked.position == Position.LFET) {
//                                        clicked.parent.getLines().get(clicked).setEnd(new edu.agh.klaukold.common.Point(clicked.getDrawableShape().getBounds().left, clicked.getDrawableShape().getBounds().top + clicked.getHeight() / 2));
//                                    } else {
//                                        clicked.parent.getLines().get(clicked).setEnd(new edu.agh.klaukold.common.Point(clicked.getDrawableShape().getBounds().right, clicked.getDrawableShape().getBounds().top + clicked.getHeight() / 2));
//                                    }
//                                }
                    }
                }
                //lay.revalidate();
                lay.invalidate();
                //lay.invalidate(clicked.drawableShape.getBounds().left - 20, clicked.drawableShape.getBounds().top - 20, clicked.drawableShape.getBounds().right + 20, clicked.drawableShape.getBounds().bottom + 20);
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
        final Button btn2 = (Button) dialog.findViewById(R.id.button2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.commandsUndo.size() > 1 && MainActivity.commandsUndo.getLast() instanceof AddBox) {
                    MainActivity.commandsUndo.getLast().undo();
                    lay.invalidate();
                    lay.revalidate();
                } else if (MainActivity.commandsUndo.size() == 1 && MainActivity.commandsUndo.getFirst() instanceof AddBox) {
                    MainActivity.commandsUndo.getFirst().undo();
                    DrawView.LUheight = 0;
                    DrawView.LDHehight = 0;
                    DrawView.RUheight = 0;
                    DrawView.RDHehight = 0;
                    DrawView.count = 0;
                    lay.invalidate();
                    lay.revalidate();
                }
                dialog.dismiss();
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Callback call = null;

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
                String text = (et.getText().toString());
                EditBox editBox = new EditBox();
                Properties properties = new Properties();
                properties.put("box", myClicked);
                properties.put("box_text", text);
                editBox.execute(properties);
            //    addCommendUndo(editBox);
                MainActivity.menu.getItem(5).setVisible(true);
                lay.updateBoxWithText(myClicked);
                myClicked.drawableShape.invalidateSelf();
                lay.invalidate(myClicked.getDrawableShape().getBounds().right, myClicked.getDrawableShape().getBounds().top, myClicked.getDrawableShape().getBounds().bottom, myClicked.getDrawableShape().getBounds().right + 30);
                dialog.dismiss();
               // lay.invalidate();
               // lay.revalidate();
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
                        btn2.setLayoutParams(buttonLayoutParams);
                        et.getLayoutParams().height -= 30;
                    }
                } else if (diff < 0) {
                    //w dol
                    if (counter < MAX_LINES && et.getLayoutParams().height < 135) {
                        LinearLayout.LayoutParams buttonLayoutParams = (LinearLayout.LayoutParams) btn.getLayoutParams();
                        buttonLayoutParams.setMargins(buttonLayoutParams.leftMargin, buttonLayoutParams.topMargin + 30,
                                buttonLayoutParams.rightMargin, buttonLayoutParams.bottomMargin);
                        btn.setLayoutParams(buttonLayoutParams);
                        btn2.setLayoutParams(buttonLayoutParams);
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
        menu.getItem(1).setVisible(false);
        menu.getItem(2).setVisible(false);
        menu.getItem(3).setVisible(false);
        menu.getItem(4).setVisible(false);
        menu.getItem(5).setVisible(false);
        menu.getItem(6).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }
    //}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(MainActivity.this, EditSheetScreen.class);
                intent.putExtra(BACKGROUNDCOLOR, Integer.parseInt(sheet1.getTheme().getProperty(Styles.FillColor)));
                // intent.putExtra(INTENSIVITY, sheet.getIntensivity());
                startActivity(intent);
                //               commandsUndo.getFirst().undo();
//                lay.updateBoxWithText(root);
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
                                lay.updateBoxWithText(((EditBox) commandsUndo.getFirst()).box);
                                for (Box b : ((EditBox) commandsUndo.getLast()).edited) {
                                    lay.updateBoxWithText(b);
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
                        lay.setBackgroundColor((Integer.parseInt(sheet1.getTheme().getProperty(Styles.FillColor))));
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
                    menu.getItem(6).setVisible(true);
                    menu.getItem(5).setVisible(false);
                    // menu.getItem(5).setVisible(true);
                } else {
                    commandsUndo.getLast().undo();
                    if (commandsUndo.getLast() instanceof EditBox) {

                        // Callback call = new Callback() {
                        //@Override
                        // public void execute() {
                        lay.updateBoxWithText(((EditBox) commandsUndo.getLast()).box);
                        for (Box b : ((EditBox) commandsUndo.getLast()).edited) {
                            lay.updateBoxWithText(b);
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
                        lay.invalidate();
                    } else if (commandsUndo.getLast() instanceof EditSheet) {
                        lay.setBackgroundColor(Integer.parseInt(sheet1.getTheme().getProperty(Styles.FillColor)));
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
                    menu.getItem(6).setVisible(true);
                    commandsUndo.removeLast();
                }
                return true;
            case R.id.action_new:
                IStyle boxEditedStyle = workbook.getStyleSheet().findStyle(boxEdited.topic.getStyleId());
                Intent intent1 = new Intent(MainActivity.this, EditBoxScreen.class);
                if (boxEditedStyle != null ) {

                    intent1.putExtra(EditBoxScreen.BOX_COLOR, boxEditedStyle.getProperty(Styles.FillColor));
                    intent1.putExtra(EditBoxScreen.TEXT_COLOR, boxEdited.topic.getTitleText());
                    intent1.putExtra(EditBoxScreen.LINE_SHAPE, boxEditedStyle.getProperty(Styles.LineClass));
                    intent1.putExtra(EditBoxScreen.LINE_COLOR, boxEditedStyle.getProperty(Styles.LineColor));
                    intent1.putExtra(EditBoxScreen.BOX_SHAPE, boxEditedStyle.getProperty(Styles.ShapeClass));
                    intent1.putExtra(EditBoxScreen.LINE_THICKNESS, boxEditedStyle.getProperty(Styles.LineWidth));
                }
                startActivity(intent1);
                lay.invalidateDrawable(boxEdited.drawableShape);
                return true;
            case R.id.new_line:
                Properties properties1 = new Properties();
                properties1.put("child", MainActivity.toEditBoxes.getFirst());
                if (MainActivity.toEditBoxes.size() == 2) {
                    properties1.put("parent", MainActivity.toEditBoxes.getLast());
                }
                AddLine addLine = new AddLine();
                addLine.execute(properties1);
                MainActivity.addCommendUndo(addLine);
//                lay.revalidate();
                lay.invalidate();
                return true;
            case R.id.action_redo:
                if (commandsRedo.size() == 1) {
                    commandsRedo.getFirst().redo();
                    if (commandsRedo.getFirst() instanceof EditBox) {
                        //     Callback call = new Callback() {
                        //         @Override
                        //        public void execute() {
                        lay.updateBoxWithText(((EditBox) commandsRedo.getFirst()).box);
                        for (Box b : ((EditBox) commandsRedo.getLast()).edited) {
                            lay.updateBoxWithText(b);
                        }


                    } else if (commandsRedo.getFirst() instanceof EditSheet) {
                        lay.setBackgroundColor(Integer.parseInt(sheet1.getTheme().getProperty(Styles.FillColor)));
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

                    }
                    commandsUndo.add(commandsRedo.getFirst());
                    commandsRedo.removeFirst();
                    menu.getItem(5).setVisible(true);
                    menu.getItem(6).setVisible(false);
                } else {
                    commandsRedo.getLast().redo();
                    if (commandsRedo.getLast() instanceof EditBox) {
                        Callback call = new Callback() {
                            @Override
                            public void execute() {
                                lay.updateBoxWithText(((EditBox) commandsRedo.getLast()).box);
                                for (Box b : ((EditBox) commandsRedo.getLast()).edited) {
                                    lay.updateBoxWithText(b);
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
                    } else if (commandsRedo.getLast() instanceof EditSheet) {
                        lay.setBackgroundColor(Integer.parseInt(sheet1.getTheme().getProperty(Styles.FillColor)));
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
                    menu.getItem(5).setVisible(true);
                    commandsRedo.removeLast();
                }
                return true;
            case R.id.action_trash:
                RemoveBox removeBox = new RemoveBox();
                Properties properties = new Properties();
                HashMap<Box, Line> boxes = new HashMap<Box, Line>();
                //todo usuwanie boxow i linii
                boxes.put(MainActivity.boxEdited, MainActivity.boxEdited.parent.getLines().get(MainActivity.boxEdited));
                for (Box b : MainActivity.toEditBoxes) {
                    boxes.put(b, b.parent.getLines().get(b));
                    if (boxes.size() > 0) {
                        properties.put("boxes", boxes);
                        removeBox.execute(properties);
                        MainActivity.addCommendUndo(removeBox);
                    }
                    return true;


                    //  return true;
                }
            case R.id.new_rel:
                if (!MainActivity.toEditBoxes.getFirst().relationships.containsKey(MainActivity.toEditBoxes.getLast())) {
                    final Dialog dialog = DialogFactory.boxContentDialog(MainActivity.this);
                    final Button btn = (Button) dialog.findViewById(R.id.dialogButtonOK);
                    final EditText et = (EditText) dialog.findViewById(R.id.editText);
                    et.requestFocus();
                    final Button btn2 = (Button) dialog.findViewById(R.id.button2);
                    btn2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Callback call = null;

                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
                            String text = (et.getText().toString());
                            AddRelationship addRel = new AddRelationship();
                            Properties p = new Properties();
                            p.put("boxes", MainActivity.toEditBoxes);
                            p.put("text", text);
                            addRel.execute(p);
                            MainActivity.addCommendUndo(addRel);
                            lay.drawRelationship(MainActivity.toEditBoxes.getFirst(), MainActivity.toEditBoxes.getLast());
                            lay.invalidate();
//                            lay.invalidate(Math.min(MainActivity.toEditBoxes.getFirst().drawableShape.getBounds().left, MainActivity.toEditBoxes.getLast().drawableShape.getBounds().left)
//                                    ,Math.min(MainActivity.toEditBoxes.getFirst().drawableShape.getBounds().top, MainActivity.toEditBoxes.getLast().drawableShape.getBounds().top)
//                            ,Math.max(MainActivity.toEditBoxes.getFirst().drawableShape.getBounds().right, MainActivity.toEditBoxes.getLast().drawableShape.getBounds().right)
//                                    ,Math.max(MainActivity.toEditBoxes.getFirst().drawableShape.getBounds().bottom, MainActivity.toEditBoxes.getLast().drawableShape.getBounds().bottom));
                            dialog.dismiss();

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
                                    btn2.setLayoutParams(buttonLayoutParams);
                                    et.getLayoutParams().height -= 30;
                                }
                            } else if (diff < 0) {
                                //w dol
                                if (counter < MAX_LINES && et.getLayoutParams().height < 135) {
                                    LinearLayout.LayoutParams buttonLayoutParams = (LinearLayout.LayoutParams) btn.getLayoutParams();
                                    buttonLayoutParams.setMargins(buttonLayoutParams.leftMargin, buttonLayoutParams.topMargin + 30,
                                            buttonLayoutParams.rightMargin, buttonLayoutParams.bottomMargin);
                                    btn.setLayoutParams(buttonLayoutParams);
                                    btn2.setLayoutParams(buttonLayoutParams);
                                    et.getLayoutParams().height += 30;
                                }
                            }
                        }
                    });

//                    et.setText(pair.first.topic.getNotes().getContent(INotes.PLAIN).getFormat());
                    int k = Utils.countLines(et.getText().toString());
                    int ile = Math.min(MAX_LINES - 1, k);

                    et.getLayoutParams().height = 75 + ile * 30;
                    LinearLayout.LayoutParams buttonLayoutParams = (LinearLayout.LayoutParams) btn.getLayoutParams();
                    buttonLayoutParams.setMargins(buttonLayoutParams.leftMargin,
                            buttonLayoutParams.topMargin + 30 * ((k < 2) ? 0 : (k == 2) ? ile - 1 : ile),
                            buttonLayoutParams.rightMargin, buttonLayoutParams.bottomMargin);
                    btn.setLayoutParams(buttonLayoutParams);
                    btn2.setLayoutParams(buttonLayoutParams);

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);

                    dialog.show();
                } else {
                    RemoveRelationship remRel = new RemoveRelationship();
                    Properties p = new Properties();
                    p.put("boxes", MainActivity.toEditBoxes);
                    remRel.execute(p);
                    MainActivity.addCommendUndo(remRel);
                    lay.invalidate();
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
        menu.getItem(5).setVisible(true);
        if (!(command instanceof AddBox)) {
            //MainActivity.lay.revalidate();
            //MainActivity.lay.invalidate();
        }
        if (boxEdited != null && command instanceof EditBox) {
            Utils.lay.updateBoxWithText(boxEdited);
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


    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}

