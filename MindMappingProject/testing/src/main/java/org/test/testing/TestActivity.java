package org.test.testing;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.dropbox.client2.exception.DropboxException;

import idziak.dropbox.DbxBrowser;
import idziak.dropbox.DropboxHandler;
import idziak.dropbox.DropboxWorkbookManager;
import idziak.dropbox.ResultListener;


public class TestActivity extends Activity {
    public static final int REQUEST_FILE = 1;
    private ProgressDialog progressDialog;
    private DropboxHandler dropboxHandler;
    private DropboxWorkbookManager dropboxWorkbookManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        dropboxHandler = ((App) getApplicationContext()).getDbxHandler();
    }

    @Override
    protected void onResume() {
        super.onResume();
        dropboxHandler.onResume();
    }

    public void browseFiles(View view) {
        Intent browserIntent = new Intent(this, DropboxBrowserActivity.class);
        startActivityForResult(browserIntent, REQUEST_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_FILE) {
            if (resultCode == RESULT_OK) {
                progressDialog = ProgressDialog.show(this, "Pobieranie", "Chwila...", true, false);
                DbxBrowser.DbxFile file = (DbxBrowser.DbxFile) data.getExtras().get(DropboxBrowserActivity.SELECTED_FILE);
                DropboxWorkbookManager.downloadWorkbook(file, loadFileListener, dropboxHandler);
            } else {
                showToast("Anulowano");
            }
        }
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
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
                if(result)
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
