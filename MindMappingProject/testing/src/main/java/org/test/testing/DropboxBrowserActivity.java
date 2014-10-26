package org.test.testing;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.client2.exception.DropboxException;

import java.util.ArrayList;

import idziak.dropbox.DbxBrowser;
import idziak.dropbox.DbxBrowser.DbxFile;
import idziak.dropbox.DropboxHandler;
import idziak.dropbox.ResultListener;


public class DropboxBrowserActivity extends Activity {

    public static final String SELECTED_FILE = "selected_file";
    private ListView listView;
    private FileArrayAdapter fileArrayAdapter;
    private DbxBrowser browser;
    private DropboxHandler dbxHandler;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dropbox_browser);

        dbxHandler = ((App) getApplicationContext()).getDbxHandler();
        browser = new DbxBrowser(dbxHandler);

        listView = (ListView) findViewById(R.id.files_list_view);
        fileArrayAdapter = new FileArrayAdapter(this);

        listView.setAdapter(fileArrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DbxFile file = (DbxFile)adapterView.getItemAtPosition(i);
                if(file.isDir()){
                    progressDialog.show();
                    browser.changeDir(file,listFolderListener);
                }else{
                    Intent result = new Intent().putExtra(SELECTED_FILE, file);
                    setResult(RESULT_OK, result);
                    finish();
                }
            }
        });

        progressDialog = ProgressDialog.show(this, "Wczytywanie", "Chwila...", true, false);
        browser.goToRootDir(listFolderListener);
    }

    private ResultListener<DbxFile,DropboxException> listFolderListener = new ResultListener<DbxFile, DropboxException>() {
        @Override
        public void taskDone(DbxFile result) {
            fileArrayAdapter.listCurrentFolder();
            progressDialog.dismiss();
        }

        @Override
        public void taskFailed(DropboxException exception) {
            progressDialog.dismiss();
            Toast.makeText(DropboxBrowserActivity.this, "Nieudane pobranie folderu", Toast.LENGTH_SHORT).show();
        }
    };

    public void newFolder(View view) {
        progressDialog.setTitle("Tworzenie folderu");
        progressDialog.show();
        browser.createNewDir("Nowy folder", new ResultListener<DbxFile, DropboxException>() {
            @Override
            public void taskDone(DbxFile result) {
                fileArrayAdapter.listCurrentFolder();
                progressDialog.dismiss();
            }

            @Override
            public void taskFailed(DropboxException exception) {
                progressDialog.dismiss();
                Toast.makeText(DropboxBrowserActivity.this, "Nieudane utworzenie folderu", Toast.LENGTH_SHORT).show();
                Log.i(getClass().getSimpleName(), "Błąd tworzenia folderu", exception);
            }
        });
    }

    private class FileArrayAdapter extends ArrayAdapter<DbxFile> {
        public FileArrayAdapter(Context context) {
            super(context, android.R.layout.simple_list_item_1, new ArrayList<DbxFile>());
        }

        public void listCurrentFolder() {
            clear();
            DbxFile parent = browser.getParentDir();
            if(parent!=null)
                add(parent);
            addAll(browser.getCurrentDir().getContents());
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            DbxFile file = getItem(position);
            View view = LayoutInflater.from(this.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setText(file.getName());
            return view;
        }
    }

}
