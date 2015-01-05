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

import edu.agh.App;
import edu.agh.R;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.agh.idziak.DropboxBrowserActivity;
import edu.agh.idziak.FileBrowserActivity;
import edu.agh.idziak.dropbox.DbxBrowser;
import edu.agh.idziak.dropbox.DropboxHandler;
import edu.agh.idziak.dropbox.DropboxWorkbookManager;
import edu.agh.idziak.dropbox.ResultListener;
import edu.agh.idziak.local.LocalWorkbookManager;
import edu.agh.klaukold.common.Box;
import edu.agh.klaukold.utilities.AsyncInvalidate;
import edu.agh.klaukold.utilities.Callback;
import edu.agh.klaukold.utilities.Utils;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.dropbox.client2.exception.DropboxException;


import org.xmind.core.internal.Workbook;


import java.io.File;
import java.util.ArrayList;
import java.util.WeakHashMap;

public class WelcomeScreen extends Activity {
    private Spinner styles;
    private DropboxWorkbookManager dropboxWorkbookManager;

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

    private Menu menu;
    private Button buttonCreateMindMap;
    private ImageView imageStyle;
    public final static String STYLE = "WELCOME_SCREEN_STYLE";
    public static final int REQUEST_FILE = 1;
    private ProgressDialog progressDialog;
    private DropboxHandler dropboxHandler;
    private String source = null;
    public static Workbook workbook;

    @Override
    public void onResume() {
        super.onResume();
        dropboxHandler.onResume();
        DrawingThread.LUheight = 0;
        DrawingThread.LDHehight = 0;
        DrawingThread.RUheight = 0;
        DrawingThread.RDHehight = 0;
//		//Utils.initDB(this);
        Utils.context = this;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        dropboxHandler = ((App) getApplicationContext()).getDbxHandler();

    }


    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.welcome_s, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.local_disc:
                Intent intent1 = new Intent(WelcomeScreen.this, FileBrowserActivity.class);
                startActivityForResult(intent1, REQUEST_FILE);
                source = "file";
                return false;
            case R.id.dropbox:
                if(!dropboxHandler.isLinked()){
                    dropboxHandler.linkAccount(this);
                }else{
                    Intent browserIntent = new Intent(this, DropboxBrowserActivity.class);
                    startActivityForResult(browserIntent, REQUEST_FILE);
                    source = "dropbox";
                }
                return  false;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void browseFiles(View view) {
        Intent browserIntent = new Intent(this, DropboxBrowserActivity.class);
        startActivityForResult(browserIntent, REQUEST_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_FILE) {
            if (resultCode == RESULT_OK) {
                if (source.equals("file")) {
                    progressDialog = ProgressDialog.show(this, "Loading", "Please wait...", true, false);
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
                            MainActivity.root = null;
                            MainActivity.workbook = null;
                            final Intent intent = new Intent(WelcomeScreen.this, MainActivity.class);
                            String style = "ReadyMap";
                            intent.putExtra(STYLE, style);
                            workbook = (Workbook) result;
                            startActivity(intent);

                        }

                        @Override
                        public void taskFailed(Exception exception) {
                            showToast("Incorrect file type.");
                            progressDialog.cancel();
                        }
                    });
                } else if (source.equals("dropbox")) {
                        progressDialog = ProgressDialog.show(this, "Loading", "Please wait...", true, false);
                        DbxBrowser.DbxFile file = (DbxBrowser.DbxFile) data.getExtras().get(DropboxBrowserActivity.SELECTED_FILE);
                        DropboxWorkbookManager.downloadWorkbook(file, loadFileListener, dropboxHandler);
                }
            } else {
                showToast("Cancel");
            }
        }
    }

    private ResultListener<DropboxWorkbookManager, Exception> loadFileListener = new ResultListener<DropboxWorkbookManager, Exception>() {
        @Override
        public void taskDone(DropboxWorkbookManager result) {
            dropboxWorkbookManager = result;
            progressDialog.dismiss();
            showToast("File loaded.");
            DrawView.LUheight = 0;
            DrawView.LDHehight = 0;
            DrawView.RUheight = 0;
            DrawView.RDHehight = 0;
            MainActivity.root = null;
            MainActivity.workbook = null;
            final Intent intent = new Intent(WelcomeScreen.this, MainActivity.class);
            String style = "ReadyMap";
            intent.putExtra(STYLE, style);
            workbook = (Workbook) dropboxWorkbookManager.getWorkbook();
            startActivity(intent);

        }

        @Override
        public void taskFailed(Exception exception) {
            showToast("Loading Fail");
            Log.e("XXXXX", exception.getMessage());
        }
    };

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
                MainActivity.style = null;
                Intent intent = new Intent(WelcomeScreen.this, MainActivity.class);
                Spinner spinner = (Spinner) findViewById(R.id.spinnerStyles);
                String style = (String) spinner.getSelectedItem();
                intent.putExtra(STYLE, style);
                MainActivity.workbook = null;
                MainActivity.root = null;
                workbook = null;
                startActivity(intent);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void saveWorkbook(View view) {
        if (dropboxWorkbookManager == null) {
            showToast("Brak skoroszytu");
            return;
        }
        progressDialog.setTitle("Zapisywanie");
        progressDialog.show();
        dropboxWorkbookManager.uploadWithOverwrite(new ResultListener<Void, Exception>() {
            @Override
            public void taskDone(Void nothing) {
                progressDialog.dismiss();
                showToast("Skoroszyt zapisany");
            }

            @Override
            public void taskFailed(Exception exception) {
                showToast("Nieudane zapisanie pliku");
                Log.e("XXXXX", exception.getMessage());
            }
        });
    }

    public void checkNewVersion(View view) {
        if (dropboxWorkbookManager == null) {
            showToast("Brak  skoroszytu");
            return;
        }
        progressDialog.setTitle("Sprawdzanie");
        progressDialog.show();
        dropboxWorkbookManager.checkForNewVersion(new ResultListener<Boolean, DropboxException>() {
            @Override
            public void taskDone(Boolean result) {
                progressDialog.dismiss();
                if (result)
                    showToast("Jest dostępna nowa wersja pliku w chmurze");
                else
                    showToast("Aktualna wersja jest aktualna");
            }

            @Override
            public void taskFailed(DropboxException exception) {
                showToast("Nieudane sprawdzenie wersji");
            }
        });
    }


    public void linkToDropbox(View view) {
        dropboxHandler.linkAccount(this);
    }
}
