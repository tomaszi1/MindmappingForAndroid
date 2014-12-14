package edu.agh.idziak;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.client2.exception.DropboxException;

import java.util.ArrayList;

import edu.agh.App;
import edu.agh.R;
import edu.agh.idziak.dropbox.DbxBrowser;
import edu.agh.idziak.dropbox.DropboxHandler;
import edu.agh.idziak.dropbox.ResultListener;

public class DropboxSaverActivity extends Activity {

    public static final String FILE_TO_SAVE = "file_to_save";
    private ListView listView;
    private FileArrayAdapter fileArrayAdapter;
    private DbxBrowser browser;
    private DropboxHandler dbxHandler;
    private ProgressDialog progressDialog;
    private EditText fileNameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dropbox_saver);

        dbxHandler = ((App) getApplicationContext()).getDbxHandler();
        browser = new DbxBrowser(dbxHandler);

        listView = (ListView) findViewById(R.id.files_list_view);
        fileArrayAdapter = new FileArrayAdapter(this);
        fileNameEditText = (EditText) findViewById(R.id.file_name_edit_text);

        listView.setAdapter(fileArrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DbxBrowser.DbxFile file = (DbxBrowser.DbxFile) adapterView.getItemAtPosition(i);
                if (file.isDir()) {
                    progressDialog.show();
                    browser.changeDir(file, listFolderListener);
                } else {
                    showDialog(file);
                }
            }
        });

        progressDialog = ProgressDialog.show(this, "Loading", "Wait...", true, false);
        browser.goToRootDir(listFolderListener);
    }

    private void showDialog(final DbxBrowser.DbxFile file) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Do you want to overwrite file?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent result = new Intent().putExtra(FILE_TO_SAVE, file);
                setResult(RESULT_OK, result);
                dialogInterface.dismiss();
                finish();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    public void saveFileButtonClicked(View view) {
        String fileName = fileNameEditText.getText().toString();
        if (fileName == null || fileName.isEmpty()) {
            Toast.makeText(DropboxSaverActivity.this, "File name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!fileName.endsWith(".xmind"))
            fileName = fileName + ".xmind";
        DbxBrowser.DbxFile newFile = browser.createNewFile(fileName);
        Intent result = new Intent().putExtra(FILE_TO_SAVE, newFile);
        setResult(RESULT_OK, result);
        finish();
    }

    private ResultListener<DbxBrowser.DbxFile, DropboxException> listFolderListener = new ResultListener<DbxBrowser.DbxFile, DropboxException>() {
        @Override
        public void taskDone(DbxBrowser.DbxFile result) {
            fileArrayAdapter.listCurrentFolder();
            progressDialog.dismiss();
        }

        @Override
        public void taskFailed(DropboxException exception) {
            progressDialog.dismiss();
            Toast.makeText(DropboxSaverActivity.this, "Loading directory failed", Toast.LENGTH_SHORT).show();
        }
    };

    private class FileArrayAdapter extends ArrayAdapter<DbxBrowser.DbxFile> {
        public FileArrayAdapter(Context context) {
            super(context, android.R.layout.simple_list_item_1, new ArrayList<DbxBrowser.DbxFile>());
        }

        public void listCurrentFolder() {
            clear();
            DbxBrowser.DbxFile parent = browser.getParentDir();
            if (parent != null)
                add(parent);
            addAll(browser.getCurrentDir().getContents());
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            DbxBrowser.DbxFile file = getItem(position);
            View view = LayoutInflater.from(this.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setText(file.getName());
            return view;
        }
    }
}
