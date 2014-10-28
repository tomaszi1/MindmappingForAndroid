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
import edu.agh.klaukold.utilities.Utils;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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

import org.xmind.core.IWorkbook;
import org.xmind.core.internal.Workbook;

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


//
//		Button btn = (Button) findViewById(R.id.welcomeNewBtn);
//		btn.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				final Dialog dialog = DialogFactory.newMapDialog(WelcomeScreen.this);
//				Button btn = (Button) dialog.findViewById(R.id.saveFile);
//				final EditText et = (EditText) dialog.findViewById(R.id.newMapEditText);
//				final Intent intent = new Intent(WelcomeScreen.this, MainActivity.class);
//
//				btn.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						if(!et.getText().toString().isEmpty()) {
//							dialog.dismiss();
//							intent.putExtra("filename", et.getText().toString());
//							startActivity(intent);
//						}
//					}
//				});
//
//				dialog.show();
//			}
//		});
//
//		btn = (Button) findViewById(R.id.welcomeReadBtn);
//		btn.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				final Dialog dialog = DialogFactory.readMapDialog(WelcomeScreen.this);
//				final ListView listview = (ListView) dialog.findViewById(R.id.listview);
//				final Intent intent = new Intent(WelcomeScreen.this, MainActivity.class);
//
//				//listview.setAdapter(new ArrayAdapter<String>(WelcomeScreen.this,
//				//		android.R.layout.simple_list_item_1 , Utils.db.getMaps()));
//
//				listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//					@Override
//					public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
//						final String item = (String) parent.getItemAtPosition(position);
//						dialog.dismiss();
//						intent.putExtra("filename", item);
//						intent.putExtra("present", true);
//						startActivity(intent);
//					}
//
//				});
//
//				dialog.show();
//			}
//		});
//
//		btn = (Button) findViewById(R.id.welcomeDeleteBtn);
//		btn.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				final Dialog dialog = DialogFactory.deleteMapDialog(WelcomeScreen.this);
//				final ListView listview = (ListView) dialog.findViewById(R.id.listview);
//
//			//	listview.setAdapter(new ArrayAdapter<String>(WelcomeScreen.this,
//				//		android.R.layout.simple_list_item_1 , Utils.db.getMaps()));
//
//				listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//					@Override
//					public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
//						final String item = (String) parent.getItemAtPosition(position);
//						dialog.dismiss();
//						//Utils.db.deleteAll(item);
//					}
//				});
//
//				dialog.show();
//			}
//		});
//
//		btn = (Button) findViewById(R.id.welcomeImportBtn);
//		btn.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				final Intent intent = new Intent(WelcomeScreen.this, MainActivity.class);
//				intent.putExtra("import", true);
//				startActivity(intent);
//			}
//		});
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
                        MainActivity.workbook = (Workbook) result;
                        Intent intent = new Intent(WelcomeScreen.this, MainActivity.class);
                        String style = "ReadyMap";
                        intent.putExtra(STYLE, style);
                      //  intent.putExtra("workbook", ((Workbook) result));
                        if (MainActivity.root != null) {
                            MainActivity.root.clear();
                        }
                        MainActivity.root = null;
                        startActivity(intent);
                    }

                    @Override
                    public void taskFailed(Exception exception) {

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
