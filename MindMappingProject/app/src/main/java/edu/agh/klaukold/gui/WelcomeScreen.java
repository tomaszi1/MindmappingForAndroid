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

import edu.agh.R;
import edu.agh.idziak.FileBrowserActivity;
import edu.agh.idziak.dropbox.DbxBrowser;
import edu.agh.idziak.dropbox.DropboxHandler;
import edu.agh.idziak.dropbox.DropboxWorkbookManager;
import edu.agh.idziak.dropbox.ResultListener;
import edu.agh.idziak.local.LocalWorkbookManager;
import edu.agh.klaukold.common.Box;
import edu.agh.klaukold.utilities.Utils;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import org.xmind.core.ITopic;
import org.xmind.core.IWorkbook;
import org.xmind.core.internal.Workbook;
import org.xmind.core.style.IStyle;
import org.xmind.core.style.IStyleSheet;
import org.xmind.ui.style.Styles;

import java.io.File;

public class WelcomeScreen extends Activity {
    private Spinner styles;

    public Spinner getStyles() {
        return styles;
    }

    public void setStyles(Spinner styles) {
        this.styles = styles;
    }

    public Button getButtonCreateMindMap() {
        return buttonCreateMindMap;
    }

    public void setButtonCreateMindMap(Button buttonCreateMindMap) {
        this.buttonCreateMindMap = buttonCreateMindMap;
    }

    private Button buttonCreateMindMap;
    private Button buttonLoad;
    private ImageView imageStyle;
    public final static String STYLE = "WELCOME_SCREEN_STYLE";
    public static final int REQUEST_FILE = 1;
    private ProgressDialog progressDialog;
    private DropboxHandler dropboxHandler;
    private DropboxWorkbookManager dropboxWorkbookManager;

    @Override
    public void onResume() {
        super.onResume();
//		
//		if(!Utils.isBaseSet()) {
//			Properties props = new Properties();
//			try {
//				InputStream inputStream = getResources().getAssets().open("base.properties");
//				props.load(inputStream);
//				Utils.setBaseDir(props.getProperty("srcDirectory"));
//				//DrawView.setActiveColor(props.getProperty("activeColor"));
//				//DrawView.setCollapsedColor(props.getProperty("collapsedColor"));
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		
//		//Utils.initDB(this);
        Utils.context = this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("text", "logging");
        setContentView(R.layout.welcome_screen);
        Spinner spinner = (Spinner) findViewById(R.id.spinnerStyles);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.styles_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        //dodanie lisener'a do spinnera i przycisku
        addListenerOnButtonCreateMindMap();
        addListenerSpinerStyles();

        buttonLoad = (Button) findViewById(R.id.buttonLoad);
        buttonLoad.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent1 = new Intent(WelcomeScreen.this, FileBrowserActivity.class);
                startActivityForResult(intent1,  REQUEST_FILE);


            }
        });




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_FILE) {
            if (resultCode == RESULT_OK) {
                progressDialog = ProgressDialog.show(this, "Pobieranie", "Chwila...", true, false);
                File file = (File) data.getExtras().get(FileBrowserActivity.SELECTED_FILE);
               // DropboxWorkbookManager.downloadWorkbook(file, loadFileListener, dropboxHandler);

                LocalWorkbookManager.loadWorkbook(file, new ResultListener() {
                    @Override
                    public void taskDone(Object result) {
                        progressDialog.dismiss();
                        DrawView.LUheight = 0;
                        DrawView.LDHehight = 0;
                        DrawView.RUheight = 0;
                        DrawView.RDHehight = 0;
                        MainActivity.workbook = (Workbook) result;
//                        for (ITopic t : MainActivity.root.topic.getChildren(ITopic.ATTACHED)) {
//                            Box b = new Box();
//                            b.topic = t;
//                            IStyle s = MainActivity.styleSheet.createStyle(IStyle.TOPIC);
//                            s = MainActivity.styleSheet.createStyle(IStyle.TOPIC);
//                            s.setProperty(Styles.TextColor, String.valueOf(MainActivity.res.getColor(R.color.black))); // trzeba podać kolor w formacie "0xffffff"
//                            s.setProperty(Styles.FillColor, String.valueOf(MainActivity.res.getColor(R.color.white)));
//                            //   s.setProperty(Styles.ShapeClass, Styles.TOPIC_SHAPE_ROUNDEDRECT);
//                            s.setProperty(Styles.FontSize, "13pt");
//                            s.setProperty(Styles.TextAlign, Styles.ALIGN_CENTER);
//                            s.setProperty(Styles.FontFamily, "Times New Roman");
//                            //  s.setProperty(Styles.LineClass, Styles.BRANCH_CONN_STRAIGHT);
//                            s.setProperty(Styles.LineWidth, "1pt");
//                            s.setProperty(Styles.LineColor, String.valueOf(Color.rgb(128, 128, 128)));
//                            s.setProperty(Styles.FontFamily, "Times New Roman");
//                            MainActivity.styleSheet.addStyle(s, IStyleSheet.NORMAL_STYLES);
//                            b.setDrawableShape((GradientDrawable) MainActivity.res.getDrawable(R.drawable.round_rect));
//                            b.topic.setStyleId(s.getId());
//                            b.parent = MainActivity.root;
//                            MainActivity.root.addChild(b);
//                            Utils.fireAddSubtopic(b);
//                        }
                        Intent intent = new Intent(WelcomeScreen.this, MainActivity.class);
                        String style = "ReadyMap";
                        intent.putExtra(STYLE, style);
                      //  intent.putExtra("workbook", ((Workbook) result));
                        if (MainActivity.root != null) {
                            MainActivity.root.getChildren().clear();
                        }
                        MainActivity.root = null;
                        startActivity(intent);
                    }

                    @Override
                    public void taskFailed(Exception exception) {
                        progressDialog.cancel();
                    }
                });
            } else {
                showToast("Anulowano");
            }
        }
    }
    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private void addListenerSpinerStyles() {
        final Spinner spinner = (Spinner) findViewById(R.id.spinnerStyles);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                imageStyle = (ImageView) findViewById(R.id.imageView);
                if (spinner.getSelectedItem().toString().equals("Default")) {
                    imageStyle.setImageResource(R.drawable.def);
                } else if (spinner.getSelectedItem().toString().equals("Classic")) {
                    imageStyle.setImageResource(R.drawable.classic);
                } else if (spinner.getSelectedItem().toString().equals("Simple")) {
                    imageStyle.setImageResource(R.drawable.simp);
                } else if (spinner.getSelectedItem().toString().equals("Business")) {
                    imageStyle.setImageResource(R.drawable.buss);
                } else if (spinner.getSelectedItem().toString().equals("Academese")) {
                    imageStyle.setImageResource(R.drawable.acad);
                } else if (spinner.getSelectedItem().toString().equals("Comic")) {
                    imageStyle.setImageResource(R.drawable.acad);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

    private void addListenerOnButtonCreateMindMap() {
        buttonCreateMindMap = (Button) findViewById(R.id.buttonCreateMindMap);
        buttonCreateMindMap.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                DrawView.LUheight = 0;
                DrawView.LDHehight = 0;
                DrawView.RUheight = 0;
                DrawView.RDHehight = 0;
                Intent intent = new Intent(WelcomeScreen.this, MainActivity.class);
                Spinner spinner = (Spinner) findViewById(R.id.spinnerStyles);
                String style = (String) spinner.getSelectedItem();
                intent.putExtra(STYLE, style);
                if (MainActivity.root != null) {
                    MainActivity.root.clear();
                }
                MainActivity.root = null;
                startActivity(intent);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
