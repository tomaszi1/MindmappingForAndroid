package edu.agh.klaukold.gui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.xmind.core.internal.Workbook;

import java.io.File;
import java.util.Properties;

import edu.agh.App;
import edu.agh.R;
import edu.agh.idziak.DropboxBrowserActivity;
import edu.agh.idziak.DropboxSaverActivity;
import edu.agh.idziak.FileBrowserActivity;
import edu.agh.idziak.FileSaverActivity;
import edu.agh.idziak.dropbox.DbxBrowser;
import edu.agh.idziak.dropbox.DropboxHandler;
import edu.agh.idziak.dropbox.DropboxWorkbookManager;
import edu.agh.idziak.dropbox.ResultListener;
import edu.agh.idziak.local.LocalWorkbookManager;
import edu.agh.klaukold.commands.EditSheet;


public class EditSheetScreen extends Activity {

    public static TextView backgroud;
    public static View backdroundColorEditScreen;
    public static int COLOR;
    public static String ACTIVITY_TYPE = "EDIT_SHEET";
    public static final int REQUEST_FILE = 1;
    private ProgressDialog progressDialog;
    private DropboxHandler dropboxHandler;
    private String source = null;
    public static Workbook workbook;
    private DropboxWorkbookManager dropboxWorkbookManager;


    @Override
    public void onResume() {
        super.onResume();
        ((GradientDrawable) backdroundColorEditScreen.getBackground()).setColor(COLOR);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_screen);
        final Intent intent = getIntent();
        COLOR = intent.getIntExtra(MainActivity.BACKGROUNDCOLOR, 1);
        //  int inten = intent.getIntExtra(MainActivity.INTENSIVITY, 0);
//        intensivity = (SeekBar) findViewById(R.id.seekBarIntensivity);
//        intensivity.setProgress((inten));
        backgroud = (TextView) findViewById(R.id.textViewBackgroundColor);
        backdroundColorEditScreen = (View) findViewById(R.id.sheet_color);
        ((GradientDrawable) backdroundColorEditScreen.getBackground()).setColor(COLOR);
        backdroundColorEditScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditSheetScreen.this, ColorPalette.class);
                intent.putExtra("ACTIVITY", ACTIVITY_TYPE);
                startActivity(intent);
            }
        });
        ImageButton b = (ImageButton) findViewById(R.id.imageButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                source = "file";
                Intent intent1 = new Intent(EditSheetScreen.this, FileSaverActivity.class);
                startActivityForResult(intent1, REQUEST_FILE);

            }
        });
        ImageButton b2 = (ImageButton) findViewById(R.id.imageButton3);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                source = "dropbox";
                dropboxHandler.linkAccount(EditSheetScreen.this);
                Intent intent1 = new Intent(EditSheetScreen.this, DropboxSaverActivity.class);
                startActivityForResult(intent1, REQUEST_FILE);
            }
        });
        dropboxHandler = ((App) getApplicationContext()).getDbxHandler();
        progressDialog = new ProgressDialog(EditSheetScreen.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_FILE) {
            if (resultCode == RESULT_OK) {
                File file = (File) data.getExtras().get(FileSaverActivity.FILE);
                if (source.equals("file")) {
                    LocalWorkbookManager.saveWorkbook(file, MainActivity.workbook, new ResultListener() {

                        @Override
                        public void taskDone(Object result) {
                            showToast("File saved.");

                            finish();
                        }

                        @Override
                        public void taskFailed(Exception exception) {
                            showToast("Failure.");
                        }
                    });
                } else {
                    DbxBrowser.DbxFile file1 = (DbxBrowser.DbxFile) data.getExtras().get(DropboxSaverActivity.FILE_TO_SAVE);
                    dropboxWorkbookManager =  DropboxWorkbookManager.bindWorkbookToDropboxFile(MainActivity.workbook, file1, dropboxHandler);
                    saveWorkbook();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private ResultListener<DropboxWorkbookManager, Exception> loadFileListener = new ResultListener<DropboxWorkbookManager, Exception>() {
        @Override
        public void taskDone(DropboxWorkbookManager result) {
            dropboxWorkbookManager = result;
            progressDialog.dismiss();
            showToast("Skoroszyt wczytany");
        }

        @Override
        public void taskFailed(Exception exception) {
            showToast("Nieudane pobranie");
            Log.e("XXXXX", exception.getMessage());
        }
    };

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    public void saveWorkbook() {
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


}
